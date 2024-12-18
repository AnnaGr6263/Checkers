package server;

import board.BigBoard;
import board.BoardSetup;

// Zastosowanie wzorca Singleton (możemy utworzyć tylko jedną instancję tej klasy dzięki czemu plansza jest ustawiana tylko raz)
public class ChooseBoard {

    private static ChooseBoard instance = null;
    private BoardSetup board = null;

    private ChooseBoard() {}        // Prywatny konstruktor

    public static ChooseBoard getInstance() {
        if (instance == null) {     // Jeśli instancja jeszcze nie istnieje (pierwsza próba utworzenia) to utwórz ją
            instance = new ChooseBoard();
        }
        return instance;    // Zwróć jedyną instancję
    }

    // Wybór planszy
    public BoardSetup choose(int choice) {
        switch (choice) {
            case 1:
                System.out.println("You selected BigBoard (16x24).");
                board = new BigBoard(); // Tworzenie planszy BigBoard
                board.boardGenerator();
                break;
            default:
                System.out.println("Invalid choice. Please select again.");
                break;
        }
        return board;
    }

    public boolean isBoardChosen() {
        return board != null; // Sprawdzenie, czy plansza została wybrana
    }

    public BoardSetup getBoard() {
        return board;
    }
}