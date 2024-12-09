package board;

public abstract class BoardSetup {

    public abstract void boardGenerator();

    public abstract Field getSpecificField(int row, int column);
}

