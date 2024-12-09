package board;

public abstract class BoardSetup {

    public abstract void boardGenerator();

    abstract Field getSpecificField(int row, int column);
}

