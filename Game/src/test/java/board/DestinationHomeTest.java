package board;

import board.enums.HomeColor;
import board.mocks.MockBoardSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import board.enums.PieceColor;
import server.ChooseBoard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static javafx.beans.binding.Bindings.when;
import static org.junit.jupiter.api.Assertions.*;


public class DestinationHomeTest {

    private DestinationHome destinationHome;

    @BeforeEach
    public void setUp() {
        destinationHome = new DestinationHome();

        List<Field> playingFields = new ArrayList<>();

        int i = 0;
        for(HomeColor homeColor : HomeColor.values()){
            Field field = new Field(i, i);
            field.setHome(homeColor);
            playingFields.add(field);
            i++;
        }
        ChooseBoard.getInstance().setTestBoard(new MockBoardSetup(playingFields)); // Symulujemy planszę

    }

    @Test
    public void testAttachDestinationHomes() {

        destinationHome.attachDestinationHomes();       // Przypisujemy pola docelowe

        // Pobieramy mapę pól docelowych
        Map<PieceColor, List<Field>> destinationHomesMap = destinationHome.getDestinationHomesMap();

        // Sprawdzamy, czy mapa nie jest pusta
        assertNotNull(destinationHomesMap, "Destination homes map should not be null");
        assertEquals(6, destinationHomesMap.size(), "There should be 6 entries in the destination homes map");

        // Weryfikujemy zawartość dla każdego PieceColor
        for (PieceColor pieceColor : PieceColor.values()) {
            List<Field> fields = destinationHomesMap.get(pieceColor);
            assertNotNull(fields, "List of fields for " + pieceColor + " should not be null");
            assertFalse(fields.isEmpty(), "List of fields for " + pieceColor + " should not be empty");


            HomeColor expectedHomeColor;

            if(pieceColor == PieceColor.RED_PIECE) expectedHomeColor = HomeColor.BLUE;
            else if (pieceColor == PieceColor.BLACK_PIECE) expectedHomeColor = HomeColor.PURPLE;
            else if (pieceColor == PieceColor.BLUE_PIECE) expectedHomeColor = HomeColor.RED;
            else if (pieceColor == PieceColor.GREEN_PIECE) expectedHomeColor = HomeColor.YELLOW;
            else if (pieceColor == PieceColor.YELLOW_PIECE) expectedHomeColor = HomeColor.GREEN;
            else if (pieceColor == PieceColor.PURPLE_PIECE) expectedHomeColor = HomeColor.BLACK;
            else expectedHomeColor = null;

            for (Field field : fields) {
                assertEquals(expectedHomeColor, field.getHome(),
                        "Field should belong to the opposite home color for " + pieceColor);
            }
        }
    }
    @Test
    public void testMapIsEmptyBeforeAttach() {
        Map<PieceColor, List<Field>> destinationMap = destinationHome.getDestinationHomesMap();
        assertTrue(destinationMap.isEmpty(), "Map should be empty before attaching destination homes");
    }


}
