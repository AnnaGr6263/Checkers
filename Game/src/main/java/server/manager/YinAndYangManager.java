package server.manager;

import board.Field;
import board.enums.HomeColor;
import board.enums.PieceColor;
import board.homes.DestinationHomeYinAndYang;
import server.Mediator;

import java.util.List;
import java.util.Map;

public class YinAndYangManager {
    private final GameManager gameManager = GameManager.getInstance();
    private boolean isYinAndYangEnabled = false;

    public YinAndYangManager( ) {
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

        Mediator blackPlayer = gameManager.getPlayers().get(0);
        Mediator yellowPlayer = gameManager.getPlayers().get(1);

        System.out.println("Black Player assigned to BLACK_PIECE: " + blackPlayer);
        System.out.println("Yellow Player assigned to YELLOW_PIECE: " + yellowPlayer);

        isYinAndYangEnabled = true; // Aktywacja Yin and Yang
        gameManager.notifyObservers("Yin and Yang variant activated. Special rules will take effect on game start!");
        return true;
    }

    public boolean isYinAndYangEnabled() {
        return isYinAndYangEnabled;
    }

    public void notifyPlayersAboutHomesAndColors(Map<PieceColor, HomeColor> pieceToHome, DestinationHomeYinAndYang destinationHome) {
        gameManager.getPlayers().get(0).sendMessage("Your color is BLACK.");
        gameManager.getPlayers().get(0).sendMessage("Your home color: " + pieceToHome.get(PieceColor.BLACK_PIECE).name());
        gameManager.getPlayers().get(0).sendMessage("Your destination home: " + getDestinationFieldsDescription(destinationHome.getDestinationHomesMap().get(PieceColor.BLACK_PIECE)));

        gameManager.getPlayers().get(1).sendMessage("Your color is YELLOW.");
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
