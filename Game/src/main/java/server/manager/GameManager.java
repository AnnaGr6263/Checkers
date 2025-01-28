package server.manager;
import board.*;
import board.enums.HomeColor;
import board.enums.PieceColor;
import board.fill.FillWIthPieces;
import board.fill.FillWithPiecesYinAndYang;
import board.homes.DestinationHome;
import board.homes.DestinationHomeYinAndYang;
import GUI.GUI;
import bot.Bot;
import data.entities.Game;
import data.entities.Move;
import GUI.GUIReplay;
import data.entities.Setup;
import javafx.application.Platform;
import server.ChooseBoard;
import server.Mediator;
import server.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa GameMamager służąca do zarządzania grą
 */

public class GameManager {

    private final GameDataService gameDataService;
    private Game currentGame;

    private static volatile GameManager gameManagerInstance;   // Jedyna instancja klasy GameManager
    private final List<Observer> observers = new ArrayList<>(); // Lista obserwatorów
    private final List<Mediator> players = new ArrayList<>();   // Lista graczy
    private boolean gameStarted = false;
    private boolean gameEnded = false;
    private RulesManager rulesManager;                          // Zarządca zasad gry
    private VictoryManager victoryManager;                      // Zarządca wygranej
    private YinAndYangManager yinAndYangManager;
    private Thread botThread;

    /**
     * Prywatny konstruktor, ponieważ korzystamy ze wzorca projektowego Singleton
     */
    private GameManager(GameDataService gameDataService){ // Prywatny konstruktor
        this.gameDataService = gameDataService;
    }
    // Dodanie Springowego zarządzania zależnością
    public static GameManager getInstance(GameDataService gameDataService) {
        if (gameManagerInstance == null) {
            synchronized (GameManager.class) {
                if (gameManagerInstance == null) {
                    gameManagerInstance = new GameManager(gameDataService);
                }
            }
        }
        return gameManagerInstance;
    }

    public static GameManager getInstance() {
        if (gameManagerInstance == null) {
            throw new IllegalStateException("GameManager has not been initialized. Please call getInstance(GameDataService) first.");
        }
        return gameManagerInstance;
    }

    /**
     * Metoda zwracjąca managera dla wariantu gry YingYang
     *
     * @return YingAndYangManager
     */
    public synchronized YinAndYangManager getYinAndYangManager() {
        if (yinAndYangManager == null) {
            yinAndYangManager = new YinAndYangManager();
        }
        return yinAndYangManager;
    }


    /**
     * Metoda dodaje obiekt typu Observer, Obserwer jest implementowany przez każdego Mediator, czyli klienta
     * uczestniczącego w grze.
     *
     * @param observer Obserwator.
     */
    public void addObserver(Observer observer) {
        observers.add(observer);
    }


    /**
     * Służy do przesłania informacji do wszystkich obserwatorów.
     *
     * @param message Treść wiadomości
     */
    public void notifyObservers(String message) {
        for (Observer observer : new ArrayList<>(observers)) {  //new ArrayList<>(observers) - aby uniknąć ConcurrentModificationException
            observer.update(message);
        }
    }

    /**
     * Getter.
     *
     * @return Listę Mediatorów, czyli graczy.
     */
    public List<Mediator> getPlayers() {
        return players;
    }

    /**
     * Metoda określająca czy gra się rozpoczęła.
     *
     * @return Prawdę gdy gra sie rozpoczęła i fałsz w przeciwnym razie.
     */
    public boolean isGameStarted() {
        return gameStarted;
    }


    /**
     * Metoda opdowiedzialna za dodanie gracza do gry.
     *
     * @param player Dany gracz.
     * @return Prawdę jeśli gracz może jeszcze dołączyć do gry.
     */
    public synchronized boolean addPlayer(Mediator player) {
        if (gameStarted) {
            player.sendMessage("Game has already started. You cannot join.");
            return false;
        }
        if (getYinAndYangManager().isYinAndYangEnabled() && players.size() >= 2) {
            player.sendMessage("Cannot join. Yin and Yang allows only 2 players.");
            return false;
        }
        if (players.size() >= 6) {
            player.sendMessage("Cannot join. Maximum 6 players allowed.");
            return false;
        }
        players.add(player); // Dodanie gracza do listy
        addObserver(player); // Dodanie gracza jako obserwatora
        notifyObservers("Player joined. Total players: " + players.size());

        if (players.size() == 2) {
            player.sendMessage("Do you want to switch to Yin and Yang variant? (yes)");
        }

        if (players.size() == 3 && !yinAndYangManager.isYinAndYangEnabled()) {
            notifyObservers("Yin and Yang is no longer available due to too many players (2 required).");
        }

        return true;
    }

