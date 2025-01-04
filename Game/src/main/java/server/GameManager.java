package server;

import board.BoardSetup;
import board.Field;
import board.FillWIthPieces;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    private final List<Observer> observers = new ArrayList<>(); // Lista obserwatorów
    private final List<Mediator> players = new ArrayList<>(); // Lista graczy
    private boolean gameStarted = false;

    // Dodanie obserwatora
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    //informowanie obserwatorów
    private void notifyObservers(String message) {
        for (Observer observer : new ArrayList<>(observers)) {//new ArrayList<>(observers) - aby uniknąć ConcurrentModificationException
            observer.update(message);
        }
    }

    // Dodanie gracza
    public synchronized boolean addPlayer(Mediator player) {
        if (gameStarted) {
            player.sendMessage("Game has already started. You cannot join.");
            return false;
        }
        if (players.size() >= 6) {
            player.sendMessage("Cannot join. Maximum 6 players allowed.");
            return false;
        }
        players.add(player); // Dodanie gracza do listy
        addObserver(player); // Dodanie gracza jako obserwatora
        notifyObservers("Player joined. Total players: " + players.size());
        return true;
    }

    // Obsługa komend
    public synchronized void handleCommand(Mediator player, String command) {

        String[] elementsOfCommand = command.split(" "); // Podział komendy na elementy

        if(command.equals("join")) {
            addPlayer(player);
        } else if (command.equals("game start")) {
            startGame(player);
        } else if (elementsOfCommand[0].equals("choose") && elementsOfCommand[1].equals("board")) {
            chooseBoard(player, command);
        } else if (elementsOfCommand[0].equals("move")) {
            processMove(player, command);
        } else {
            player.sendMessage("Invalid command: " + command);
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
        // Jak już mamy określoną liczbę graczy to wypałniamy odpowiednio pionami konkretne domki
        FillWIthPieces fillWIthPieces = new FillWIthPieces(players);
        fillWIthPieces.fill();
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
        // Utworzenie obiektu MovesManager
        MovesManager movesManager = new MovesManager(player, command);

        movesManager.performMove();         // Oddelegowanie całej logiki ruchu do MovesManager
    }
}