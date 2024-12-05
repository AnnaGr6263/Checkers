package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class Server {
    private static final int MAX_CLIENTS = 6;
    private static final AtomicInteger connectedClients = new AtomicInteger(0);
    private static final List<Mediator> mediators = new ArrayList<>();

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(4444)) {

            System.out.println("Server is listening on port 4444");

            while (true) {
                Socket socket = serverSocket.accept();
                if (connectedClients.get() >= MAX_CLIENTS) {
                    System.out.println("Max clients reached. Rejecting new client...");
                    rejectClient(socket);
                    continue;
                }

                connectedClients.incrementAndGet();
                System.out.println("New client connected. Total clients: " + connectedClients.get());

                Mediator mediator = new Mediator(socket, mediators, connectedClients);
                mediators.add(mediator);
                mediator.start();
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void rejectClient(Socket socket) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Server is full. Connection rejected.");
            socket.close();
        } catch (IOException ex) {
            System.out.println("Error rejecting client: " + ex.getMessage());
        }
    }
}
