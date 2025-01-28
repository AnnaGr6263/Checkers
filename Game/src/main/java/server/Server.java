package server;
import data.repositories.GameRepository;
import data.repositories.MoveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import server.manager.GameManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Klasa Server: zarządza połączeniami klientów i odpowiada za komunikację między nimi.
 */
@SpringBootApplication(scanBasePackages = {"server", "data.repositories", "server.manager"})
@EnableJpaRepositories(basePackages = "data.repositories")
@EntityScan(basePackages = "data.entities")
public class Server implements CommandLineRunner {
    private static final int MAX_CLIENTS = 6; // Maksymalna liczba klientów
    private static final AtomicInteger connectedClients = new AtomicInteger(0); // Licznik klientów

    @Autowired
    private GameManager gameManager;

    /**
     * Metoda main.
     *
     * @param args Argumenty.
     */
    public static void main(String[] args) {

        SpringApplication.run(Server.class, args);

    }
    @Override

    public void run(String... args) throws Exception {

        System.out.println("Server is starting...");
        try (ServerSocket serverSocket = new ServerSocket(4444)) {
            System.out.println("Server is listening on port 4444");

            while (true) {
                Socket socket = serverSocket.accept();
                if (connectedClients.get() >= MAX_CLIENTS) {
                    rejectClient(socket); // Odrzucenie    nowego połączenia, jeśli osiągnięto limit klientów
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

    /**
     * Odrzuca klienta gdy z określonych powodów nie może dołączyć do serwera.
     *
     * @param socket Socket.
     */
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

    /**
     * Metoda wywoływana po rozłączeniu klienta.
     */
    public static void clientDisconnected() {
        connectedClients.decrementAndGet(); // Zmniejszenie licznika klientów
        System.out.println("A client disconnected. Connected clients: " + connectedClients.get());
    }
}



