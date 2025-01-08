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

            GameManager.getInstance().processMoveFromClick(selectedStartField, selectedEndField);


            /*
            // Walidacja ruchu w GUI
            if (isMoveValid(selectedStartField, selectedEndField)) {
                GameManager.getInstance().processMoveFromClick(selectedStartField, selectedEndField);
            } else {
                System.out.println("Invalid move!");
                //GUI.getInstance().showError("Invalid move!");
            }

            // Resetuj zaznaczenie pól
            //GUI.getInstance().highlightField(selectedStartField, false);*/
            selectedStartField = null;
            selectedEndField = null;
        }
    }

    private boolean isMoveValid(Field start, Field end) {
        if (start == null || end == null) return false;

        // Sprawdź, czy pole końcowe jest sąsiadem
        return start.getNeighbours().contains(end) && !end.hasPiece();

    }

    public Field getSelectedEndField() {
        return selectedEndField;
    }

    public Field getSelectedStartField() {
        return selectedStartField;
    }
}
