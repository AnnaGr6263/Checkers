package GUI;

import board.Field;
import javafx.scene.input.MouseEvent;
import server.GameManager;

public class ClickHandler {
    private Field selectedStartField; // Wybrane pole początkowe
    private Field selectedEndField; // Wybrane pole końcowe

    public ClickHandler() {}


    // Obsługa kliknięcia na pole
    public void handle(MouseEvent event, Field clickedField) {
        System.out.println("mouse event worked");

        if (selectedStartField == null) {
            // Jeśli nie wybrano jeszcze pola początkowego, ustaw je
            selectedStartField = clickedField;
            System.out.println("Selected start field: " + selectedStartField);
        } else {
            // Jeśli wybrano pole początkowe, ustaw pole końcowe
            selectedEndField = clickedField;
            System.out.println("Selected end field: " + selectedEndField);

            GameManager.getInstance().processMoveFromClick(selectedStartField, selectedEndField);

            // Zresetuj zaznaczenia po wykonaniu ruchu
            selectedStartField = null;
            selectedEndField = null;
        }
    }

    public Field getSelectedEndField() {
        return selectedEndField;
    }

    public Field getSelectedStartField() {
        return selectedStartField;
    }
}
