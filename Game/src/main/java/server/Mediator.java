package server;

import java.io.*;
import java.net.Socket;

// Klasa Mediator: odpowiada za połączenie z jednym klientem.
// Przesyła wiadomości od klienta do serwera i odwrotnie.

public class Mediator extends Thread implements Observer {
    private final Socket socket; // Połączenie z klientem
    private PrintWriter out; // Strumień wyjściowy do klienta
    private final GameManager gameManager; // Menadżer gry

    public Mediator(Socket socket, GameManager gameManager) {
        this.socket = socket;
        this.gameManager = gameManager;
        try {
            // Inicjalizacja strumienia wyjściowego
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
            System.out.println("Error initializing PrintWriter: " + ex.getMessage());
        }
        this.start();
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("bye")) {
                    sendMessage("Goodbye! Disconnecting from the server.");
                    break; // Zakończ pętlę, aby rozłączyć klienta
                }
                gameManager.handleCommand(this, message);
            }
        } catch (IOException ex) {
            System.out.println("Client disconnected: " + ex.getMessage());
        } finally {
            Server.clientDisconnected();
            closeSocket();
        }
    }

    @Override
    public void update(String message) {
        sendMessage(message); // Otrzymywanie powiadomień od GameManager
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message); // Wysyłanie wiadomości do klienta
        }
    }

    private void closeSocket() {
        try {
            socket.close(); // Zamknięcie połączenia z klientem
        } catch (IOException ex) {
            System.out.println("Error closing socket: " + ex.getMessage());
        }
    }
}
