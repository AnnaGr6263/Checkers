package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

// Klasa Server: zarządza połączeniami klientów i odpowiada za komunikację między nimi.

public class Server {
    private static final int MAX_CLIENTS = 6; // Maksymalna liczba klientów
    private static final AtomicInteger connectedClients = new AtomicInteger(0); // Liczba aktywnych klientów
    private static final List<Mediator> mediators = new CopyOnWriteArrayList<>(); // Lista mediatorów obsługujących klientów

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(4444)) {
            System.out.println("Server is listening on port 4444");

            while (true) {
                Socket socket = serverSocket.accept();
                if (connectedClients.get() >= MAX_CLIENTS) {
                    rejectClient(socket); // Odrzucenie nowego połączenia, jeśli osiągnięto limit klientów
                    continue;
                }

                connectedClients.incrementAndGet();
                System.out.println("New client connected. Total clients: " + connectedClients.get());

                Mediator mediator = new Mediator(socket); // Tworzenie mediatora dla nowego klienta
                mediators.add(mediator); // Dodanie mediatora do listy
                mediator.start(); // Rozpoczęcie obsługi klienta
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
        }
    }

    public static void broadcastMessage(String message, Mediator sender) {
        // Rozsyłanie wiadomości do wszystkich mediatorów z wyjątkiem nadawcy
        for (Mediator mediator : mediators) {
            if (mediator != sender) {
                mediator.sendMessage(message);
            }
        }
    }

    public static void removeMediator(Mediator mediator) {
        mediators.remove(mediator); // Usunięcie mediatora z listy
        int count = connectedClients.decrementAndGet(); // Zmniejszenie liczby klientów
        System.out.println("Client disconnected. Total clients: " + count);
    }

    private static void rejectClient(Socket socket) {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("Server is full. Connection rejected."); // Informacja dla odrzuconego klienta
        } catch (IOException ex) {
            System.out.println("Error rejecting client: " + ex.getMessage());
        } finally {
            try {
                socket.close(); // Zamknięcie połączenia odrzuconego klienta
            } catch (IOException ex) {
                System.out.println("Error closing rejected client socket: " + ex.getMessage());
            }
        }
    }
}
