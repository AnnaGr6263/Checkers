package server;

import board.Field;

/**
 * Klasa odpowiedzialna za przetwarzanie komendy o schemacie: move [0-16]x[0-24]->[0-16]x[0-24]
 */
public class CommandManager {

    private String command;
    private Field startField;
    private Field endField;
    private Mediator player;

    /**
     * Konstruktor.
     *
     * @param player Gracz, podający komendę.
     * @param command Treść komendy.
     */
    public CommandManager(Mediator player, String command) {
        this.player = player;
        this.command = command;
        this.startField = null;
        this.endField = null;
    }

    /**
     * Metoda odpowiedzialna za sprawdzanie czy ruch jest poprawny w sensie mieszczenia się w ramach wybranej planszy.
     *
     * @return Prawdę, gdy ruch zawiera się w ramach planszy, fałsz w przeciwnym wypadku.
     */
    public boolean isMoveIntoStar() {
        String[] commandWithoutWordMove = command.split(" "); // Podział komendy na części
        if(commandWithoutWordMove.length == 1) {
            player.sendMessage("Invalid command. After move command give coordinates");
            return false;
        }

        String move = commandWithoutWordMove[1];

        String[] moveParts = move.split("->");

        if (moveParts.length != 2) {
            player.sendMessage("Invalid move. Use pattern: move [0,16]x[0,24]->[0,16]x[0,24]");
            return false;
        }

        String start = moveParts[0]; // Początek ruchu
        String end = moveParts[1]; // Koniec ruchu
        String[] startCo = start.split("x");      // Współrzędne pola początkowego
        String[] endCo = end.split("x");      // Współrzędne pola końcowego

        if (startCo.length != 2 || endCo.length != 2) {
            player.sendMessage("Invalid move. Use pattern: move [0,16]x[0,24]->[0,16]x[0,24]");
            return false;
        }

        String rowStartField = startCo[0]; // Wiersz pola początkowego
        String colStartField = startCo[1]; // Kolumna pola początkowego
        String rowEndField = endCo[0];// Wiersz pola końcowego
        String colEndField = endCo[1]; // Kolumna pola końcowego

        int rowSF;
        try {
            rowSF = Integer.parseInt(rowStartField);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid start row: " + rowStartField);
            return false; // Nie można sparsować współrzędnej wiersza startowego
        }

        int colSF;
        try {
            colSF = Integer.parseInt(colStartField);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid start column: " + colStartField);
            return false; // Nie można sparsować współrzędnej kolumny startowej
        }


        int rowEF;
        try {
            rowEF = Integer.parseInt(rowEndField);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid end row: " + rowEndField);
            return false; // Nie można sparsować współrzędnej wiersza końcowego
        }

        int colEF;
        try {
            colEF = Integer.parseInt(colEndField);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid end column: " + colEndField);
            return false; // Nie można sparsować współrzędnej kolumny końcowej
        }


        try {
            startField = ChooseBoard.getInstance().getBoard().getSpecificField(rowSF, colSF);
            endField = ChooseBoard.getInstance().getBoard().getSpecificField(rowEF, colEF);

            if (startField.isInStar()) {
                if (endField.isInStar()) {
                    return true;
                } else {
                    player.sendMessage("End Field " + end + " is not inside the star-shaped board");
                    return false;
                }
            } else {
                player.sendMessage("Start Filed " + start + " is not inside the star-shaped board");
                return false;
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
            return false;
        }
    }

    /**
     * Getter do pola końcowego.
     *
     * @return Zwraca pole końcowe.
     */
    public Field getEndField() {
        return endField;
    }

    /**
     * Getter do pola początkowego.
     *
     * @return Zwraca pole początkowe.
     */
    public Field getStartField() {
        return startField;
    }
}
