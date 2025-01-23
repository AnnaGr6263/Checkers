package bot;

import server.Mediator;
import server.manager.GameManager;
import board.Field;
import board.enums.PieceColor;

/**
 * Klasa reprezentująca bota jako gracza w grze Chinese Checkers.
 */
public class Bot extends Mediator {
    private final PieceColor botColor;
    private final GameManager gameManager;


    public Bot(PieceColor botColor) {
        super(null); // Bot nie ma rzeczywistego połączenia sieciowego
        this.botColor = botColor;
        this.gameManager = GameManager.getInstance();
    }

    /**
     * Bot wykonuje swój ruch.
     */
    public void makeMove() {
        Field startField = getRandomStartField();
        if (startField == null) {
            sendMessage("No valid start field found for bot.");
            return;
        }

        Field endField = getRandomValidMove(startField);
        if (endField != null) {
            gameManager.processMoveFromClick(startField, endField, -1);
            sendMessage("[BOT] performed move: " + startField.getRow() + "x" + startField.getCol() + " -> " + endField.getRow() + "x" + endField.getCol());
        }
    }

    /**
     * Wybiera pole startowe dla bota.
     */
    private Field getRandomStartField() {
        // TODO: DO UZUPEŁNIENIA
        return null;
    }

    /**
     * Wybiera pole docelowe dla bota.
     */
    private Field getRandomValidMove(Field startField) {
        // TODO: DO UZUPEŁNIENIA
        return startField;
    }

    @Override
    public void sendMessage(String message) {
        System.out.println("[BOT] " + message);
    }
}
