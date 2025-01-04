package server;
import GUI.GUI;
import GUI.GUIMoves;
import board.Field;
import board.Piece;

public class MovesManager {

    private Mediator player;
    private String command;

    private Field startField = null;
    private Field endField = null;
    private GUIMoves guiMoves;
    private GameManager gameManager;

    public MovesManager(Mediator player, String command, GameManager gameManager) {
        this.player = player;
        this.command = command;
        this.gameManager = gameManager;

        // Uzyskaj instancję GUI i zainicjalizuj GUIMoves
        GUI gui = GUI.getInstance();
        this.guiMoves = new GUIMoves(gui);

        isMoveIntoStar();
    }

    public Field getStartField() {
        return startField;
    }

    // Metoda odpowiedzialna za sprawdzanie czy ruch jest całkowicie poprawny w ramach wybranej planszy
    private boolean isMoveIntoStar() {
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

    private boolean isValidMove() {
        if(!isMoveIntoStar()) return false;          // Jeśli ruch jest całkowicie poza planszą lub zła komenda

        // Końcowe pole nie jest sąsiadem (czyli jest gdzieś dalej) lub jest zajęte przez pion
        if(!startField.getNeighbours().contains(endField) || endField.hasPiece()) {
            player.sendMessage("Move is not available");
            return false;
        }
        // W innym wypadku ruch jest możliwy
        return true;
    }
    public void performMove() {
        if (!isValidMove()) {
            player.sendMessage("Cannot perform move. Invalid move detected.");
        } else {
            // Przenieś pionek
            Piece piece = startField.getPiece(); // Pobierz pionek z pola początkowego
            startField.removePiece();           // Usuń pionek z pola początkowego
            endField.setPiece(piece);           // Ustaw pionek na polu końcowym

            // Wyślij powiadomienie do wszystkich graczy
            String message = String.format("Move performed by %s: %dx%d -> %dx%d",
                    piece.getColor(), // Kolor pionka wykonującego ruch
                    startField.getRow(), startField.getCol(),
                    endField.getRow(), endField.getCol());
            gameManager.notifyObservers(message); // Wywołanie notifyObservers

            // Zaktualizuj GUI
            guiMoves.updateMove(startField, endField);
        }
    }
}
