package GUI;

import board.Field;
import board.Piece;
import javafx.application.Platform;

public class GUIMoves {

    private final GUI gui;

    public GUIMoves(GUI gui) {
        this.gui = gui; // Referencja do instancji GUI
    }

    // Aktualizacja GUI po wykonaniu ruchu
    public void updateMove(Field startField, Field endField) {
        if (startField == null || endField == null) {
            System.out.println("Invalid fields for move.");
            return;
        }

        // Przenieś pionek w logice planszy
        Piece piece = startField.getPiece();
        if (piece != null) {
            startField.removePiece(); // Usuń pionek ze startowego pola
            endField.setPiece(piece); // Umieść pionek na docelowym polu
        }

        // Odśwież GUI, aby pokazać zmiany
        Platform.runLater(() -> gui.refresh());
    }
}
