package board;

public class Field {

    private boolean inStar = false;        //is a field filled with a pawn or no
    private int col;                        //column
    private int row;                        //row

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
