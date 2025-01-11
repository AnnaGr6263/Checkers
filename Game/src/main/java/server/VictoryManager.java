package server;

import board.Field;
import board.enums.PieceColor;

import java.util.List;
import java.util.Map;

public class VictoryManager {

    private final Map<PieceColor, List<Field>> destinationHomesMap;
    private final int numberOfPlayers;
    private int whichPlace = 0;        // Licznik graczy, którzy ukończyli grę, bo wprowadzili wszystkie swoje pionki

    public VictoryManager(Map<PieceColor, List<Field>> destinationHomesMap, int numberOfPlayers) {
        this.destinationHomesMap = destinationHomesMap;
        this.numberOfPlayers = numberOfPlayers;
    }

    public boolean checkVictory(PieceColor pieceColor) {
        List<Field> destinationFields = destinationHomesMap.get(pieceColor);    // Pobieramy listę pól, na które musi dostać się pionek
        if (destinationFields == null || destinationFields.isEmpty()) {
            throw new IllegalArgumentException("No destination fields defined for color: " + pieceColor);
        }

        for (Field field : destinationFields) {
            if (field.getPiece() == null || field.getPiece().getColor() != pieceColor) {    // Pole puste lub zajęte
                return false;
            }
        }
        return true; // Wszystkie pola są zajęte przez pionki danego koloru, więc mamy wygraną
    }

    public int whichPlace() {
        whichPlace++;
        return whichPlace;
    }

    public boolean isEnd() {                // Koniec jest wtedy kiedy jakiś gracz zostałby już sam
        return (whichPlace == numberOfPlayers - 1);
    }
}
