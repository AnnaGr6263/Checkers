package board.homes;

import board.Field;
import board.enums.HomeColor;
import board.enums.PieceColor;
import server.ChooseBoard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klasa odpowiedzialna za zarządzanie domkami docelowymi w trybie Yin i Yang.
 * Obsługuje przypisanie domków docelowych do odpowiednich pionków na podstawie konfiguracji gry.
 */
public class DestinationHomeYinAndYang implements DestinationHomeInterface {

    // Mapa przypisująca kolory pionków do list pól, które stanowią ich domki docelowe.
    private final Map<PieceColor, List<Field>> destinationHomesMap = new HashMap<>();

    /**
     * Konstruktor klasy DestinationHomeYinAndYang.
     * Tworzy pustą mapę domków docelowych.
     */
    public DestinationHomeYinAndYang() {
    }

    /**
     * Zwraca mapę domków docelowych, gdzie kluczem jest kolor pionka,
     * a wartością lista pól stanowiących domki docelowe.
     *
     * @return Mapa domków docelowych.
     */
    @Override
    public Map<PieceColor, List<Field>> getDestinationHomesMap() {
        return destinationHomesMap;
    }

    /**
     * Przypisuje domki docelowe do pionków w trybie Yin i Yang.
     * Każdy kolor pionka otrzymuje listę pól, które stanowią jego domki docelowe.
     *
     * @param startHome1 Domek startowy dla pierwszego gracza (czarnych pionków).
     * @param startHome2 Domek startowy dla drugiego gracza (żółtych pionków).
     * @throws IllegalStateException jeśli domki docelowe nie są poprawnie zainicjalizowane.
     */
    public void attachDestinationHomes(HomeColor startHome1, HomeColor startHome2) {
        // Pobranie list pól dla domków docelowych, które są przeciwne do domków startowych.
        List<Field> blackDestination = getList(startHome1.getOpposite());
        List<Field> yellowDestination = getList(startHome2.getOpposite());

        // Walidacja poprawności inicjalizacji domków docelowych.
        if (blackDestination == null || blackDestination.isEmpty()) {
            throw new IllegalStateException("Destination fields for BLACK_PIECE are not properly initialized.");
        }
        if (yellowDestination == null || yellowDestination.isEmpty()) {
            throw new IllegalStateException("Destination fields for YELLOW_PIECE are not properly initialized.");
        }

        // Przypisanie domków docelowych do odpowiednich kolorów pionków.
        destinationHomesMap.put(PieceColor.BLACK_PIECE, blackDestination);
        destinationHomesMap.put(PieceColor.YELLOW_PIECE, yellowDestination);
    }

    /**
     * Pobiera listę pól należących do określonego koloru domku.
     *
     * @param homeColor Kolor domku, dla którego mają być zwrócone pola.
     * @return Lista pól przypisana do danego koloru domku.
     */
    private List<Field> getList(HomeColor homeColor) {
        List<Field> neededFields = new ArrayList<>();
        List<Field> playingFields = ChooseBoard.getInstance().getBoard().getFieldsInsideAStar();

        // Iteracja przez wszystkie pola i dodanie tych, które należą do określonego koloru domku.
        for (Field field : playingFields) {
            if (field.getHome() == homeColor) {
                neededFields.add(field);
            }
        }
        return neededFields;
    }
}
