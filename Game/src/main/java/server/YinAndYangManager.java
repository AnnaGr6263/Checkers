package server;

import board.DestinationHome;
import board.Field;
import board.enums.HomeColor;
import board.enums.PieceColor;

import java.util.List;
import java.util.Map;

public class YinAndYangManager {
    private final GameManager gameManager;
    private boolean isYinAndYangEnabled = false;

    public YinAndYangManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public boolean enableYinAndYang() {
        if (gameManager.getPlayers().size() != 2) {
            gameManager.notifyObservers("Cannot activate Yin and Yang. Exactly 2 players are required.");
            return false;
        }
        if (gameManager.isGameStarted()) {
            gameManager.notifyObservers("Cannot activate Yin and Yang. The game has already started.");
            return false;
        }

        isYinAndYangEnabled = true;
        gameManager.notifyObservers("Yin and Yang variant activated. Special rules will take effect on game start!");
        return true;
    }

    public boolean isYinAndYangEnabled() {
        return isYinAndYangEnabled;
    }

    public void notifyPlayersAboutHomesAndColors(Map<PieceColor, HomeColor> pieceToHome, DestinationHome destinationHome) {
        // Gracz 1
        gameManager.getPlayers().get(0).sendMessage("Your color is BLACK.");
        //dupa print do wywalenia
        gameManager.getPlayers().get(0).sendMessage("Your home color: " + pieceToHome.get(PieceColor.BLACK_PIECE).name());
        gameManager.getPlayers().get(0).sendMessage("Your destination home: " + getDestinationFieldsDescription(destinationHome.getDestinationHomesMap().get(PieceColor.BLACK_PIECE)));

        // Gracz 2
        gameManager.getPlayers().get(1).sendMessage("Your color is YELLOW.");
        //dupa print do wywalenia
        gameManager.getPlayers().get(1).sendMessage("Your home color: " + pieceToHome.get(PieceColor.YELLOW_PIECE).name());
        gameManager.getPlayers().get(1).sendMessage("Your destination home: " + getDestinationFieldsDescription(destinationHome.getDestinationHomesMap().get(PieceColor.YELLOW_PIECE)));
    }

    // Pomocnicza metoda do opisania pól domków docelowych -- do wywalenia potem
    private String getDestinationFieldsDescription(List<Field> fields) {
        if (fields == null || fields.isEmpty()) {
            return "No destination fields.";
        }
        StringBuilder description = new StringBuilder();
        for (Field field : fields) {
            description.append(String.format("(%d, %d) ", field.getRow(), field.getCol()));
        }
        return description.toString().trim();
    }

}
