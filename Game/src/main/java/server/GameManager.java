package server;

import board.BoardSetup;
import board.Field;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    //private BoardSetup board;       // Plansza gry
    private final List<Observer> observers = new ArrayList<>(); // Lista obserwatorów
    private final List<Mediator> players = new ArrayList<>(); // Lista graczy
    private boolean gameStarted = false;

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    private void notifyObservers(String message) {
        for (Observer observer : new ArrayList<>(observers)) {
            observer.update(message);
        }
    }

    public synchronized boolean addPlayer(Mediator player) {
        if (gameStarted) {
            player.sendMessage("Game has already started. You cannot join.");
            return false;
        }
        if (players.size() >= 6) {
            player.sendMessage("Cannot join. Maximum 6 players allowed.");
            return false;
        }
        players.add(player);
        addObserver(player);
        notifyObservers("Player joined. Total players: " + players.size());
        return true;
    }

    public synchronized void handleCommand(Mediator player, String command) {

        String[] elementsOfCommand = command.split(" ");

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

    private void startGame(Mediator sender) {
        if (gameStarted) {
            sender.sendMessage("Game has already started.");
            return;
        }
        if (!ChooseBoard.getInstance().isBoardChosen()) {
            sender.sendMessage("Please choose a board before starting the game using the command 'choose board big'.");
            return;
        }
        if (players.size() != 2 && players.size() != 3 && players.size() != 4 && players.size() != 6) {
            sender.sendMessage("Game requires 2, 3, 4, or 6 players.");
            return;
        }
        gameStarted = true;
        notifyObservers("Game started with " + players.size() + " players on the chosen board!");
    }


    private void chooseBoard(Mediator player, String command) {
        if (gameStarted) {
            player.sendMessage("Cannot change the board. The game has already started.");
            return;
        }
        if (ChooseBoard.getInstance().isBoardChosen()) {
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


    private void processMove(Mediator player, String command) {
        if (!gameStarted) {
            player.sendMessage("Game has not started yet.");
            return;
        }

        String[] commandWithoutWordMove = command.split(" ");
        String move = commandWithoutWordMove[1];

        String[] moveParts = move.split("->");

        if (moveParts.length > 2) {
            player.sendMessage("Invalid move. Use pattern: move [0,16]x[0,24]->[0,16]x[0,24]");
        }

        String start = moveParts[0];
        String[] startCo = start.split("x");      // Współrzędne pola początkowego

        if (startCo.length > 2) {
            player.sendMessage("Invalid move. Use pattern: move [0,16]x[0,24]->[0,16]x[0,24]");
        }

        String rowStartField = startCo[0];
        int rowSF = Integer.parseInt(rowStartField);
        String colStartField = startCo[1];
        int colSF = Integer.parseInt(colStartField);

        String end = moveParts[1];
        String[] endCo = end.split("x");      // Współrzędne pola końcowego

        if (endCo.length > 2) {
            player.sendMessage("Invalid move. Use pattern: move [0,16]x[0,24]->[0,16]x[0,24]");
        }

        String rowEndField = endCo[0];
        int rowEF = Integer.parseInt(rowEndField);
        String colEndField = endCo[1];
        int colEF = Integer.parseInt(colEndField);


        try {
            Field startField = ChooseBoard.getInstance().getBoard().getSpecificField(rowSF, colSF);
            Field endField = ChooseBoard.getInstance().getBoard().getSpecificField(rowEF, colEF);

            if (startField.isInStar()) {
                if (endField.isInStar()) {
                    notifyObservers("Player move: " + move);
                } else {
                    player.sendMessage("End Field " + end + " is not inside the star-shaped board");
                }
            } else {
                player.sendMessage("Start Filed " + start + " is not inside the star-shaped board");
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
        }
    }
}