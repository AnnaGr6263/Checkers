package server;

import java.net.Socket;
import java.io.*;
import java.net.UnknownHostException;

/**
 * Klasa reprezentująca klienta uczestnika komunikacji w systemie.
 * Uczestnik komunikuje się z serwerem poprzez swojego dedykowanego Mediatora.
 */
public class Client {

    /**
     * Metoda main.
     *
     * @param args Argumenty.
     */
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 4444); // Połączenie z serwerem na porcie 4444

            // Inicjalizacja strumienia wyjściowego do serwera (wysylanie)
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Inicjalizacja strumienia wejściowego z serwera (odbieranie)
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Enter \"join\" to join the game, \"choose board BigBoard\" to select a board, and \"game start\" to start the game:");
            System.out.println("Enter \"replay game\" to get a list of games and \"replay game [id]\" to replay the game");
            System.out.println("Then send moves between players. Pattern: move [0,16]x[0,24]->[0,16]x[0,24]");
            System.out.println("Enter \"skip\" to skip the move.");
            System.out.println("Type \"bye\" to exit.");

            // Wątek odbierający wiadomości z serwera
            Thread receiverThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) { // Oczekiwanie na wiadomość od serwera
                        System.out.println(serverMessage); // Wyświetlenie wiadomości od serwera
                        if (serverMessage.contains("Connection rejected")) {
                            System.exit(0); // Zakończenie aplikacji, jeśli odrzucono połączenie
                        }
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
