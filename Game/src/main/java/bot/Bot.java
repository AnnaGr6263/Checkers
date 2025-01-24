package bot;

import server.Mediator;
import server.manager.GameManager;
import board.Field;
import board.enums.PieceColor;
import server.ChooseBoard;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Klasa reprezentująca bota jako gracza w grze Chinese Checkers.
 * Bot działa jako osobny wątek, który sprawdza, czy jest jego tura i wykonuje losowy ruch.
 */
public class Bot extends Mediator implements Runnable {
    private final PieceColor botColor;              // Kolor pionków bota
    private final GameManager gameManager;          // Instancja zarządzania grą
    private final Random random = new Random();     // Generator losowy do wyboru ruchów
    private volatile boolean running = true;        // Flaga określająca, czy bot ma działać
    private static final ReentrantLock moveLock = new ReentrantLock(); // Blokada synchronizująca ruchy wielu botów

    /**
     * Konstruktor inicjalizujący bota z przypisanym kolorem.
     * @param botColor kolor pionków bota
     */
    public Bot(PieceColor botColor) {
        super(null);
        this.botColor = botColor;
        this.gameManager = GameManager.getInstance();
    }

    /**
     * Metoda uruchamiana w osobnym wątku.
     * Bot sprawdza, czy jest jego kolej i wykonuje ruch.
     */
    @Override
    public void run() {
        while (running) {
            // Bot wykonuje ruchy tylko jeśli gra się rozpoczęła i jest jego tura
            if (gameManager.isGameStarted() && gameManager.getCurrentPlayer() == this) {
                try {
                    moveLock.lock();        // Zabezpieczenie, aby tylko jeden bot mógł wykonywać ruch na raz
                    makeMove();
                } finally {
                    moveLock.unlock();
                }
            }

            try {
                Thread.sleep(500);      // Czekanie przed ponownym sprawdzeniem tury, aby nie obciążać CPU
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;            // Zatrzymanie bota w przypadku przerwania wątku

            }
        }
    }

    /**
     * Wykonuje ruch bota, jeśli jest jego kolej.
     */
    public void makeMove() {
        // Sprawdzenie, czy to rzeczywiście tura tego bota
        if (gameManager.getCurrentPlayer() != this) {
            return;
        }

        // Wybór losowego pionka do przesunięcia
        Field startField = getRandomStartField();
        if (startField == null) {
            sendMessage("[BOT] No valid move found, skipping turn.");
            gameManager.getRulesManager().nextPlayer();     // Przekazanie tury innemu graczowi
            return;
        }

        // Wybór losowego, poprawnego ruchu dla wybranego pionka
        Field endField = getRandomValidMove(startField);
        if (endField != null) {

            // Tworzenie komendy ruchu w formacie "move XxY->AxB"
            String moveKey = startField.getRow() + "x" + startField.getCol() + "->" +
                    endField.getRow() + "x" + endField.getCol();

            String command = "move " + moveKey;
            sendMessage("[BOT] Performing move: " + command);
            gameManager.handleCommand(this, command);

            // Ważne: Nie wywołujemy `nextPlayer()` od razu!
            // System sam zmieni gracza po obsłudze ruchu
        }
    }

    /**
     * Wybiera losowy pionek bota, który może się poruszyć.
     * @return Pole, na którym znajduje się wybrany pionek bota lub null, jeśli nie ma dostępnych pionków.
     */
    private Field getRandomStartField() {
        List<Field> fields = ChooseBoard.getInstance().getBoard().getFieldsInsideAStar();
        for (Field field : fields) {
            if (field.hasPiece() && field.getPiece().getColor() == botColor) {
                return field;   // Zwraca pierwszy znaleziony pionek bota
            }
        }
        return null;         // Brak dostępnych pionków do ruchu
    }

    /**
     * Wybiera losowe dostępne pole, na które można przesunąć dany pionek.
     * @param startField Pole startowe, z którego ma ruszyć pionek.
     * @return Pole docelowe, jeśli istnieje poprawny ruch, lub null, jeśli brak możliwości ruchu.
     */
    private Field getRandomValidMove(Field startField) {
        List<Field> neighbors = startField.getNeighbours();
        for (Field neighbor : neighbors) {
            if (!neighbor.hasPiece()) {
                return neighbor;    // Zwraca pierwsze znalezione wolne pole
            }
        }
        return null;         // Brak dostępnych ruchów
    }

    /**
     * Wysyła wiadomość informacyjną do konsoli (lub innego systemu logowania).
     * @param message Wiadomość do wysłania
     */
    @Override
    public void sendMessage(String message) {
        System.out.println("[BOT] " + message);
    }

    /**
     * Zatrzymuje działanie bota, ustawiając flagę `running` na false.
     */
    public void terminate() {
        running = false;
    }
}
