package server;
import GUI.GUI;
import GUI.GUIMoves;
import board.Field;
import board.Piece;

import java.util.*;

/**
 * Klasa MovesManager odpowiedzialna za sprawdzanie czy zaproponowany przez gracza ruch jest zgodny z zasadami gry.
 */
public class MovesManager {

    private Field startField = null;
    private Field endField = null;
    private GUIMoves guiMoves;

    /**
     * Konstruktor.
     *
     * @param startField Pole początkowe zaproponowanego przez gracza ruchu.
     * @param endField Pole końcowe zaproponowanego przez gracza ruchu.
     */
    public MovesManager(Field startField, Field endField) {
        this.startField = startField;
        this.endField = endField;

        // Uzyskaj instancję GUI i zainicjalizuj GUIMoves
        GUI gui = GUI.getInstance();
        this.guiMoves = new GUIMoves();
    }

    /**
     * Getter.
     *
     * @return Pole początkowe.
     */
    public Field getStartField() {
        return startField;
    }

    /**
     * Metoda sprawdza, czy można wykonać skok nad pionkiem
     *
     * @return Prawdę, jeśli można wykonać przeskok nad pionkiem
     */
    private boolean canJumpOverPiece() {
        // Obliczenie pozycji pola pomiędzy startField a endField
        int midRow = (startField.getRow() + endField.getRow()) / 2;
        int midCol = (startField.getCol() + endField.getCol()) / 2;


        Field midField = ChooseBoard.getInstance().getBoard().getSpecificField(midRow, midCol);
        // Pole pośrednie musi mieć pionek, a końcowe pole musi być puste
        return midField.hasPiece() && !endField.hasPiece();
    }

    /**
     *  Sprawdza, czy możliwy jest przeskok nad kilkoma pionami.
     *
     * @param currentField Pole, na którym aktualnie stoi pionek na początku jest to pole startowe.
     * @param targetField Pole, do którego chcemy dotrzeć.
     * @param visitedFields Wszystkie pola, na których w trakcie wykonywania tej metody stanął pionek.
     * @return Prawdę, gdy można wykonać przeskok nad wieloma pionkami.
     */
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

    /**
     * Metoda najszybciej sprawdzająca poprawność ruchu.
     * Nie można go wykonać gdy: na polu startowym nie ma pionka lub pole końcowe jest zajęte
     *
     * @return Prawdę lub fałsz.
     */
    public boolean firstCheck() {
        // Jeśli na polu startowym nie ma pionka albo na polu końcowym jest pionek to ruch nie jest możliwy
        if(!startField.hasPiece() || endField.hasPiece())
            return false;
        return true;
    }

    // Sprawdza czy można wykonać taki ruch
    /**
     * Sprawdza czy można wykonać taki ruch o zadanych w konstruktorze współrzędnych.
     *
     * @return Prawdę, jeśli ruch może zostać wykonany, w przeciwnym razie fałsz.
     */
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

    /**
     * Metoda odpowiedzialna za wykonanie ruchu.
     */
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
