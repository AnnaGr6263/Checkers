package GUI;

import board.Field;
import javafx.scene.input.MouseEvent;
import server.GameManager;
import server.MovesManager;

public class ClickHandler {
    private Field selectedStartField; // Wybrane pole początkowe
    private Field selectedEndField; // Wybrane pole końcowe

    public ClickHandler() {}

    public void handle(MouseEvent event, Field clickedField) {
        if (selectedStartField == null) {
            selectedStartField = clickedField;
            System.out.println("Selected start field: " + selectedStartField);
        } else {
            selectedEndField = clickedField;

            System.out.println("tu jestem");
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
