package board;
import board.enums.HomeColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BigBoardTest {

    private BigBoard board;

    @BeforeEach
    public void setUp() {
        board = new BigBoard();
        board.boardGenerator();
    }

    @Test
    // Testowanie generowania planszy
    public void testBoardGeneration() {

        // Oczekiwana liczba pól w gwieździe (61 pól w środku + 6 ramion po 10 pól każde)
        int expectedFieldsInStar = 61 + 6 * 10;
        assertEquals(expectedFieldsInStar, board.getFieldsInsideAStar().size(),
                "Number of fields in the star should match the expected value.");
    }

    @Test
    // Testowanie generowania pól w rogach planszy
    public void testCornersGenerator() {

        // Sprawdzenie pól w czerwonym rogu (przykładowo)
        long redFields = board.getFieldsInsideAStar().stream()
                .filter(field -> field.getHome() == HomeColor.RED).count();
        assertEquals(10, redFields, "Red corner should contain exactly 10 fields.");
    }

    @Test
    // Testowanie konkretnego pola na planszy (w środku gwiazdy)
    public void testFieldNeighbours() {

        // Testowanie konkretnego pola w środku gwiazdy
        Field field = board.getSpecificField(8, 12);
        assertNotNull(field.getNeighbours(), "Neighbours should not be null.");
        assertFalse(field.getNeighbours().isEmpty(), "Field should have neighbours.");
    }

    @Test
    // Testowanie oznaczania pól w gwieździe
    public void testAllFieldsInStarAreMarkedCorrectly() {

        // Wszystkie pola w `fieldsInsideAStar` powinny być oznaczone jako "w gwieździe"
        boolean allFieldsMarked = board.getFieldsInsideAStar().stream().allMatch(Field::isInStar);
        assertTrue(allFieldsMarked, "All fields in the star should be marked as part of the star.");
    }
}