    /**
     * Obsługuje komendy wysyłane przez graczy do serwera.
     *
     * @param player Gracz, który wysłał komendę.
     * @param command Treść komendy.
     */
    public synchronized void handleCommand(Mediator player, String command) {
        // Jeśli gra się skończyła nie obsuguje już żadnych komend
        if (gameEnded) {
            player.sendMessage("The game has ended. No more moves can be made.");
            return;
        }
        String[] elementsOfCommand = command.split(" "); // Podział komendy na elementy

        if (command.equalsIgnoreCase("yes")) {
            if (players.size() == 2 && !gameStarted) {
                getYinAndYangManager();
                yinAndYangManager.enableYinAndYang();
            } else {
                player.sendMessage("Cannot enable Yin and Yang. Ensure there are exactly 2 players and the game hasn't started.");
            }
        } else if(command.equalsIgnoreCase("join")) {
            addPlayer(player);
        } else if (command.equalsIgnoreCase("game start")) {
            startGame(player);
        } else if (elementsOfCommand[0].equalsIgnoreCase("choose") && elementsOfCommand[1].equalsIgnoreCase("board")) {
            chooseBoard(player, command);
        } else if (elementsOfCommand[0].equalsIgnoreCase("move")) {
            processMove(player, command);
        } else if (command.equalsIgnoreCase("skip")) {
            skipTurn(player);
        } else if (command.equalsIgnoreCase("replay game")) {
            showSavedGames(player);
        } else if (command.startsWith("replay game ")) {
            Long gameId = Long.parseLong(command.split(" ")[2]);
            replayGame(player, gameId);
        }
    }

    /**
     * Metoda odpowiedzialna za rozpoczęcie gry.
     * Sprawdza czy wszystkie potrzebne do rozpoczęcia warunki gry zostały spełnione.
     * Ustawia flagę rozpoczęcia gry na true.
     *
     * @param sender gracz
     */
    public void startGame(Mediator sender) {
        if (gameStarted) {
            sender.sendMessage("Game has already started.");
            return;
        }
        if (!ChooseBoard.getInstance().isBoardChosen()) { // Sprawdzenie, czy plansza została wybrana
            sender.sendMessage("Please choose a board before starting the game using the command 'choose board BigBoard'.");
            return;
        }

        if (players.size() == 1) {
            startBotThread(PieceColor.BLUE_PIECE);
        }

        if (players.size() != 1 && players.size() != 2 && players.size() != 3 && players.size() != 4 && players.size() != 6) {
            sender.sendMessage("Game requires 1, 2, 3, 4, or 6 players.");
            return;
        }
        gameStarted = true; // Ustawienie flagi rozpoczęcia gry
        notifyObservers("Game started with " + players.size() + " players on the chosen board!");

        if (yinAndYangManager.isYinAndYangEnabled()) {
            DestinationHomeYinAndYang destinationHome = new DestinationHomeYinAndYang();
            FillWithPiecesYinAndYang filler = new FillWithPiecesYinAndYang(destinationHome);
            yinAndYangManager.notifyPlayersAboutHomesAndColors(filler.getPieceToHomeMapping(), destinationHome);
            victoryManager = new VictoryManager(destinationHome.getDestinationHomesMap(), players.size());
        } else {
            DestinationHome destinationHome = new DestinationHome();
            destinationHome.attachDestinationHomes();
            FillWIthPieces fillWIthPieces = new FillWIthPieces(players.size());
            victoryManager = new VictoryManager(destinationHome.getDestinationHomesMap(), players.size());
        }

        rulesManager = new RulesManager(players);
        rulesManager.startGame();

        // Tworzenie listy graczy bez botów
        List<Mediator> humanPlayers = new ArrayList<>();
        for (Mediator player : players) {
            if (!(player instanceof Bot)) {     // Sprawdzamy, czy gracz NIE jest botem
                humanPlayers.add(player);
            }
        }

        // Lista kolorów pionków dostępnych w grze
        List<PieceColor> piecesInGame = new ArrayList<>();
        if(yinAndYangManager.isYinAndYangEnabled()) {
            piecesInGame.add(PieceColor.BLACK_PIECE);
            piecesInGame.add(PieceColor.YELLOW_PIECE);
        } else {
            List<HomeColor> homeColorsInGame = rulesManager.getHomesForPlayerCount(players.size());
            for (HomeColor homeColor : homeColorsInGame) {
                piecesInGame.add(rulesManager.mapHomeColorToPieceColor(homeColor));
            }
        }

        // Przekazanie instancji planszy do GUI
        BoardSetup currentBoard = ChooseBoard.getInstance().getBoard();
        System.out.println("Starting GUI...");

        // Ustawienie planszy w GUI
        GUI.setBoard(currentBoard);

        // Uruchomienie GUI dla każdego gracza
        GUI.launchForPlayers(humanPlayers.size(), piecesInGame);

        // Rejestracja gry w bazie danych
        currentGame = gameDataService.whenGameStarted(players.size(), yinAndYangManager.isYinAndYangEnabled());

        // Po fillWithPieces jednym albo drugim przyszła pora na zapisanie tych ustawień początkowych w bazie danych
        for(Field field : ChooseBoard.getInstance().getBoard().getFieldsInsideAStar()) {
            Piece piece = field.getPiece();
            if(piece != null) {
                String color = piece.getColor().toString();
                int posX = field.getRow();
                int posY = field.getCol();
                gameDataService.recordSetup(currentGame, color, posX, posY);
            }
        }
        // Powiadom pierwszego gracza o jego ruchu
        rulesManager.getCurrentPlayer().sendMessage("It's your turn!");
    }

