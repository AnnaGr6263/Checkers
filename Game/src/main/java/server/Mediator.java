package server;

import java.io.*;
import java.net.*;


public class Mediator extends Thread {
    private Socket socket;

    public Mediator(Socket socket) {
        this.socket = socket;
    }

    public void run() {

        try {
            //Inicjalizacja  odbierania od socketa
            InputStream input = socket.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));

            //Inicjalizacja  wysylania do socketa
            OutputStream output = socket.getOutputStream();
            PrintWriter out = new PrintWriter(output, true);

            String line;
            do {
                // Odbieranie od socketa
                line = in.readLine();
                // Wypisywanie na serwerze
                System.out.println(line);
                // Wysylanie do socketa
                out.println("-> ("+line+")");

            } while (!line.equals("bye"));

            socket.close();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}