package GUI;

import board.Field;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import server.manager.GameManager;


public class ClickHandler {
    private Field selectedStartField; // Wybrane pole początkowe
    private Field selectedEndField; // Wybrane pole końcowe

    private Circle circleToHighlight;

    public ClickHandler() {}

    public void handle(Field clickedField, Circle circle) {

        if (selectedStartField == null) {
            circleToHighlight = circle;
            selectedStartField = clickedField;
            highlightField(circleToHighlight);
        } else {
            selectedEndField = clickedField;

            removeHighlight(circleToHighlight);
            GameManager.getInstance().processMoveFromClick(selectedStartField, selectedEndField);

            selectedStartField = null;
            selectedEndField = null;
        }
    }

    private void highlightField(Circle circle) {

        if (circle != null) {
            circle.setStroke(Color.LIGHTGRAY); // Zmień kolor obrysu
            circle.setStrokeWidth(3.0); // Zwiększ grubość obrysu
        }
    }
    private void removeHighlight(Circle circle) {
        if (circle != null) {
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(1.5);
            circle = null;
        }
    }

    public Field getSelectedEndField() {
        return selectedEndField;
    }

    public Field getSelectedStartField() {
        return selectedStartField;
    }
}
