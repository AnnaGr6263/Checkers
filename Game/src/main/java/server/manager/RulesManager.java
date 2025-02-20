package server.manager;
import GUI.GUI;
import board.Field;
import board.enums.HomeColor;
import board.enums.PieceColor;
import bot.Bot;
import javafx.application.Platform;
import server.Mediator;

import java.util.*;

/**
 * Klasa odpowiedzialna za zarządzanie grą w kontekście zasad wykonywania ruchów. Tzn. zarządzanie kolejnością.
 */
public class RulesManager {

    private final List<Mediator> players; // Lista graczy
    private final Map<HomeColor, Mediator> homeAssignments; // Przypisanie domków do graczy
    private int currentPlayerIndex; // Indeks aktualnego gracza
    private boolean gameStarted; // Flaga informująca o rozpoczęciu gry
    private final GameManager gameManager = GameManager.getInstance(); // Instancja klasy GameManager

    /**
     * Konstruktor.
     *
     * @param players Lista zawierająca graczy w grze.
     */
    public RulesManager(List<Mediator> players) {
        this.players = players;
        this.homeAssignments = new HashMap<>();
        this.currentPlayerIndex = 0; // Start od losowego gracza
        this.gameStarted = false;
    }

    /**
     * Rozpoczęcie gry i przypisanie graczom ich domków.
     */
    public void startGame() {
        if (gameStarted) {
            throw new IllegalStateException("Game has already started.");
        }

        if (gameManager.getYinAndYangManager().isYinAndYangEnabled()) {
            setupYinAndYangAssignments();
        } else {
            assignHomes(); // Przypisanie domków do graczy tylko w trybie klasycznym
            shufflePlayers(); // Losowe ustawienie kolejności graczy
        }

        gameStarted = true;
    }

    /**
     * Przypisuje domki do graczy w trybie Yin and Yang
     */
    private void setupYinAndYangAssignments() {
        Map<PieceColor, HomeColor> pieceToHomeMapping = gameManager.getYinAndYangManager().getPieceToHomeMapping();

        for (Map.Entry<PieceColor, HomeColor> entry : pieceToHomeMapping.entrySet()) {
            Mediator player = getPlayerByColorForYinAndYang(entry.getKey());
            if (player != null) {
                homeAssignments.put(entry.getValue(), player);
            } else {
                throw new IllegalStateException("Player not found for piece color: " + entry.getKey());
            }
        }
    }

    /**
     * Przypisuje domki do graczy w zależności od liczby graczy
     */
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

    /**
     * Pobiera zestaw kolorów domków na podstawie liczby graczy.
     *
     * @param playerCount liczba graczy.
     * @return Listę kolorów w grze.
     */
    public List<HomeColor> getHomesForPlayerCount(int playerCount) {
        switch (playerCount) {
            case 1:
                return Arrays.asList(HomeColor.RED);
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

    /**
     * Tasowanie graczy w celu ustalenia losowej kolejności.
     */
    private void shufflePlayers() {
        Collections.shuffle(players); // Losowe przetasowanie graczy
        currentPlayerIndex = 0; // Ustaw pierwszy gracz na indeks 0
    }

    /**
     * Pobieranie gracza, który aktualnie wykonuje ruch.
     *
     * @return Gracz, który aktualnie wykonuje ruch.
     */
    public Mediator getCurrentPlayer() {
        System.out.println("Current player index: " + currentPlayerIndex);
        return players.get(currentPlayerIndex);
    }

    /**
     * Przejście do kolejnego gracza.
     */
    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        Mediator currentPlayer = getCurrentPlayer();
        System.out.println("Next player is: " + currentPlayer);
        if (currentPlayer instanceof Bot) {     // Jeśli gracz to bot, to wykonaj ruch
            ((Bot) currentPlayer).makeMove();   // Wykonaj ruch bota
            Platform.runLater(() -> GUI.refreshAll());
        } else {
            currentPlayer.sendMessage("It's your turn!");
        }
    }

    /**
     * Sprawdza czy gracz może wykonać ruch.
     *
     * @param player Gracz wykonujacy ruch.
     * @param startField Wybrane przez niego pole startowe.
     * @return Prawdę jeśli gracz może wykonać ruch i fałsz w przeciwnym razie.
     */
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

        PieceColor pieceColor = startField.getPiece().getColor();
        System.out.println("Player: " + player + ", PieceColor: " + pieceColor);

        if (pieceColor == null) {
            player.sendMessage("Invalid move: The piece has no color.");
            return false;
        }

        // Sprawdź tryb Yin and Yang
        if (gameManager.getYinAndYangManager().isYinAndYangEnabled()) {
            return playerOwnsPieceInYinAndYang(player, pieceColor);
        }

        // Sprawdź klasyczny tryb gry
        return playerOwnsPiece(player, pieceColor);
    }