    /**
     * Tworzy i uruchamia nowy wątek bota dla danego koloru pionków.
     * Dodaje bota do listy graczy, rejestruje go jako obserwatora i powiadamia pozostałych graczy o jego dołączeniu.
     *
     * @param botColor Kolor pionków bota (RED_PIECE, BLUE_PIECE itp.).
     */
    private void startBotThread(PieceColor botColor) {
        // Tworzy nową instancję bota dla danego koloru
        Bot bot = new Bot(botColor);

        // Tworzy nowy wątek i przypisuje do niego bota
        botThread = new Thread(bot);

        // Uruchamia wątek bota
        botThread.start();

        // Dodaje bota do listy graczy
        players.add(bot);

        // Rejestruje bota jako obserwatora gry
        addObserver(bot);

        // Powiadamia wszystkich obserwatorów (np. innych graczy) o dołączeniu bota do gry
        notifyObservers("Bot joined the game!");
    }

    /**
     * Metoda odpowiedzialna za wybór planszy do gry.
     *
     * @param player Gracz.
     * @param command Komenda wyboru planszy.
     */
    private void chooseBoard(Mediator player, String command) {
        if (gameStarted) {
            player.sendMessage("Cannot change the board. The game has already started.");
            return;
        }
        if (ChooseBoard.getInstance().isBoardChosen()) { // Sprawdzenie, czy plansza została już wybrana
            player.sendMessage("Board has already been chosen.");
            return;
        }
        if (command.equalsIgnoreCase("choose board BigBoard")) {
            ChooseBoard.getInstance().choose(1);    // Wybór BigBoard
            notifyObservers("Board chosen: Big Board (16x24).");
        } else {
            player.sendMessage("Invalid board selection. Use 'choose board BigBoard'.");
        }
    }

