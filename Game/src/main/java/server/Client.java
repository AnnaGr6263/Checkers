package server;

import java.net.*;
import java.io.*;

public class Client {

    public static void main(String[] args) {

        try  {
            Socket socket = new Socket("localhost", 4444);
            // Inicjalizacja wysylania do serwera
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            // Inicjalizacja  odbierania z serwera
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Watek odbierajacy z serwera
            Thread receiverThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println("From server: " + serverMessage);
                        System.out.println("Enter text: ");
                    }
                } catch (IOException ex) {
                    System.out.println("Connection closed by server.");
                }finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.out.println("Error closing socket: " + e.getMessage());
                    }
                    System.exit(0);
                }
            });
            receiverThread.start();

            // Wpisanie textu przez konsole
            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
            String text;

            do {
                System.out.println("Enter text: ");
                text = bufferRead.readLine();
                // Wysylanie do serwera
                out.println(text);

            } while (!text.equals("bye"));
            socket.close();

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}