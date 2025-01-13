package GUI;

import board.Field;
import board.enums.PieceColor;
import javafx.scene.input.MouseEvent;
import server.GameManager;
import server.Mediator;
import server.MovesManager;
import server.RulesManager;

public class ClickHandler {
    private Field selectedStartField; // Wybrane pole początkowe
    private Field selectedEndField; // Wybrane pole końcowe
    private PieceColor pieceColor;  // Kolor klikniętego pionka na polu początkowym

    public ClickHandler() {}

    public void handle(MouseEvent event, Field clickedField) {

        if (selectedStartField == null) {
            selectedStartField = clickedField;
        } else {
            selectedEndField = clickedField;

            GameManager.getInstance().processMoveFromClick(selectedStartField, selectedEndField);

            selectedStartField = null;
            selectedEndField = null;
        }
    }

    private boolean isMoveValid(Field start, Field end) {
        return true;
    }

    public Field getSelectedEndField() {
        return selectedEndField;
    }

    public Field getSelectedStartField() {
        return selectedStartField;
    }
}
