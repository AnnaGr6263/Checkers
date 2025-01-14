package board;

import board.enums.PieceColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PieceTest {
    @Test
    void constructorShouldInitializeColor() {
        // Given
        PieceColor expectedColor = PieceColor.RED_PIECE;

        // When
        Piece piece = new Piece(expectedColor);

        // Then
        assertNotNull(piece.getColor(), "Piece color should not be null");
        assertEquals(expectedColor, piece.getColor(), "Piece color should match the expected color");
    }

    @Test
    void getColorShouldReturnCorrectColor() {
        // Given
        PieceColor expectedColor = PieceColor.BLUE_PIECE;

        // When
        Piece piece = new Piece(expectedColor);

        // Then
        assertEquals(expectedColor, piece.getColor(), "getColor should return the correct color");
    }
}
