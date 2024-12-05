package server;

import java.util.List;
import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Mediator extends Thread {
    private Socket socket;
    private List<Mediator> mediators;
    private AtomicInteger connectedClients;
    private PrintWriter out;

    public Mediator(Socket socket, List<Mediator> mediators, AtomicInteger connectedClients) {
        this.socket = socket;
        this.mediators = mediators;
        this.connectedClients = connectedClients;

        try {
            OutputStream output = socket.getOutputStream();
            this.out = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Error initializing PrintWriter: " + ex.getMessage());
        }
    }

    public void run() {

        try {
            //Inicjalizacja  odbierania od socketa
            InputStream input = socket.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));

            String line;
            do {
                // Odbieranie od socketa
                line = in.readLine();
                if (line != null) {
                    System.out.println("Received: " + line);
                    broadcastMessage(line);
                }

            } while (!line.equals("bye"));

            socket.close();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }finally {
            connectedClients.decrementAndGet();
            mediators.remove(this);
            System.out.println("Client disconnected. Remaining clients: " + connectedClients.get());
        }
    }

    private void broadcastMessage(String message) {
        for (Mediator mediator : mediators) {
            if (mediator != this) {
                mediator.sendMessage(message);
            }
        }
    }

    private void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
}