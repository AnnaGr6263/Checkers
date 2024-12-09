package board;

public abstract class BoardSetup {

    abstract void boardGenerator();

    //returns field with specified coordinates
    public abstract Field getSpecificField(int row, int column);
}

