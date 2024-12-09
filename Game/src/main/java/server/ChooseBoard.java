package server;

import board.BigBoard;
import board.BoardSetup;

import java.util.Scanner;

// Zastosowanie wzorca Singleton (możemy utworzyć tylko jedną instancję tej klasy dzięki czemu plansza jest ustawiana tylko raz)
public class ChooseBoard {
    private static ChooseBoard instance;
    private BoardSetup board;

    private ChooseBoard() {}        // Prywatny konstruktor

    public static ChooseBoard getInstance() {
        if (instance == null) {     // Jeśli instancja jeszcze nie istnieje (pierwsza próba utworzenia) to utwórz ją
            instance = new ChooseBoard();
        }
        return instance;    // Zwróć jedyną instancję
    }

    public BoardSetup choice() {
        while(board == null) {          // Aż plansza nie zostanie wybrana
            Scanner scanner = new Scanner(System.in);

            try {
                System.out.println("Which board you choose: ");
                System.out.println("1. BigBoard (16x24)");
                System.out.print("Enter a number: ");

                int choice = 0;
                choice = scanner.nextInt(); // Odczytujemy liczbę od użytkownika

                // Łatwe rozszerzenie gry (np. zaimplementowanie obsługi innych plansz i tutaj dokananie wyboru)
                switch (choice) {
                    case 1:
                        System.out.println("You selected BigBoard (16x24).");
                        board = new BigBoard();     // Konkretna plansza (BigBoard dziedziczy po BoardSetup)
                        break;
                    default:
                        System.out.print("Invalid choice. Please choose a valid option. ");
                        board = null;
                        break;
                }
            } catch (Exception e) {
                System.out.print("Invalid input. Please enter a valid number. ");
            }
        }
        return board;
    }
}
