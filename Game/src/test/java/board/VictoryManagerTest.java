package board;

import board.enums.PieceColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.VictoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class VictoryManagerTest {

    private VictoryManager victoryManager;
    private Map<PieceColor, List<Field>> destinationHomesMap;
    private int numberOfPlayers;

    @BeforeEach
    public void setUp() {
        numberOfPlayers = 3;

        // Symulacja pól docelowych dla każdego koloru
        destinationHomesMap = new HashMap<>();
        int row = 0;
        for (PieceColor color : PieceColor.values()) {
            List<Field> fields = new ArrayList<>();
            for (int col = 0; col < 4; col++) { // Załóżmy, że każdy kolor ma 4 pola docelowe
                fields.add(new Field(row, col));
            }
            destinationHomesMap.put(color, fields);
            row++;
        }
        victoryManager = new VictoryManager(destinationHomesMap, numberOfPlayers);
    }

    @Test
    void testCheckVictoryWhenAllPiecesAreInDestinationHome() {
        PieceColor color = PieceColor.RED_PIECE;
        List<Field> fields = destinationHomesMap.get(color);

        for (Field field : fields) {
            field.setPiece(new Piece(color)); // Ustaw pionki na miejcach docelowych
        }
        assertTrue(victoryManager.checkVictory(color), "Should return true when all fields are occupied by correct pieces.");
    }

    @Test
    void testCheckVictoryWhenSomeFieldsEmpty() {
        PieceColor color = PieceColor.RED_PIECE;
        List<Field> fields = destinationHomesMap.get(color);

        fields.get(0).setPiece(new Piece(color)); // Tylko jedno pole ma pionek

        assertFalse(victoryManager.checkVictory(color), "Should return false when some fields are empty.");
    }

    @Test
    void testCheckVictoryWhenFieldOccupiedByDifferentColor() {
        PieceColor color = PieceColor.RED_PIECE;
        List<Field> fields = destinationHomesMap.get(color);

        fields.get(0).setPiece(new Piece(PieceColor.BLUE_PIECE));   // Niepoprawny kolor
        for(int i = 1; i < fields.size(); i++) {
            fields.get(i).setPiece(new Piece(color));               // Wszystkie inne w dobrym kolorze
        }

        assertFalse(victoryManager.checkVictory(color), "Should return false when a field is occupied by a different color piece.");
    }

    @Test
    void testWhichPlaceIncrement() {
        assertEquals(1, victoryManager.whichPlace(), "First call to whichPlace should return 1.");
        assertEquals(2, victoryManager.whichPlace(), "Second call to whichPlace should return 2.");
        assertEquals(3, victoryManager.whichPlace(), "Third call to whichPlace should return 3.");
    }
    @Test
    void testIsEnd() {
        // Symulacja ukończenia gry przez dwóch graczy
        victoryManager.whichPlace(); // 1 miejsce
        victoryManager.whichPlace(); // 2 miejsce

        assertTrue(victoryManager.isEnd(), "Game should end when one player remains.");
    }
}