    /**
     * Obsługa ruchu wysyłanego przez terminal przez gracza.
     *
     * @param player Gracz.
     * @param command Treść komendy ruchu.
     */
    private void processMove(Mediator player, String command) {
        System.out.println("Processing move for player: " + player);
        System.out.println("Command: " + command);

        if (!gameStarted) {
            player.sendMessage("Game has not started yet.");
            return;
        }
        // Utworzenie obiektu zajmującego się sprawdzaniem poprawności komendy i jej składni
        CommandManager commandManager = new CommandManager(player, command);
        if (!commandManager.isMoveIntoStar()) {
            return; // Nieprawidłowa komenda lub pola są poza gwiazdą
        }
        Field startField = commandManager.getStartField();
        Field endField = commandManager.getEndField();

        if (!rulesManager.canPlayerMove(player, startField)) {
            return; // Gracz nie ma prawa wykonać ruchu
        }

        MovesManager movesManager = new MovesManager(startField, endField);
        if(movesManager.isValidMove()) {
            movesManager.performMove();         // Oddelegowanie całej logiki ruchu do MovesManager

            int startX = startField.getRow();
            int startY = startField.getCol();
            int endX = endField.getRow();
            int endY = endField.getCol();

            gameDataService.recordMove(currentGame, startX, startY, endX, endY);     // Zapisanie ruchu do bazy danych

            rulesManager.nextPlayer();          // Przejście do kolejnego gracza

            // Sprawdzenie wygranej
            if (victoryManager.checkVictory(rulesManager.getPlayerColor(player))) {

                System.out.println("Checking victory for pieceColor: " + rulesManager.getPlayerColor(player));

                notifyObservers("Player with color " + rulesManager.getPlayerColor(player) + " takes " + victoryManager.whichPlace() +" place.");
                if(victoryManager.isEnd()) {
                    notifyObservers("End of the game.");
                    endGame();
                }
            }
        } else {
            player.sendMessage("Invalid move.");
        }
    }


    /**
     * Umożliwienie pominięcia swojego ruchu przez gracza.
     *
     * @param player Gracz.
     */
    private void skipTurn(Mediator player) {
        if (!gameStarted) {
            player.sendMessage("Game has not started yet.");
            return;
        }
        if (player != rulesManager.getCurrentPlayer()) {
            player.sendMessage("It's not your turn!");
            return;
        }

        // Pobierz kolor gracza, który rezygnuje
        PieceColor playerColor = rulesManager.getPlayerColor(player);

        // Powiadom wszystkich graczy o rezygnacji z ruchu
        notifyObservers(String.format("Turn skipped by %s", playerColor.name()));

        // Przejdź do następnego gracza
        rulesManager.nextPlayer();
    }

    /**
     * Metoda, do której kieruje nas ClickHandler. Odpowiada ona za sprawdzenie i wykonanie ruchu przesłanego
     * za pomocą klikania w GUI.
     *
     * @param selectedStartField Pole początkowe.
     * @param selectedEndField Pole końcowe.
     */
    public void processMoveFromClick(Field selectedStartField, Field selectedEndField, int guiId) {


        MovesManager movesManager = new MovesManager(selectedStartField, selectedEndField);
        if (movesManager.firstCheck()) {

            PieceColor pieceColor = selectedStartField.getPiece().getColor();
            Mediator currentPlayer;

            // Bierzemy instancje gui
            List<GUI> guiInstances = GUI.getGuiInstances();
            for(int i =0; i < guiInstances.size(); i++) {
                if(guiInstances.get(i).getGuiId() == guiId){        // Szukamy tej instancji, którą nacisnęliśmy
                    GUI currentGUI = guiInstances.get(i);
                    PieceColor guiColor = currentGUI.getColor();
                    // Jeśli wybrany kolor pionka jest inny niż przypisany do gui to nie możeny wykonać ruchu, bo to nie nasze GUI
                    if(!pieceColor.equals(guiColor)) {
                        System.err.println("It is not your GUI.");
                        return;
                    }
                }
            }

            // Użyj odpowiedniej metody w zależności od trybu gry
            if (getYinAndYangManager().isYinAndYangEnabled()) {
                currentPlayer = rulesManager. getPlayerByColorForYinAndYang(pieceColor);       // Uzyskanie gracza po kolore pionka dla Yin And Yang
            } else {
                currentPlayer = rulesManager.getPlayerByColor(pieceColor);      // Uzyskanie gracza po kolorze pionka gra klasyczna
            }

            if (currentPlayer == null) { // Obsługa przypadku, gdy gracza nie znaleziono
                System.err.println("Error: No player found for piece color: " + pieceColor);
                return;
            }

            if (!rulesManager.canPlayerMove(currentPlayer, selectedStartField)) {
                return; // Gracz nie ma prawa wykonać ruchu.
            }

            if (movesManager.isValidMove()) {
                movesManager.performMove(); // Oddelegowanie całej logiki ruchu do MovesManager

                int startX = selectedStartField.getRow();
                int startY = selectedStartField.getCol();
                int endX = selectedEndField.getRow();
                int endY = selectedEndField.getCol();

                gameDataService.recordMove(currentGame, startX, startY, endX, endY);     // Zapisanie ruchu do bazy danych

                rulesManager.nextPlayer(); // Przejście do kolejnego gracza

                // Sprawdzenie wygranej
                if (victoryManager.checkVictory(rulesManager.getPlayerColor(currentPlayer))) {

                    System.out.println("Checking victory for pieceColor: " + rulesManager.getPlayerColor(currentPlayer));

                    notifyObservers("Player with color " + rulesManager.getPlayerColor(currentPlayer) + " takes " + victoryManager.whichPlace() +" place.");
                    if(victoryManager.isEnd()) {
                        notifyObservers("End of the game.");
                        endGame();
                    }
                }

            } else {
                currentPlayer.sendMessage("Invalid move.");
            }
        } else {
            System.out.println("Invalid move.");
        }
    }

