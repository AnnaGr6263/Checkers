package server.manager;

import board.Field;
import board.enums.PieceColor;

import java.util.List;
import java.util.Map;

/**
 * Klasa VictoryManager odpowiadająca za obsługę wygranej w grze.
 */
public class VictoryManager {

    private final Map<PieceColor, List<Field>> destinationHomesMap;
    private final int numberOfPlayers;
    private int whichPlace = 0;        // Licznik graczy, którzy ukończyli grę, bo wprowadzili wszystkie swoje pionki

    /**
     * Konstruktor.
     *
     * @param destinationHomesMap Hash mapa zawierająca kolor pionka i przypisaną mu listę pól docelowych.
     * @param numberOfPlayers Liczba graczy w aktualnie prowadzonej rozgrywce.
     */
    public VictoryManager(Map<PieceColor, List<Field>> destinationHomesMap, int numberOfPlayers) {
        this.destinationHomesMap = destinationHomesMap;
        this.numberOfPlayers = numberOfPlayers;
    }

    /**
     * Metoda sprawdzająca wygraną.
     *
     * @param pieceColor Kolor pionków gracza.
     * @return Prawdę jeśli gracz wygrał i fałsz jeśli jeszcze to nie nastąpiło.
     */
    public boolean checkVictory(PieceColor pieceColor) {

        if (pieceColor == null) {
            throw new IllegalArgumentException("Piece color is null. Cannot check victory.");
        }

        List<Field> destinationFields = destinationHomesMap.get(pieceColor);    // Pobieramy listę pól, na które musi dostać się pionek
        if (destinationFields == null || destinationFields.isEmpty()) {
            System.out.println("Error: No destination fields defined for color: " + pieceColor);
            throw new IllegalArgumentException("No destination fields defined for color: " + pieceColor);
        }

        for (Field field : destinationFields) {
            if (field.getPiece() == null || field.getPiece().getColor() != pieceColor) {    // Pole puste lub zajęte
                return false;
            }
        }
        return true; // Wszystkie pola są zajęte przez pionki danego koloru, więc mamy wygraną
    }

    /**
     * Kontroluje liczbę, graczy, którzy zakończyli rozgrywkę.
     *
     * @return Które miejsce zajął gracz.
     */
    public int whichPlace() {
        whichPlace++;
        return whichPlace;
    }

    /**
     * Sprawdza czy gra się zakończyła.
     *
     * @return Prawdę gdy gra sie skończyła.
     */
    public boolean isEnd() {                // Koniec jest wtedy kiedy jakiś gracz zostałby już sam
        return (whichPlace == numberOfPlayers - 1);
    }
}
