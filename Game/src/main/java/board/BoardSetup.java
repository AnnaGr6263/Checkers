package board;

import java.util.List;

public abstract class BoardSetup {

    public abstract void boardGenerator();
    public abstract List<Field> getFieldsInsideAStar();
    public abstract Field getSpecificField(int row, int column);
}

