package server;

import board.BigBoard;
import board.BoardSetup;

/**
 * Klasa, do konstrukcji, której używamy wzorca Singleton.
 * Dzieki temu możemy utworzyć tylko jedną instancję tej klasy, więc plansza jest ustawiana tylko raz w ciągu całej gry.
 */
public class ChooseBoard {

    private static ChooseBoard instance = null;
    private BoardSetup board = null;

    /**
     * Prywatny konstruktor
     */
    private ChooseBoard() {}

    /**
     * Metoda pozwalająca zwrócić jedyną instancję klasy ChooseBoard, poniewać gra toczy się na jednej planszy,
     * której nie można zmienić w trakcie gry.
     *
     * @return Jedyną instancję klasy ChooseBoard.
     */
    public static ChooseBoard getInstance() {
        if (instance == null) {     // Jeśli instancja jeszcze nie istnieje (pierwsza próba utworzenia) to utwórz ją
            instance = new ChooseBoard();
        }
        return instance;    // Zwróć jedyną instancję
    }

    /**
     * Metoda zarządzająca wyborem planszy, na razie mamy tylko jedną planszę do wyboru.
     *
     * @param choice Wybór.
     * @return Odpowiednią dla dokonanego wyboru planszę.
     */
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

    /**
     * Metoda sprawdzająca czy plansza została już wybrana.
     *
     * @return Prawdę jeśli plansza została wybrana i fałsz w przeciwnym wypadku.
     */
    public boolean isBoardChosen() {
        return board != null; // Sprawdzenie, czy plansza została wybrana
    }

    /**
     * Getter wybranej planszy.
     *
     * @return Planszę.
     */
    public BoardSetup getBoard() {
        return board;
    }

    /**
     * Metoda tylko do testów z mockami.
     *
     * @param TestBoard Plansza testowa.
     */
    public void setTestBoard(BoardSetup TestBoard) {
        this.board = TestBoard;
    }
}