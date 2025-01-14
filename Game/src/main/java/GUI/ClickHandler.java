package GUI;

import board.Field;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import server.manager.GameManager;

/**
 * Klasa tworzona w ramach obsługi klikania w GUI w celu wybrania pól początkowych i końcowych.
 */
public class ClickHandler {
    private Field selectedStartField; // Wybrane pole początkowe
    private Field selectedEndField; // Wybrane pole końcowe

    private Circle circleToHighlight;

    /**
     * Konstruktor.
     */
    public ClickHandler() {}

    /**
     * Najważniejsza metoda w klasie, czyli handle.
     * Odpowiada za weryfikację, czy kliknięcie jest pierwsze i dotyczy pola startowego, czy drugie i dotyczy pola końcowego.
     *
     * @param clickedField Pole, które zostało kliknięte.
     * @param circle Obiekt koła, graficzna reprezentacja w GUI naszego klikniętego pola.
     */
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

    /**
     * Metoda pomocnicza, dzięki której podświetlamy kliknięte pole (startowe).
     *
     * @param circle Obiekt pola w GUI.
     */
    private void highlightField(Circle circle) {

        if (circle != null) {
            circle.setStroke(Color.LIGHTGRAY); // Zmień kolor obrysu
            circle.setStrokeWidth(3.0); // Zwiększ grubość obrysu
        }
    }

    /**
     * Metoda pomocnicza usuwająca podświetlenie.
     *
     * @param circle Obiekt pola w GUI.
     */
    private void removeHighlight(Circle circle) {
        if (circle != null) {
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(1.5);
            circle = null;
        }
    }

    /**
     * Getter.
     *
     * @return Pole końcowe.
     */
    public Field getSelectedEndField() {
        return selectedEndField;
    }

    /**
     * Getter.
     *
     * @return Pole początkowe.
     */
    public Field getSelectedStartField() {
        return selectedStartField;
    }
}
