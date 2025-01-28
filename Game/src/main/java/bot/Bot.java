package bot;

import board.BoardSetup;
import board.homes.DestinationHome;
import server.Mediator;
import server.manager.CommandManager;
import server.manager.GameManager;
import board.Field;
import board.enums.PieceColor;
import server.ChooseBoard;
import server.manager.MovesManager;
import server.manager.VictoryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Klasa reprezentująca bota jako gracza w grze Chinese Checkers.
 * Bot działa jako osobny wątek, który sprawdza, czy jest jego tura i wykonuje losowy ruch.
 */
public class Bot extends Mediator implements Runnable {
    private final PieceColor botColor;              // Kolor pionków bota
    private final GameManager gameManager = GameManager.getInstance();          // Instancja zarządzania grą
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

                    makeMove();

                } catch (Exception e) {
                    throw new RuntimeException(e);
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
     * Metoda sprawdzająca, czy bot wygrał.
     * Pobiera VictoryManager dynamicznie
     * @return true, jeśli bot wygrał; false, jeśli jeszcze nie.
     */
    private boolean hasBotWon() {
        VictoryManager victoryManager = gameManager.getVictoryManager();

        if (victoryManager == null) {
            sendMessage("[BOT] VictoryManager is not initialized yet.");
            return false;
        }

        return victoryManager.checkVictory(botColor);
    }

    /**
     * Metoda wykonująca ruch bota w grze.
     * Bot próbuje znaleźć losowy pionek i wykonać legalny ruch.
     * Priorytetowo sprawdza ruchy w obrębie gwiazdy, a jeśli żaden ruch nie jest poprawny,
     * pomija turę po określonej liczbie prób.
     */
    public void makeMove() {
        // Sprawdzenie, czy to rzeczywiście tura bota
        if (gameManager.getCurrentPlayer() != this) {
            return;
        }

        int maxAttempts = 10; // Ograniczenie liczby prób znalezienia poprawnego ruchu
        int attempts = 0;

        while (attempts < maxAttempts) {
            try {
                // Wybór losowego pionka do ruchu
                Field startField = getRandomStartField();
                if (startField == null) {
                    sendMessage("[BOT] No valid move found, skipping turn.");
                    gameManager.getRulesManager().nextPlayer();
                    return;
                }

                // Wybór najlepszego dostępnego ruchu dla wybranego pionka
                Field endField = getBestValidMove(startField);
                if (endField != null) {
                    // Formatowanie komendy ruchu w postaci "move XxY->AxB"
                    String moveKey = startField.getRow() + "x" + startField.getCol() + "->" +
                            endField.getRow() + "x" + endField.getCol();
                    String command = "move " + moveKey;

                    // Sprawdzenie, czy ruch znajduje się w obrębie gwiazdy
                    CommandManager commandManager = new CommandManager(this, command);
                    if (commandManager.isMoveIntoStar()) {
                        sendMessage("[BOT] Performing move: " + command);
                        gameManager.handleCommand(this, command); // Wysłanie ruchu do systemu gry

                        // teraz sprawdzamy wygraną dopiero po wykonaniu ruchu
                        if (hasBotWon()) {
                            gameManager.notifyObservers("Bot with color " + botColor +
                                    " takes " + gameManager.getVictoryManager().whichPlace() + " place.");

                            if (gameManager.getVictoryManager().isEnd()) {
                                gameManager.notifyObservers("End of the game.");
                                gameManager.endGame();
                            }
                        }
                        return;
                    } else {
                        sendMessage("[BOT] Move is outside the star, searching for another move.");
                    }
                }
            } catch (IllegalArgumentException e) {
                // Obsługa wyjątku w przypadku próby wykonania niepoprawnego ruchu (np. wyjście poza planszę)
                sendMessage("[BOT] Invalid move detected: " + e.getMessage());
            } catch (Exception e) {
                // Obsługa innych nieoczekiwanych błędów
                sendMessage("[BOT] Unexpected error while making a move: " + e.getMessage());
            }
            attempts++; // Zwiększenie licznika prób
        }

        // Jeśli bot nie znalazł poprawnego ruchu po określonej liczbie prób, pomija swoją turę
        sendMessage("[BOT] No valid move found after " + maxAttempts + " attempts, skipping turn.");
        gameManager.getRulesManager().nextPlayer();
    }

    /**
     * Wybiera losowy pionek należący do bota, który może wykonać ruch.
     * Bot przeszukuje całą planszę i zbiera wszystkie pionki w swoim kolorze.
     * Jeśli nie ma dostępnych pionków do ruchu, zwraca `null`.
     * W przeciwnym razie wybiera losowy pionek spośród dostępnych.
     *
     * @return Losowo wybrany pionek bota do ruchu lub `null`, jeśli brak dostępnych pionków.
     */
    private Field getRandomStartField() {
        List<Field> botFields = new ArrayList<>(); // Lista pól, na których znajdują się pionki bota
        List<Field> fields = ChooseBoard.getInstance().getBoard().getFieldsInsideAStar(); // Pobranie wszystkich pól wewnątrz gwiazdy

        // Przeszukiwanie pól w poszukiwaniu pionków bota
        for (Field field : fields) {
            if (field.hasPiece() && field.getPiece().getColor() == botColor) {
                botFields.add(field); // Dodanie do listy pionków bota
            }
        }

        // Jeśli nie znaleziono żadnego pionka do ruchu, zwróć `null`
        if (botFields.isEmpty()) {
            return null;
        }

        // Wybór losowego pionka z listy dostępnych
        return botFields.get(random.nextInt(botFields.size()));
    }

    /**
     * Wybiera najlepszy możliwy ruch dla bota, uwzględniając zarówno skoki nad pionkami, jak i ruch na sąsiednie pole.
     * Priorytetowo traktuje skoki, jeśli są dostępne, ponieważ mogą pozwolić na szybsze przemieszczanie się do celu.
     * Wybór ruchu opiera się na minimalizacji dystansu do docelowego obszaru bota.
     *
     * @param startField Pole, z którego bot wykonuje ruch.
     * @return Najlepsze pole docelowe lub `null`, jeśli żaden ruch nie jest możliwy.
     */
    private Field getBestValidMove(Field startField) {
        List<Field> neighbors = startField.getNeighbours(); // Pobranie sąsiadujących pól
        Field bestMove = null;
        int minDistance = Integer.MAX_VALUE;
        BoardSetup board = ChooseBoard.getInstance().getBoard();

        // Priorytet dla skoków nad innymi pionkami
        for (Field neighbor : neighbors) {
            if (neighbor.hasPiece()) { // Jeśli sąsiadujące pole jest zajęte, sprawdzamy możliwość skoku
                int jumpRow = 2 * neighbor.getRow() - startField.getRow();
                int jumpCol = 2 * neighbor.getCol() - startField.getCol();

                // Sprawdzenie, czy skok mieści się w granicach planszy
                if (jumpRow >= 0 && jumpRow < 17 && jumpCol >= 0 && jumpCol < 25) {
                    try {
                        Field jumpTarget = board.getSpecificField(jumpRow, jumpCol);
                        if (jumpTarget != null && !jumpTarget.hasPiece()) {
                            MovesManager movesManager = new MovesManager(startField, jumpTarget);
                            if (movesManager.isValidMove()) { // Walidacja ruchu
                                int distance = calculateDistanceToGoal(jumpTarget);
                                if (distance < minDistance) { // Sprawdzenie, czy ruch przybliża do celu
                                    minDistance = distance;
                                    bestMove = jumpTarget;
                                }
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        sendMessage("[BOT] Nieprawidłowy skok, pomijam ruch: " + e.getMessage());
                    }
                }
            }
        }

        // Jeśli skok nie jest możliwy, wybór zwykłego ruchu na sąsiednie pole
        if (bestMove == null) {
            for (Field neighbor : neighbors) {
                if (!neighbor.hasPiece() && board.getFieldsInsideAStar().contains(neighbor)) {
                    MovesManager movesManager = new MovesManager(startField, neighbor);
                    if (movesManager.isValidMove()) {
                        int distance = calculateDistanceToGoal(neighbor);
                        if (distance < minDistance) { // Wybór ruchu minimalizującego dystans do celu
                            minDistance = distance;
                            bestMove = neighbor;
                        }
                    }
                }
            }
        }

        return bestMove;
    }

    /**
     * Pobiera pola docelowe bota dopiero w momencie ich użycia.
     * @return Lista pól docelowych dla bota.
     */
    private List<Field> getDestinationFields() {
        VictoryManager victoryManager = gameManager.getVictoryManager();
        if (victoryManager == null) {
            return new ArrayList<>();
        }

        return victoryManager.getDestinationHomesMap().getOrDefault(botColor, new ArrayList<>());
    }

    /**
     * Oblicza dystans Manhattanowski od danego pola do najbliższego celu bota.
     * @param field Pole, dla którego liczymy dystans.
     * @return Najmniejsza odległość do pola docelowego.
     */
    private int calculateDistanceToGoal(Field field) {
        int minDistance = Integer.MAX_VALUE;
        List<Field> destinationFields = getDestinationFields();

        // Iteracja przez wszystkie pola docelowe bota
        for (Field target : destinationFields) {
            int distance = Math.abs(field.getRow() - target.getRow()) + Math.abs(field.getCol() - target.getCol());
            // Jeśli obliczona odległość jest mniejsza niż aktualna minimalna, aktualizujemy wartość
            if (distance < minDistance) {
                minDistance = distance;
            }
        }

        return minDistance;     // Zwracamy najmniejszą odległość
    }

    /**
     * Wysyła wiadomość informacyjną do konsoli (lub innego systemu logowania).
     * @param message Wiadomość do wysłania
     */
    @Override
    public void sendMessage(String message) {
        System.out.println("[BOT] " + message);
    }
}
