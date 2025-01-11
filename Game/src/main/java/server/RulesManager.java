package server;
import board.Field;
import board.enums.HomeColor;
import board.enums.PieceColor;

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

        // Sprawdź, czy pionek na polu należy do gracza
        PieceColor pieceColor = startField.getPiece().getColor();

        if (!playerOwnsPiece(player, pieceColor)) {
            player.sendMessage("Invalid move: You can only move your own pieces!");
            return false;
        }

        return true; // Ruch jest poprawny
    }

    private boolean playerOwnsPiece(Mediator player, PieceColor pieceColor) {
        HomeColor playerHome = null;

        // Znajdź HomeColor przypisany do gracza
        for (Map.Entry<HomeColor, Mediator> entry : homeAssignments.entrySet()) {
            if (entry.getValue().equals(player)) {
                playerHome = entry.getKey();
                break;
            }
        }

        if (playerHome == null) return false;

        return pieceColor == mapHomeColorToPieceColor(playerHome);
    }

    // Pobierz kolor gracza na podstawie przypisanego domku
    public PieceColor getPlayerColor(Mediator player) {
        for (Map.Entry<HomeColor, Mediator> entry : homeAssignments.entrySet()) {
            if (entry.getValue().equals(player)) {
                return mapHomeColorToPieceColor(entry.getKey());  // Zwraca kolor gracza
            }
        }
        return null;
    }
    // Metoda pomocnicza do mapowania HomeColor na PieceColor
    private PieceColor mapHomeColorToPieceColor(HomeColor homeColor) {
        switch (homeColor) {
            case RED:
                return PieceColor.RED_PIECE;
            case BLUE:
                return PieceColor.BLUE_PIECE;
            case GREEN:
                return PieceColor.GREEN_PIECE;
            case BLACK:
                return PieceColor.BLACK_PIECE;
            case YELLOW:
                return PieceColor.YELLOW_PIECE;
            case PURPLE:
                return PieceColor.PURPLE_PIECE;
            default:
                throw new IllegalArgumentException("Invalid HomeColor: " + homeColor);
        }
    }
}
