package server;
import server.manager.GameManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

// Klasa Server: zarządza połączeniami klientów i odpowiada za komunikację między nimi.

public class Server {
    private static final int MAX_CLIENTS = 6; // Maksymalna liczba klientów
    private static final AtomicInteger connectedClients = new AtomicInteger(0); // Licznik klientów
    private static final GameManager gameManager = GameManager.getInstance();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(4444)) {
            System.out.println("Server is listening on port 4444");

            while (true) {
                Socket socket = serverSocket.accept();
                if (connectedClients.get() >= MAX_CLIENTS) {
                    rejectClient(socket); // Odrzucenie nowego połączenia, jeśli osiągnięto limit klientów
                    continue;
                }

                // Połączenie z nowym klientem
                connectedClients.incrementAndGet();
                // Utworzenie nowego mediatora dla klienta
                Mediator mediator = new Mediator(socket);
                System.out.println("New client connected. Total clients: " + connectedClients.get());
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
        }
    }

    private static void rejectClient(Socket socket) {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) { // Inicjalizacja strumienia wyjściowego
            out.println("Connection rejected: Server is full.");
        } catch (IOException ex) {
            System.out.println("Error rejecting client: " + ex.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                System.out.println("Error closing rejected client socket: " + ex.getMessage());
            }
        }
    }

    // Metoda wywoływana po rozłączeniu klienta
    public static void clientDisconnected() {
        connectedClients.decrementAndGet(); // Zmniejszenie licznika klientów
        System.out.println("A client disconnected. Connected clients: " + connectedClients.get());
    }
}



