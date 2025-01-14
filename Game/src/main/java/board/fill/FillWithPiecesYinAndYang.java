package board.fill;

import board.Field;
import board.Piece;
import board.enums.HomeColor;
import board.enums.PieceColor;
import board.homes.DestinationHomeYinAndYang;
import server.ChooseBoard;
import java.util.*;

/**
 * Klasa odpowiedzialna za wypełnienie planszy pionkami w trybie Yin i Yang.
 * Obsługuje losowanie domków startowych, przypisywanie pionków oraz ustawianie domków docelowych.
 */
public class FillWithPiecesYinAndYang {
    private final List<Field> playingFields;        // Pola na planszy
    private final List<HomeColor> chosenHomes = new ArrayList<>();      // Wylosowane domki
    private final DestinationHomeYinAndYang destinationHome;         // Domki docelowe
    private final Map<PieceColor, HomeColor> pieceToHomeMapping = new HashMap<>();      // Mapowanie koloru pionków na domki

    /**
     * Konstruktor klasy FillWithPiecesYinAndYang.
     *
     * @param destinationHome Obiekt odpowiedzialny za zarządzanie domkami docelowymi.
     */
    public FillWithPiecesYinAndYang(DestinationHomeYinAndYang destinationHome) {
        this.playingFields = ChooseBoard.getInstance().getBoard().getFieldsInsideAStar();
        this.destinationHome = destinationHome;

        setupYinAndYang();  // Inicjalizacja logiki Yin and Yang
    }

    /**
     * Metoda odpowiedzialna za konfigurację trybu Yin i Yang.
     * Losuje domki startowe, przypisuje kolory pionków oraz ustawia domki docelowe.
     */
    private void setupYinAndYang() {
        // 1. Lista dostępnych domków
        List<HomeColor> availableHomes = new ArrayList<>(List.of(
                HomeColor.RED, HomeColor.BLUE, HomeColor.GREEN,
                HomeColor.YELLOW, HomeColor.PURPLE, HomeColor.BLACK
        ));

        // 2. Losowanie dwóch domków
        Collections.shuffle(availableHomes);
        HomeColor startHome1 = availableHomes.get(0);       //Domek dla pierwszego gracza
        HomeColor startHome2 = availableHomes.get(1);       //Domek dla drugiego gracza

        // 3. Przypisanie kolorów pionków do wylosowanych domków
        pieceToHomeMapping.put(PieceColor.BLACK_PIECE, startHome1);
        pieceToHomeMapping.put(PieceColor.YELLOW_PIECE, startHome2);

        // 4. Ustawienie domków docelowych
        destinationHome.attachDestinationHomes(startHome1, startHome2);

        // 5. Przypisywanie pionków do domków
        assignFieldsToPlayers(startHome1, startHome2);

        // Log diagnostyczny
        System.out.println("Start Home for BLACK_PIECE: " + startHome1);
        System.out.println("Start Home for YELLOW_PIECE: " + startHome2);
        System.out.println("Destination for BLACK_PIECE: " + destinationHome.getDestinationHomesMap().get(PieceColor.BLACK_PIECE));
        System.out.println("Destination for YELLOW_PIECE: " + destinationHome.getDestinationHomesMap().get(PieceColor.YELLOW_PIECE));
    }

    /**
     * Przypisuje pola na planszy do graczy na podstawie wylosowanych domków.
     *
     * @param startHome1 Domek startowy dla czarnych pionków.
     * @param startHome2 Domek startowy dla żółtych pionków.
     */
    private void assignFieldsToPlayers(HomeColor startHome1, HomeColor startHome2) {
        for (Field field : playingFields) {
            HomeColor fieldHomeColor = field.getHome();     // Pobierz kolor domku dla pola

            if (fieldHomeColor == startHome1) {
                // Domki startowe gracza 1 (czarne pionki)
                field.setPiece(new Piece(PieceColor.BLACK_PIECE));
            } else if (fieldHomeColor == startHome2) {
                // Domki startowe gracza 2 (żółte pionki)
                field.setPiece(new Piece(PieceColor.YELLOW_PIECE));
            } else if (isInDestinationHomes(field)) {
                // Domki docelowe – pozostają jako domki
                continue;
            } else if (fieldHomeColor != HomeColor.NONE) {
                // Pozostałe domki niewykorzystane – przekształcenie na normalne pola do gry
                field.setHome(HomeColor.NONE);
            }
        }
    }

    /**
     * Sprawdza, czy dane pole należy do jednego z domków docelowych.
     *
     * @param field Pole do sprawdzenia.
     * @return true, jeśli pole należy do domku docelowego, false w przeciwnym razie.
     */
    private boolean isInDestinationHomes(Field field) {
        for (List<Field> fields : destinationHome.getDestinationHomesMap().values()) {
            if (fields.contains(field)) {
                return true;        // Pole jest częścią domku docelowego
            }
        }
        return false;       // Pole nie należy do żadnego domku docelowego
    }

    /**
     * Pobiera mapowanie kolorów pionków na domki startowe.
     *
     * @return Mapowanie kolorów pionków na domki startowe.
     */
    public Map<PieceColor, HomeColor> getPieceToHomeMapping() {
        return pieceToHomeMapping;
    }

}