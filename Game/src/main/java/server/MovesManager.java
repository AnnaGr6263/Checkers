package server;
import GUI.GUI;
import GUI.GUIMoves;
import board.Field;
import board.Piece;

public class MovesManager {

    private Mediator player;
    private Field startField = null;
    private Field endField = null;
    private GUIMoves guiMoves;
    private GameManager gameManager;

    public MovesManager(Mediator player, GameManager gameManager, Field startField, Field endField) {
        this.player = player;
        this.gameManager = gameManager;

        this.startField = startField;
        this.endField = endField;

        // Uzyskaj instancję GUI i zainicjalizuj GUIMoves
        GUI gui = GUI.getInstance();
        this.guiMoves = new GUIMoves();
    }

    public Field getStartField() {
        return startField;
    }

    // Sprawdza, czy można wykonać skok nad pionkiem
    private boolean canJumpOverPiece() {
        // Obliczenie pozycji pola pomiędzy startField a endField
        int midRow = (startField.getRow() + endField.getRow()) / 2;
        int midCol = (startField.getCol() + endField.getCol()) / 2;


        Field midField = ChooseBoard.getInstance().getBoard().getSpecificField(midRow, midCol);
        // Pole pośrednie musi mieć pionek, a końcowe pole musi być puste
        if(midField.hasPiece() && !endField.hasPiece()) {
            return true;
        } else {
            player.sendMessage("Invalid jump");
            return false;
        }
    }

    // Sprawdza czy można wykonać taki ruch
    private boolean isValidMove() {

        // Na samym początku sprawdzenie ruchu prostego (na sąsiednie pole)
        if (startField.getNeighbours().contains(endField) && !endField.hasPiece()) {
            return true;
        }

        // Następnie sprawdzenie ruchu skoku nad pionkiem (na razie nad jednym pionkiem)
        if ((Math.abs(startField.getRow() - endField.getRow()) == 2 && Math.abs(startField.getCol() - endField.getCol()) == 2) ||
                (Math.abs(startField.getCol() - endField.getCol()) == 2 && startField.getRow() == endField.getRow())) {
            return canJumpOverPiece();
        } else {

            // Jeśli żaden z warunków nie jest spełniony, ruch jest nieprawidłowy
            player.sendMessage("Move is not available");
            return false;
        }
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
