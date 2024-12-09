package board;

public abstract class BoardSetup {

    abstract void boardGenerator();

    abstract Field getSpecificField(int row, int column);
}

