package server.manager;

import board.enums.HomeColor;
import board.enums.PieceColor;
import board.homes.DestinationHomeYinAndYang;
import server.Mediator;
import java.util.Map;

/**
 * Klasa zarządzająca trybem Yin and Yang w grze.
 * Odpowiada za aktywację trybu oraz komunikację z graczami
 * w związku z przypisaniem kolorów oraz specjalnych zasad.
 */
public class YinAndYangManager {
    private final GameManager gameManager = GameManager.getInstance();
    private boolean isYinAndYangEnabled = false;        // Flaga informująca o aktywacji Yin and Yang
    private Map<PieceColor, HomeColor> pieceToHomeMapping;      // Mapowanie koloru pionka na dom startowy.

    /**
     * Konstruktor klasy YinAndYangManager.
     */
    public YinAndYangManager( ) {}

    /**
     * Próbuje aktywować tryb Yin and Yang w grze.
     *
     * @return true, jeśli aktywacja się powiodła, false w przeciwnym razie.
     */
    public boolean enableYinAndYang() {
        // Sprawdzenie, czy w grze jest dokładnie dwóch graczy.
        if (gameManager.getPlayers().size() != 2) {
            gameManager.notifyObservers("Cannot activate Yin and Yang. Exactly 2 players are required.");
            return false;
        }
        // Sprawdzenie, czy gra nie została już rozpoczęta.
        if (gameManager.isGameStarted()) {
            gameManager.notifyObservers("Cannot activate Yin and Yang. The game has already started.");
            return false;
        }

        Mediator blackPlayer = gameManager.getPlayers().get(0);         // Przypisanie pierwszego gracza do czarnego koloru.
        Mediator yellowPlayer = gameManager.getPlayers().get(1);        // Przypisanie drugiego gracza do żółtego koloru.


        // log diagnostyczny
        System.out.println("Black Player assigned to BLACK_PIECE: " + blackPlayer);
        System.out.println("Yellow Player assigned to YELLOW_PIECE: " + yellowPlayer);

        isYinAndYangEnabled = true; // Aktywacja Yin and Yang
        gameManager.notifyObservers("Yin and Yang variant activated. Special rules will take effect on game start!");
        return true;
    }

    /**
     * Sprawdza, czy tryb Yin and Yang jest aktywny.
     *
     * @return true, jeśli tryb jest aktywny, false w przeciwnym razie.
     */
    public boolean isYinAndYangEnabled() {
        return isYinAndYangEnabled;
    }

    /**
     * Powiadamia graczy o przypisanych kolorach pionków oraz domach startowych.
     *
     * @param pieceToHome     Mapowanie koloru pionka na dom startowy.
     * @param destinationHome Obiekt celu dla graczy w trybie Yin and Yang.
     */
    public void notifyPlayersAboutHomesAndColors(Map<PieceColor, HomeColor> pieceToHome, DestinationHomeYinAndYang destinationHome) {
        this.pieceToHomeMapping = pieceToHome;

        gameManager.getPlayers().get(0).sendMessage("Your color is BLACK.");
        gameManager.getPlayers().get(1).sendMessage("Your color is YELLOW.");
    }

    /**
     * Pobiera mapowanie koloru pionka na dom startowy.
     *
     * @return Mapowanie koloru pionka na dom startowy.
     */
    public Map<PieceColor, HomeColor> getPieceToHomeMapping() {
        return pieceToHomeMapping;
    }
}
