package board;

import board.enums.HomeColor;
import board.enums.PieceColor;
import server.ChooseBoard;

import java.util.*;

public class FillWithPiecesYinAndYang {
    private final List<Field> playingFields;
    private final List<HomeColor> chosenHomes = new ArrayList<>();
    private final DestinationHome destinationHome;

    public FillWithPiecesYinAndYang(DestinationHome destinationHome) {
        this.playingFields = ChooseBoard.getInstance().getBoard().getFieldsInsideAStar();
        this.destinationHome = destinationHome;

        setupYinAndYang();
    }

    private void setupYinAndYang() {
        // Lista dostępnych domków
        List<HomeColor> availableHomes = new ArrayList<>(List.of(
                HomeColor.RED, HomeColor.BLUE, HomeColor.GREEN,
                HomeColor.YELLOW, HomeColor.PURPLE, HomeColor.BLACK
        ));

        // Losowanie dwóch domków
        Collections.shuffle(availableHomes);
        HomeColor startHome1 = availableHomes.get(0);
        HomeColor startHome2 = availableHomes.get(1);

        // Dodanie domków do wybranych
        chosenHomes.add(startHome1);
        chosenHomes.add(startHome2);

        // Przypisanie domków docelowych do pionków
        destinationHome.getDestinationHomesMap().put(
                PieceColor.BLACK_PIECE, destinationHome.getList(startHome1.getOpposite())
        );
        destinationHome.getDestinationHomesMap().put(
                PieceColor.YELLOW_PIECE, destinationHome.getList(startHome2.getOpposite())
        );

        System.out.println("Chosen start homes: " + chosenHomes);
        System.out.println("Destination for BLACK_PIECE: " + destinationHome.getList(startHome1.getOpposite()));
        System.out.println("Destination for YELLOW_PIECE: " + destinationHome.getList(startHome2.getOpposite()));

        // Wypełnianie domków startowych pionkami
        for (Field field : playingFields) {
            if (field.getHome() == startHome1) {
                field.setPiece(new Piece(PieceColor.BLACK_PIECE)); // Czarny pionek
            } else if (field.getHome() == startHome2) {
                field.setPiece(new Piece(PieceColor.YELLOW_PIECE)); // Żółty pionek
            }
        }
    }

    public Map<PieceColor, HomeColor> getPieceToHomeMapping() {
        Map<PieceColor, HomeColor> pieceToHome = new HashMap<>();
        pieceToHome.put(PieceColor.BLACK_PIECE, chosenHomes.get(0));
        pieceToHome.put(PieceColor.YELLOW_PIECE, chosenHomes.get(1));
        return pieceToHome;
    }


    public List<HomeColor> getChosenHomes() {
        return chosenHomes;
    }
}

