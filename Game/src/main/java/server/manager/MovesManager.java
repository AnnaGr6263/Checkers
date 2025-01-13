package server.manager;
import GUI.GUI;
import GUI.GUIMoves;
import board.Field;
import board.Piece;
import server.ChooseBoard;

import java.util.*;

public class MovesManager {

    private Field startField = null;
    private Field endField = null;
    private GUIMoves guiMoves;
    private GameManager gameManager = GameManager.getInstance();

    public MovesManager(Field startField, Field endField) {

        this.startField = startField;
        this.endField = endField;

        // Uzyskaj instancję GUI i zainicjalizuj GUIMoves
        GUI gui = GUI.getInstance();
        this.guiMoves = new GUIMoves();
    }

    public Field getStartField() {
        return startField;
    }

    // Sprawdza, czy można wykonać skok nad pionkiem
    private boolean canJumpOverPiece() {
        // Obliczenie pozycji pola pomiędzy startField a endField
        int midRow = (startField.getRow() + endField.getRow()) / 2;
        int midCol = (startField.getCol() + endField.getCol()) / 2;


        Field midField = ChooseBoard.getInstance().getBoard().getSpecificField(midRow, midCol);
        // Pole pośrednie musi mieć pionek, a końcowe pole musi być puste
        return midField.hasPiece() && !endField.hasPiece();
    }
    // Sprawdza, czy możliwy jest przeskok nad kilkoma pionami
    private boolean canMultiJump(Field currentField, Field targetField, Set<Field> visitedFields) {
        // Jeśli aktualne pole to pole docelowe, multi-jump jest możliwy
        if (currentField == targetField) {
            return true;
        }

        // Dodaj aktualne pole do odwiedzonych
        visitedFields.add(currentField);

        // Sprawdź możliwe skoki
        for (Field neighbor : currentField.getNeighbours()) {
            if (neighbor != null && neighbor.hasPiece() && !visitedFields.contains(neighbor)) {
                // Oblicz pozycję pola docelowego po przeskoczeniu pionka
                int jumpRow = 2 * neighbor.getRow() - currentField.getRow();
                int jumpCol = 2 * neighbor.getCol() - currentField.getCol();

                Field jumpTarget = ChooseBoard.getInstance().getBoard().getSpecificField(jumpRow, jumpCol);

                // Sprawdź, czy pole docelowe jest poprawne
                if (jumpTarget != null && !jumpTarget.hasPiece() && !visitedFields.contains(jumpTarget)) {
                    if (canMultiJump(jumpTarget, targetField, visitedFields)) {
                        return true; // Jeśli dotarliśmy do celu, multi-jump jest możliwy
                    }
                }
            }
        }

        // Usuń aktualne pole z odwiedzonych (backtracking)
        visitedFields.remove(currentField);

        return false; // Brak poprawnej sekwencji skoków

    }

    public boolean firstCheck() {// Jeśli na polu startowym nie ma pionka albo na polu końcowym jest pionek to ruch nie jest możliwy
        if(!startField.hasPiece() || endField.hasPiece())
            return false;
        return true;}

    // Sprawdza czy można wykonać taki ruch
    public boolean isValidMove() {

        // Na samym początku sprawdzenie ruchu prostego (na sąsiednie pole)
        if (startField.getNeighbours().contains(endField)) {
            return true;

        // Następnie sprawdzenie ruchu skoku nad pionkiem (na razie nad jednym pionkiem)
        } else if ((Math.abs(startField.getRow() - endField.getRow()) == 2 && Math.abs(startField.getCol() - endField.getCol()) == 2) ||
                (Math.abs(startField.getCol() - endField.getCol()) == 2 && startField.getRow() == endField.getRow())) {
            return canJumpOverPiece();
        } else {
            // W tym zadaniu sprawdzi się lepiej niż ArrayLista - będzie po prostu szybszy
            Set<Field> visitedFields = new HashSet<>();
            return canMultiJump(startField, endField, visitedFields);
        }
    }
    public void performMove() {

        Piece piece = startField.getPiece(); // Pobierz pionek z pola początkowego

        // Wyślij powiadomienie do wszystkich graczy
        String message = String.format("Move performed by %s: %dx%d -> %dx%d",
                piece.getColor(), // Kolor pionka wykonującego ruch
                startField.getRow(), startField.getCol(),
                endField.getRow(), endField.getCol());
        GameManager.getInstance().notifyObservers(message); // Wywołanie notifyObservers

        // Zaktualizuj GUI
        guiMoves.updateMove(startField, endField);

    }
}