    /**
     * Kończy grę, ustawiając flagę gameEnded na true
     */
    public void endGame() {
        if (!gameEnded) {
            gameEnded = true;
            gameDataService.whenGameEnded(currentGame);        // Zapisanie w bazie ukończenia gry
        }
    }


    /**
     * Getter.
     **/
    public Mediator getCurrentPlayer() {
        return rulesManager.getCurrentPlayer();
    }

    /**
     * Getter.
     **/
    public RulesManager getRulesManager() {
        return rulesManager;
    }

    /**
     * Getter.
     **/
    public VictoryManager getVictoryManager() {
        return victoryManager;
    }

    private void showSavedGames(Mediator player) {
        List<Game> savedGames = gameDataService.getSavedGames();
        if (savedGames.isEmpty()) {
            player.sendMessage("No saved games available.");
            return;
        }
        StringBuilder message = new StringBuilder("Saved games:\n");
        for (Game game : savedGames) {
            message.append(String.format("ID: %d, Players: %d, Yin and Yang: %s\n",
                    game.getId(), game.getNumberOfPlayers(), game.isYingAndYangVariantEnabled() ? "Yes" : "No"));
        }
        player.sendMessage(message.toString());
    }

    private void replayGame(Mediator player, Long gameId) {
        List<Move> moves = gameDataService.getMovesForGame(gameId);
        if (moves.isEmpty()) {
            player.sendMessage("No moves found for the selected game.");
            return;
        }
        // Pobranie informacji o grze
        Game selectedGame = gameDataService.getGameById(gameId);
        if (selectedGame == null) {
            player.sendMessage("Game not found.");
            return;
        }

        int numberOfPlayers = selectedGame.getNumberOfPlayers();
        boolean isYinAndYang = selectedGame.isYingAndYangVariantEnabled();

        // Tworzenie planszy BigBoard
        BoardSetup bigBoard = new BigBoard(); // Zainicjuj planszę BigBoard
        bigBoard.boardGenerator();            // Upewnij się, że metoda poprawnie inicjalizuje planszę

        // Pobranie ustawień początkowych pionków
        List<Setup> setups = gameDataService.getInitialSetup(gameId);
        if (setups.isEmpty()) {
            player.sendMessage("No initial setup found for the selected game.");
            return;
        }

        // Odtworzenie ustawień początkowych na planszy
        for (Setup setup : setups) {
            Field field = bigBoard.getSpecificField(setup.getStartPositionX(), setup.getStartPositionY());
            if (field != null) {
                Piece piece = new Piece(PieceColor.valueOf(setup.getPieceColor()));
                field.setPiece(piece);
            }
        }

        // Uruchomienie GUIReplay
        GUIReplay.setBoard(bigBoard); // Ustaw planszę w GUIReplay
        GUIReplay.launchForReplay();


        player.sendMessage("Replaying game...");

        // Symulowanie ruchów
        GUIReplay replayInstance = new GUIReplay();
        replayInstance.setBoard(bigBoard);

        // Symulowanie ruchów w GUIReplay
        for (Move move : moves) {
            int startX = move.getStartPositionX();
            int startY = move.getStartPositionY();
            int endX = move.getEndPositionX();
            int endY = move.getEndPositionY();

            // Aktualizacja planszy w GUI
            Platform.runLater(() -> {
                GUIReplay.animateMove(startX, startY, endX, endY);
            });

            try {
                Thread.sleep(1000); // Opóźnienie między ruchami dla efektu animacji
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }
        player.sendMessage("Replay completed.");
    }

}