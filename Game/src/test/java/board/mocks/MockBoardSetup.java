package board.mocks;

import board.BoardSetup;
import board.Field;

import java.util.List;

public class MockBoardSetup extends BoardSetup {

    private final List<Field> fieldsInsideAStar;

    public MockBoardSetup(List<Field> fieldsInsideAStar) {
        this.fieldsInsideAStar = fieldsInsideAStar;
    }

    @Override
    public Field getSpecificField(int row, int col) {
        for (Field field : fieldsInsideAStar) {
            if (field.getRow() == row && field.getCol() == col) {
                return field;
            }
        }
        throw new IllegalArgumentException("Field not found");
    }

    @Override
    public List<Field> getFieldsInsideAStar() {
        return fieldsInsideAStar;
    }
    // W mocku do testowania z góry zakładam, że wszystkie pola już należą do planszy
    @Override
    public void boardGenerator() {
        // Puste, ponieważ planszę tworzy my gdzie indziej
    }
}
