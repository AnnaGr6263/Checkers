package server;

import java.io.*;
import java.net.Socket;

// Klasa Mediator: odpowiada za połączenie z jednym klientem.
// Przesyła wiadomości od klienta do serwera i odwrotnie.

public class Mediator extends Thread {
    private final Socket socket; // Połączenie z klientem
    private PrintWriter out; // Strumień wyjściowy do klienta

    public Mediator(Socket socket) {
        this.socket = socket;
        try {
            // Inicjalizacja strumienia wyjściowego
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
            System.out.println("Error initializing PrintWriter: " + ex.getMessage());
        }
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message;
            // Odbieranie wiadomości od klienta
            while ((message = in.readLine()) != null) {
                System.out.println("Received: " + message);
                Server.broadcastMessage(message, this); // Przekazywanie wiadomości do serwera
            }
        } catch (IOException ex) {
            System.out.println("Client disconnected: " + ex.getMessage());
        } finally {
            Server.removeMediator(this); // Usunięcie mediatora po rozłączeniu klienta
            closeSocket();
        }
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
