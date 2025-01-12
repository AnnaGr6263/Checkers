package server;
import board.*;
import board.enums.HomeColor;
import board.enums.PieceColor;
import javafx.application.Application;
import GUI.GUI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameManager {

    private final List<Observer> observers = new ArrayList<>(); // Lista obserwatorów
    private final List<Mediator> players = new ArrayList<>();   // Lista graczy
    private boolean gameStarted = false;
    private boolean gameEnded = false;
    private RulesManager rulesManager;                          // Zarządca zasad gry
    private VictoryManager victoryManager;                      // Zarządca wygranej
    private final YinAndYangManager yinAndYangManager = new YinAndYangManager(this);

    // Dodanie obserwatora
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    //informowanie obserwatorów
    public void notifyObservers(String message) {
        for (Observer observer : new ArrayList<>(observers)) {//new ArrayList<>(observers) - aby uniknąć ConcurrentModificationException
            observer.update(message);
        }
    }

    public List<Mediator> getPlayers() {
        return players;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public YinAndYangManager getYinAndYangManager() {
        return yinAndYangManager;
    }


    // Dodanie gracza
    public synchronized boolean addPlayer(Mediator player) {
        if (gameStarted) {
            player.sendMessage("Game has already started. You cannot join.");
            return false;
        }
        if (yinAndYangManager.isYinAndYangEnabled() && players.size() >= 2) {
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

    // Obsługa komend
    public synchronized void handleCommand(Mediator player, String command) {
        // Jeśli gra się skończyła nie obsuguje już żadnych komend
        if (gameEnded) {
            player.sendMessage("The game has ended. No more moves can be made.");
            return;
        }
        String[] elementsOfCommand = command.split(" "); // Podział komendy na elementy

        if (command.equalsIgnoreCase("yes")) {
            if (players.size() == 2 && !gameStarted) {
                yinAndYangManager.enableYinAndYang();
            } else {
                player.sendMessage("Cannot enable Yin and Yang. Ensure there are exactly 2 players and the game hasn't started.");
            }
        } else if(command.equals("join")) {
            addPlayer(player);
        } else if (command.equals("game start")) {
            startGame(player);
        } else if (elementsOfCommand[0].equals("choose") && elementsOfCommand[1].equals("board")) {
            chooseBoard(player, command);
        } else if (elementsOfCommand[0].equals("move")) {
            processMove(player, command);
        } else if (command.equals("skip")) {
            skipTurn(player);
        }
    }

    // Rozpoczęcie gry
    private void startGame(Mediator sender) {
        if (gameStarted) {
            sender.sendMessage("Game has already started.");
            return;
        }
        if (!ChooseBoard.getInstance().isBoardChosen()) { // Sprawdzenie, czy plansza została wybrana
            sender.sendMessage("Please choose a board before starting the game using the command 'choose board BigBoard'.");
            return;
        }
        if (players.size() != 2 && players.size() != 3 && players.size() != 4 && players.size() != 6) {
            sender.sendMessage("Game requires 2, 3, 4, or 6 players.");
            return;
        }
        gameStarted = true; // Ustawienie flagi rozpoczęcia gry
        notifyObservers("Game started with " + players.size() + " players on the chosen board!");

        DestinationHome destinationHome = new DestinationHome();
        destinationHome.attachDestinationHomes();

        if (yinAndYangManager.isYinAndYangEnabled()) {
            FillWithPiecesYinAndYang filler = new FillWithPiecesYinAndYang(destinationHome);
            yinAndYangManager.notifyPlayersAboutHomesAndColors(filler.getPieceToHomeMapping(), destinationHome);
        }

        victoryManager = new VictoryManager(destinationHome.getDestinationHomesMap(), players.size());
        rulesManager = new RulesManager(players, this);
        rulesManager.startGame();

        // Przekazanie instancji planszy do GUI
        BoardSetup currentBoard = ChooseBoard.getInstance().getBoard();
        System.out.println("Starting GUI...");
        new Thread(() -> {
            GUI.setBoard(currentBoard); // Przekazanie planszy
            GUI.setGameManager(this);
            Application.launch(GUI.class); // Uruchomienie GUI
        }).start();

        // Powiadom pierwszego gracza o jego ruchu
        rulesManager.getCurrentPlayer().sendMessage("It's your turn!");
    }


    // Wybór planszy
    private void chooseBoard(Mediator player, String command) {
        if (gameStarted) {
            player.sendMessage("Cannot change the board. The game has already started.");
            return;
        }
        if (ChooseBoard.getInstance().isBoardChosen()) { // Sprawdzenie, czy plansza została już wybrana
            player.sendMessage("Board has already been chosen.");
            return;
        }
        if (command.equals("choose board BigBoard")) {
            ChooseBoard.getInstance().choose(1);    // Wybór BigBoard
            notifyObservers("Board chosen: Big Board (16x24).");
        } else {
            player.sendMessage("Invalid board selection. Use 'choose board BigBoard'.");
        }
    }

    // Obsługa ruchu
    private void processMove(Mediator player, String command) {
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

        MovesManager movesManager = new MovesManager(this, startField, endField);
        if(movesManager.isValidMove()) {
            movesManager.performMove();         // Oddelegowanie całej logiki ruchu do MovesManager
            rulesManager.nextPlayer();          // Przejście do kolejnego gracza

            // Sprawdzenie wygranej
            if (victoryManager.checkVictory(rulesManager.getPlayerColor(player))) {
                notifyObservers("Player with color " + rulesManager.getPlayerColor(player) + " takes " + victoryManager.whichPlace() +" place.");
                if(victoryManager.isEnd()) {
                    notifyObservers("End of the game.");
                    gameEnded = true;
                }
            }
        } else {
            player.sendMessage("Invalid move");
        }
    }

    // Pominięcie ruchu
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
}