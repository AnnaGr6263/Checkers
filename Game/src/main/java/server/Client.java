package server;

import java.net.Socket;
import java.io.*;
import java.net.UnknownHostException;

// Klasa Client: reprezentuje uczestnika komunikacji w systemie.
// Uczestnik komunikuje się z serwerem poprzez swojego dedykowanego Mediatora.

public class Client {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 4444); // Połączenie z serwerem na porcie 4444

            // Inicjalizacja strumienia wyjściowego do serwera
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Inicjalizacja strumienia wejściowego z serwera
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Wątek odbierający wiadomości z serwera
            Thread receiverThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println("From server: " + serverMessage);
                        // Sprawdzenie czy połączenie zostało odrzucone
                        if (serverMessage.contains("Connection rejected")) {
                            System.exit(0); // Zakończenie aplikacji
                        }
                        System.out.println("Enter text: "); // Komunikat dla użytkownika
                    }
                } catch (IOException ex) {
                    System.out.println("Connection closed by server.");
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.out.println("Error closing socket: " + e.getMessage());
                    }
                    System.exit(0);
                }
            });
            receiverThread.start();

            // Wczytywanie danych od użytkownika
            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
            String text;

            do {
                System.out.println("Enter text: ");
                text = bufferRead.readLine(); // Pobranie tekstu z konsoli
                out.println(text); // Wysłanie tekstu do serwera
            } while (!text.equals("bye")); // Zakończenie, gdy użytkownik wpisze "bye"

            socket.close(); // Zamknięcie połączenia
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
