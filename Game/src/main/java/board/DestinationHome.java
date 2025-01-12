package board;

import board.enums.HomeColor;
import board.enums.PieceColor;
import server.ChooseBoard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klasa odpowiedzialna za przypisywanie dla danego koloru pionka (typ enum) jego pól docelowych.
 */
public class DestinationHome {

    private final Map<PieceColor, List<Field>> destinationHomesMap = new HashMap<>();

    /**
     * Konstruktor.
     */
    public DestinationHome() {
    }

    /**
     * Getter.
     *
     * @return Zwraca mapę, w której dla każdego koloru pionka jest przypisana Lista pól docelowych.
     */
    public Map<PieceColor, List<Field>> getDestinationHomesMap() {
        return destinationHomesMap;
    }

    /**
     * Przypisuje kolorom pionków ich pola docelowe.
     */
    public void attachDestinationHomes() {
        destinationHomesMap.put(PieceColor.RED_PIECE, getList(HomeColor.RED.getOpposite()));   // Czerwony idzie na wprost itd.
        destinationHomesMap.put(PieceColor.BLUE_PIECE, getList(HomeColor.BLUE.getOpposite()));
        destinationHomesMap.put(PieceColor.BLACK_PIECE, getList(HomeColor.BLACK.getOpposite()));
        destinationHomesMap.put(PieceColor.PURPLE_PIECE, getList(HomeColor.PURPLE.getOpposite()));
        destinationHomesMap.put(PieceColor.GREEN_PIECE, getList(HomeColor.GREEN.getOpposite()));
        destinationHomesMap.put(PieceColor.YELLOW_PIECE, getList(HomeColor.YELLOW.getOpposite()));
    }

    /**
     * Metoda pomocnicza. Służy do pobierania listy pól o danym kolorze domku.
     *
     * @param homeColor Kolor domku.
     * @return Listę pól, które należą do danego domku.
     */
    private List<Field> getList(HomeColor homeColor) {
        List<Field> neededFields = new ArrayList<>();           // Do przechowania pól o określonym kolorze domku
        List<Field> playingFields = ChooseBoard.getInstance().getBoard().getFieldsInsideAStar();

        for(Field field : playingFields) {
            if(field.getHome() == homeColor) {
                neededFields.add(field);
            }
        }
        return neededFields;
    }
}
