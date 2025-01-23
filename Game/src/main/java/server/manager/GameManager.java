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
import server.ChooseBoard;
import server.Mediator;
import server.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa GameMamager służąca do zarządzania grą
 */
public class GameManager {

    private static  volatile GameManager gameManagerInstance;   // Jedyna instancja klasy GameManager
    private final List<Observer> observers = new ArrayList<>(); // Lista obserwatorów
    private final List<Mediator> players = new ArrayList<>();   // Lista graczy
    private boolean gameStarted = false;
    private boolean gameEnded = false;
    private RulesManager rulesManager;                          // Zarządca zasad gry
    private VictoryManager victoryManager;                      // Zarządca wygranej
    private YinAndYangManager yinAndYangManager;

    /**
     * Prywatny konstruktor, ponieważ korzystamy ze wzorca projektowego Singleton
     */
    private GameManager(){ // Prywatny konstruktor
    }

    /**
     * Bezpieczna dla wielowątkowości metoda getInstance() zwracająca nam jedyną istniejącą w czasie gry
     * instancję GameManager
     *
     * @return instancję GameManager
     */
    public static GameManager getInstance() {
        // Zastosowanie double-checked locking. W wypadku gdy wiele wątków próbuje dostać się do instancji tej klasy
        GameManager gameManager = gameManagerInstance;
        if(gameManager != null) {
            return gameManager;
        }
        synchronized (GameManager.class) {
            if(gameManagerInstance == null) {
                gameManagerInstance = new GameManager();
            }
            return gameManagerInstance;
        }
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
        for (Observer observer : new ArrayList<>(observers)) {//new ArrayList<>(observers) - aby uniknąć ConcurrentModificationException
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
            Bot bot = new Bot(PieceColor.BLUE_PIECE); // Bot dostaje niebieskie pionki
            players.add(bot);
            addObserver(bot);
            notifyObservers("Bot dołączył do gry jako drugi gracz!");
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

        // Powiadom pierwszego gracza o jego ruchu
        rulesManager.getCurrentPlayer().sendMessage("It's your turn!");
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
            rulesManager.nextPlayer();          // Przejście do kolejnego gracza

            // Sprawdzenie wygranej
            if (victoryManager.checkVictory(rulesManager.getPlayerColor(player))) {

                System.out.println("Checking victory for pieceColor: " + rulesManager.getPlayerColor(player));

                notifyObservers("Player with color " + rulesManager.getPlayerColor(player) + " takes " + victoryManager.whichPlace() +" place.");
                if(victoryManager.isEnd()) {
                    notifyObservers("End of the game.");
                    gameEnded = true;
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
                rulesManager.nextPlayer(); // Przejście do kolejnego gracza
            } else {
                currentPlayer.sendMessage("Invalid move.");
            }
        } else {
            System.out.println("Invalid move.");
        }
    }
}