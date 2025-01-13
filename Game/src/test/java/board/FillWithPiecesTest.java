package board;

import board.enums.HomeColor;
import board.enums.PieceColor;
import board.fill.FillWIthPieces;
import board.mocks.MockBoardSetup;
import org.junit.jupiter.api.Test;
import server.ChooseBoard;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FillWithPiecesTest {

    private List<Field> mockFields;
    private FillWIthPieces fillWithPieces;

    void setUpForTwo() {
        mockFields = new ArrayList<>();         // Pola, które należą do planszy

        // Tworzymy planszę z polami
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                Field field = new Field(row, col);
                if (row == 0 && col < 3) {
                    field.setHome(HomeColor.RED);
                } else if (row == 4 && col > 1) {
                    field.setHome(HomeColor.BLUE);
                } else {
                    field.setHome(HomeColor.NONE);
                }
                mockFields.add(field);
            }
        }

        ChooseBoard.getInstance().setTestBoard(new MockBoardSetup(mockFields)); // Symulujemy planszę

    }

    // Sprawdzenie czy metoda fill() w klasie FillWIthPieces() działa poprawnie - testujemy dla dwóch graczy
    @Test
    void testFillForTwoPlayers() {
        setUpForTwo();
        fillWithPieces = new FillWIthPieces(2);

        fillWithPieces.fill();

        int redCount = 0, blueCount = 0;

        // Sprawdzamy, czy odpowiednie pola mają pionki, pionki na razie mają stać w domkach
        for (Field field : mockFields) {
            if (field.getHome() == HomeColor.RED) {
                assertNotNull(field.getPiece());
                assertEquals(PieceColor.RED_PIECE, field.getPiece().getColor());
                redCount++;
            } else if (field.getHome() == HomeColor.BLUE) {
                assertNotNull(field.getPiece());
                assertEquals(PieceColor.BLUE_PIECE, field.getPiece().getColor());
                blueCount++;
            } else {
                assertNull(field.getPiece());
                assertEquals(HomeColor.NONE, field.getHome());
            }
        }
        assertEquals(3, redCount);
        assertEquals(3, blueCount);
    }


    void setUpForThree() {
        mockFields = new ArrayList<>();         // Pola, które należą do planszy

        // Tworzymy planszę z polami
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Field field = new Field(row, col);
                if ((row == 0 && col == 0) || (row == 0 && col == 1)) {
                    field.setHome(HomeColor.RED);
                } else if ((row == 1 && col == 0) || (row == 1 && col == 1)) {
                    field.setHome(HomeColor.BLACK);
                } else if ((row == 2 && col == 0) || (row == 2 && col == 1)){
                    field.setHome(HomeColor.YELLOW);
                } else {
                    field.setHome(HomeColor.NONE);
                }
                mockFields.add(field);
            }
        }

        ChooseBoard.getInstance().setTestBoard(new MockBoardSetup(mockFields)); // Symulujemy planszę
    }

    // Sprawdzenie czy metoda fill() w klasie FillWIthPieces() działa poprawnie - testujemy dla dwóch graczy
    @Test
    void testFillForThreePlayers() {
        setUpForThree();
        fillWithPieces = new FillWIthPieces(3);

        fillWithPieces.fill();

        int redCount = 0, blackCount = 0, yellowCount = 0;

        // Sprawdzamy, czy odpowiednie pola mają pionki, pionki na razie mają stać w domkach
        for (Field field : mockFields) {
            if (field.getHome() == HomeColor.RED) {
                assertNotNull(field.getPiece());
                assertEquals(PieceColor.RED_PIECE, field.getPiece().getColor());
                redCount++;
            } else if (field.getHome() == HomeColor.BLACK) {
                assertNotNull(field.getPiece());
                assertEquals(PieceColor.BLACK_PIECE, field.getPiece().getColor());
                blackCount++;
            } else if (field.getHome() == HomeColor.YELLOW) {
                assertNotNull(field.getPiece());
                assertEquals(PieceColor.YELLOW_PIECE, field.getPiece().getColor());
                yellowCount++;
            } else {
                assertNull(field.getPiece());
                assertEquals(HomeColor.NONE, field.getHome());
            }
        }
        assertEquals(2, redCount);
        assertEquals(2, blackCount);
        assertEquals(2, yellowCount);
    }
}


