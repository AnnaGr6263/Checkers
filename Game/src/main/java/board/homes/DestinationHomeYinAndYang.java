package board.homes;

import board.Field;
import board.enums.HomeColor;
import board.enums.PieceColor;
import server.ChooseBoard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DestinationHomeYinAndYang implements DestinationHomeInterface {

    private final Map<PieceColor, List<Field>> destinationHomesMap = new HashMap<>();

    public DestinationHomeYinAndYang() {
    }

    @Override
    public Map<PieceColor, List<Field>> getDestinationHomesMap() {
        return destinationHomesMap;
    }

    public void attachDestinationHomes(HomeColor startHome1, HomeColor startHome2) {
        // Ustawianie domków docelowych dla czarnych i żółtych pionków
        destinationHomesMap.put(PieceColor.BLACK_PIECE, getList(startHome1.getOpposite()));
        destinationHomesMap.put(PieceColor.YELLOW_PIECE, getList(startHome2.getOpposite()));
    }

    private List<Field> getList(HomeColor homeColor) {
        List<Field> neededFields = new ArrayList<>();
        List<Field> playingFields = ChooseBoard.getInstance().getBoard().getFieldsInsideAStar();

        for (Field field : playingFields) {
            if (field.getHome() == homeColor) {
                neededFields.add(field);
            }
        }
        return neededFields;
    }
}
