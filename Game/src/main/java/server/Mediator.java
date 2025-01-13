package server;

import server.manager.GameManager;
import java.io.*;
import java.net.Socket;

// Klasa Mediator: odpowiada za połączenie z jednym klientem.
// Przesyła wiadomości od klienta do serwera i odwrotnie.

public class Mediator extends Thread implements Observer {
    private final Socket socket; // Połączenie z klientem
    private PrintWriter out; // Strumień wyjściowy do klienta
    private final GameManager gameManager = GameManager.getInstance(); // Menadżer gry

    public Mediator(Socket socket) {
        this.socket = socket;

        if (socket != null) { // Sprawdź, czy socket nie jest null
            try {
                // Inicjalizacja strumienia wyjściowego
                this.out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException ex) {
                System.out.println("Error initializing PrintWriter: " + ex.getMessage());
            }
        } else {
            System.out.println("Socket is null. Output stream will not be initialized.");
            this.out = null; // Ustaw na null, aby uniknąć dalszych problemów tylko na potrzeby testow
        }

        this.start(); // Uruchomienie wątku
    }


    @Override
    // Odbieranie wiadomości od klienta - watek
    public void run() {
        if (socket == null) {
            System.out.println("Socket is null. Cannot start input stream handling.");
            return; // Wyjście z metody, jeśli socket jest null
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) { // Inicjalizacja strumienia wejściowego
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
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                System.out.println("Error closing socket: " + ex.getMessage());
            }
        } else {
            System.out.println("Socket is already null. No need to close.");
        }
    }
}
