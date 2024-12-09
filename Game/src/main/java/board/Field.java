package board;

public class Field {

    private boolean inStar = false;         // Is a filed in a Star or not
    private int col;                        // Column
    private int row;                        // Row

    public Field(int row, int col) {
        this.col = col;
        this.row = row;
        this.inStar = false;
    }

    public boolean isInStar() {
        return inStar;
    }
    public void setInStar(){
        inStar = (!inStar);
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
}
