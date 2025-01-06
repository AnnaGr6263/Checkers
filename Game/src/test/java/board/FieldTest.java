package board;
import board.enums.PieceColor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FieldTest {

    @Test
    // Sprawdza poprawność operacji dodawania i usuwania pionka na polu.
    public void testSetAndRemovePiece() {

        // Tworzy obiekt Field (pole) o współrzędnych (0, 0)
        Field field = new Field(0, 0);

        // Tworzy obiekt Piece (pionek) w kolorze RED_PIECE
        Piece piece = new Piece(PieceColor.RED_PIECE);

        // Ustawia pionek na polu
        field.setPiece(piece);

        // Sprawdza, czy pionek został poprawnie dodany do pola
        assertTrue(field.hasPiece(), "Field should have a piece after calling setPiece.");

        // Usuwa pionek z pola
        field.removePiece();

        // Sprawdza, czy pole jest puste po usunięciu pionka
        assertFalse(field.hasPiece(), "Field should not have a piece after calling removePiece.");
    }
}
