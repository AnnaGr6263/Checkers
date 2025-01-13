package board.fill;

import board.Field;
import board.Piece;
import board.enums.HomeColor;
import board.enums.PieceColor;
import board.homes.DestinationHomeYinAndYang;
import server.ChooseBoard;

import java.util.*;

public class FillWithPiecesYinAndYang {
    private final List<Field> playingFields;  // Pola na planszy
    private final List<HomeColor> chosenHomes = new ArrayList<>();  // Wylosowane domki
    private final DestinationHomeYinAndYang destinationHome;  // Domki docelowe
    private final Map<PieceColor, HomeColor> pieceToHomeMapping = new HashMap<>();

    public FillWithPiecesYinAndYang(DestinationHomeYinAndYang destinationHome) {
        this.playingFields = ChooseBoard.getInstance().getBoard().getFieldsInsideAStar();
        this.destinationHome = destinationHome;

        setupYinAndYang();  // Inicjalizacja logiki Yin and Yang
    }

    private void setupYinAndYang() {
        // 1. Lista dostępnych domków
        List<HomeColor> availableHomes = new ArrayList<>(List.of(
                HomeColor.RED, HomeColor.BLUE, HomeColor.GREEN,
                HomeColor.YELLOW, HomeColor.PURPLE, HomeColor.BLACK
        ));

        // 2. Losowanie dwóch domków
        Collections.shuffle(availableHomes);
        HomeColor startHome1 = availableHomes.get(0);
        HomeColor startHome2 = availableHomes.get(1);

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

    private void assignFieldsToPlayers(HomeColor startHome1, HomeColor startHome2) {
        for (Field field : playingFields) {
            HomeColor fieldHomeColor = field.getHome(); // Kolor domku pola

            if (fieldHomeColor == startHome1) {
                // Domki startowe gracza 1 (czarne pionki)
                field.setPiece(new Piece(PieceColor.BLACK_PIECE));
                System.out.println("Assigned BLACK_PIECE to field: " + field);

            } else if (fieldHomeColor == startHome2) {
                // Domki startowe gracza 2 (żółte pionki)
                field.setPiece(new Piece(PieceColor.YELLOW_PIECE));
                System.out.println("Assigned YELLOW_PIECE to field: " + field);

            } else if (isInDestinationHomes(field)) {
                // Domki docelowe – pozostają jako domki
                System.out.println("Field is a destination home, skipping: " + field);
                continue;
            } else if (fieldHomeColor != HomeColor.NONE) {
                // Pozostałe domki niewykorzystane – przekształcenie na normalne pola do gry
                field.setHome(HomeColor.NONE);
                System.out.println("Set field to NONE: " + field);
            }
        }
    }

    // Pomocnicza metoda sprawdzająca, czy dane pole należy do domków docelowych
    private boolean isInDestinationHomes(Field field) {
        for (List<Field> fields : destinationHome.getDestinationHomesMap().values()) {
            if (fields.contains(field)) {
                return true; // Pole znajduje się w jednej z list domków docelowych
            }
        }
        return false; // Pole nie jest częścią żadnego z domków docelowych
    }

    // Pobieranie mapowania pionków na domki startowe
    public Map<PieceColor, HomeColor> getPieceToHomeMapping() {
        return pieceToHomeMapping;
    }

}