    /**
     * Sprawsza czy konretny gracz jest właścicielem pionka o określonym kolorze.
     *
     * @param player Gracz.
     * @param pieceColor Kolor pionka.
     * @return Prawdę jeśli gracz jest posiadaczem pionków w takim kolorze i fałsz w przeciwnym razie.
     */
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

    /**
     * Sprawsza czy konretny gracz jest właścicielem pionka o określonym kolorze w wariancie gry YingAndYang.
     *
     * @param player Gracz.
     * @param pieceColor Kolor pionka.
     * @return Prawdę jeśli gracz jest posiadaczem pionków w takim kolorze i fałsz w przeciwnym razie.
     */
    private boolean playerOwnsPieceInYinAndYang(Mediator player, PieceColor pieceColor) {
        Mediator owner = getPlayerByColorForYinAndYang(pieceColor); // Pobierz gracza, który posiada dany kolor pionka
        if (owner != null && owner.equals(player)) {
            return true; // Gracz jest właścicielem pionka
        }
        player.sendMessage("Invalid move: You can only move your own pieces in Yin and Yang!");
        return false;
    }

    /**
     * Pobierz kolor pionków gracza na podstawie przypisanego domku.
     *
     * @param player Gracz.
     * @return Kolor pionka.
     */
    public PieceColor getPlayerColor(Mediator player) {
        HomeColor playerHome = null;

        // Znajdź HomeColor przypisany do gracza
        for (Map.Entry<HomeColor, Mediator> entry : homeAssignments.entrySet()) {
            if (entry.getValue().equals(player)) {
                playerHome = entry.getKey();
                break;
            }
        }

        if (playerHome == null) {
            throw new IllegalStateException("Player home not found for player: " + player);
        }

        if (gameManager.getYinAndYangManager().isYinAndYangEnabled()) {
            for (Map.Entry<PieceColor, HomeColor> entry : gameManager.getYinAndYangManager().getPieceToHomeMapping().entrySet()) {
                if (entry.getValue().equals(playerHome)) {
                    return entry.getKey();
                }
            }
            return null;
        } else {
            return mapHomeColorToPieceColor(playerHome);
        }
    }

    /**
     * Pobiera gracza na podstawie używanych przez niego pionków o określonym kolorze.
     *
     * @param pieceColor Kolor pionków.
     * @return Gracza.
     */
    public Mediator getPlayerByColor(PieceColor pieceColor) {
        // Iterujemy po przypisaniach HomeColor do Mediator
        for (Map.Entry<HomeColor, Mediator> entry : homeAssignments.entrySet()) {
            HomeColor homeColor = entry.getKey();
            Mediator mediator = entry.getValue();

            // Mapujemy HomeColor na PieceColor
            if (mapHomeColorToPieceColor(homeColor).equals(pieceColor)) {
                return mediator; // Znaleziono gracza, zwracamy Mediatora
            }
        }

        // Jeśli nie znaleziono żadnego gracza, zwracamy null
        return null;
    }

    /**
     * Pobranie gracza za pomocą jego koloru pionka. Metoda potrzebna przy YingAndYang
     *
     * @param pieceColor Kolor pionka.
     * @return Gracza o określonym kolorze pionka.
     */
    public Mediator getPlayerByColorForYinAndYang(PieceColor pieceColor) {
        if (pieceColor == PieceColor.BLACK_PIECE) {
            return players.get(0); // Zakładamy, że gracz z czarnymi pionkami jest pierwszy na liście
        } else if (pieceColor == PieceColor.YELLOW_PIECE) {
            return players.get(1); // Gracz z żółtymi pionkami jest drugi na liście
        }
        return null; // Dla innych kolorów
    }

    /**
     * Metoda do mapowania HomeColor na PieceColor.
     * Przypisuje kolorowi domku odpowiadający mu kolor pionków.
     *
     * @param homeColor Kolor domku.
     * @return Kolor pionków.
     */
    public PieceColor mapHomeColorToPieceColor(HomeColor homeColor) {
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