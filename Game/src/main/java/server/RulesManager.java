package server;

import board.Field;
import board.enums.HomeColor;

import java.util.*;

public class RulesManager {

    private final List<Mediator> players; // Lista graczy
    private final Map<HomeColor, Mediator> homeAssignments; // Przypisanie domków do graczy
    private int currentPlayerIndex; // Indeks aktualnego gracza
    private boolean gameStarted; // Flaga informująca o rozpoczęciu gry

    public RulesManager(List<Mediator> players) {
        this.players = players;
        this.homeAssignments = new HashMap<>();
        this.currentPlayerIndex = 0; // Start od losowego gracza
        this.gameStarted = false;
    }

    // Rozpocznij grę i przypisz domki do graczy
    public void startGame() {
        if (gameStarted) {
            throw new IllegalStateException("Game has already started.");
        }

        assignHomes(); // Przypisanie domków do graczy
        shufflePlayers(); // Losowe ustawienie kolejności graczy
        gameStarted = true;
    }

    // Przypisz domki do graczy w zależności od liczby graczy
    private void assignHomes() {
        List<HomeColor> availableHomes = getHomesForPlayerCount(players.size());

        if (availableHomes == null) {
            throw new IllegalArgumentException("Invalid number of players: " + players.size());
        }

        for (int i = 0; i < players.size(); i++) {
            Mediator player = players.get(i);
            HomeColor home = availableHomes.get(i);
            homeAssignments.put(home, player); // Przypisanie gracza do domku
            player.sendMessage("Your home color: " + home.name());
        }
    }

    // Pobierz zestaw kolorów domków na podstawie liczby graczy
    private List<HomeColor> getHomesForPlayerCount(int playerCount) {
        switch (playerCount) {
            case 2:
                return Arrays.asList(HomeColor.RED, HomeColor.BLUE);
            case 3:
                return Arrays.asList(HomeColor.RED, HomeColor.YELLOW, HomeColor.BLACK);
            case 4:
                return Arrays.asList(HomeColor.RED, HomeColor.BLUE, HomeColor.BLACK, HomeColor.PURPLE);
            case 6:
                return Arrays.asList(HomeColor.RED, HomeColor.BLUE, HomeColor.YELLOW, HomeColor.GREEN, HomeColor.PURPLE, HomeColor.BLACK);
            default:
                return null; // Nieobsługiwana liczba graczy
        }
    }

    // Tasowanie graczy w celu ustalenia losowej kolejności
    private void shufflePlayers() {
        Collections.shuffle(players); // Losowe przetasowanie graczy
        currentPlayerIndex = 0; // Ustaw pierwszy gracz na indeks 0
    }

    // Pobierz gracza, który aktualnie wykonuje ruch
    public Mediator getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    // Przejdź do kolejnego gracza w kolejności
    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        Mediator currentPlayer = getCurrentPlayer();
        currentPlayer.sendMessage("It's your turn!");
    }

    // Sprawdź, czy gracz może wykonać ruch
    public boolean canPlayerMove(Mediator player, Field startField) {
        if (!gameStarted) {
            player.sendMessage("Game has not started yet.");
            return false;
        }

        if (player != getCurrentPlayer()) {
            player.sendMessage("It's not your turn!");
            return false;
        }

        if (startField == null) {
            player.sendMessage("Invalid move: The starting field does not exist.");
            return false;
        }

        if (!startField.hasPiece()) {
            player.sendMessage("Invalid move: The starting field is empty.");
            return false;
        }

        // Sprawdź, czy pionek na polu startowym należy do gracza
        HomeColor homeColor = startField.getHome();
        Mediator owner = homeAssignments.get(homeColor);

        if (owner == null || owner != player) {
            player.sendMessage("Invalid move: You can only move your own pieces!");
            return false;
        }

        return true; // Ruch jest poprawny
    }
}
