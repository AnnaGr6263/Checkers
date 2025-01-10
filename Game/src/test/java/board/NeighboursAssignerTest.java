package board;

import board.mocks.MockBoardSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NeighboursAssignerTest {

    private List<Field> fields;
    private MockBoardSetup mockBoard;
    private NeighboursAssigner neighboursAssigner;

    @BeforeEach
    void setUp() {
        fields = new ArrayList<>();

        for(int row = 0; row < 5; row++) {          // Na potrzeby testu tworzymy prostszą planszę 5x5
            for(int col = 0; col < 5; col ++) {
                Field field = new Field(row, col);
                fields.add(field);
            }
        }

        for (Field field : fields) {
            field.setInStar();          // Wszystkie utworzone pola traktujemy jako pola przezaczone do gry
        }

        mockBoard = new MockBoardSetup(fields);
        neighboursAssigner = new NeighboursAssigner(mockBoard);
    }

    @Test
    void addCorrectNeighbours() {

        neighboursAssigner.assignNeighbours();      // Każdemu polu przypisujemy sąsiadów

        Field testedField = mockBoard.getSpecificField(2, 2);       // Sprawdzamy poprawnoć dla pola (2,2)
        assertNotNull(testedField);

        List<Field> neighbours = testedField.getNeighbours();
        assertEquals(6, neighbours.size());

        assertTrue(neighbours.contains(getField(1, 3)), "Brakuje sąsiada (1,3)");
        assertTrue(neighbours.contains(getField(3, 3)), "Brakuje sąsiada (3,3)");
        assertTrue(neighbours.contains(getField(3, 1)), "Brakuje sąsiada (3,1)");
        assertTrue(neighbours.contains(getField(1, 1)), "Brakuje sąsiada (1,1)");
        assertTrue(neighbours.contains(getField(2, 4)), "Brakuje sąsiada (2,4)");
        assertTrue(neighbours.contains(getField(2, 0)), "Brakuje sąsiada (2,0)");

    }

    @Test
    void ifIgnoresOutOfBoundsFields() {
        neighboursAssigner.assignNeighbours();

        Field testedField = mockBoard.getSpecificField(0,0);
        assertNotNull(testedField);

        List<Field> neighbours = testedField.getNeighbours();
        assertEquals(2, neighbours.size());

        assertTrue(neighbours.contains(getField(0,2)), "Brakuje sąsiada (0,2)");
        assertTrue(neighbours.contains(getField(1,1)), "Brakuje sąsiada (1,1)");
    }

    // Metoda pomocnicza
    Field getField(int row, int col){
        return mockBoard.getSpecificField(row, col);
    }
}
