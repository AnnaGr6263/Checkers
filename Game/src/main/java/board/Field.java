package board;

import board.enums.HomeColor;

import java.util.ArrayList;
import java.util.List;

public class Field {

    private boolean inStar = false;         // Czy pole zawiera się w gwieżdzie? (Pole gry)
    private int col;                        // Kolumna
    private int row;                        // Wiersz
    private HomeColor home;                 // Czy pole jest domkiem, jak tak to jakim
    private Piece piece;                    // Pion, który znajduje się na danym polu
    private List<Field> neighbours = new ArrayList<>();

    public Field(int row, int col) {
        this.col = col;
        this.row = row;
        this.inStar = false;
        this.home = HomeColor.NONE;         // Domyślnie pole nie należy do żadnego domku
        this.piece = null;                  // Domyślnie pole nie jest zajomwane przez żaden pion
    }

    public boolean isInStar() {
        return inStar;
    }
    public void setInStar(){
        inStar = true;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
    public HomeColor getHome() {
        return home;
    }

    public void setHome(HomeColor home) {
        this.home = home;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }
    public void removePiece() {
        this.piece = null;
    }

    public boolean hasPiece() {
        return this.piece != null;
    }

    public List<Field> getNeighbours() {
        return neighbours;
    }

    public void addNeighbour(Field neighbour) {
        if(!neighbours.contains(neighbour)) {
            neighbours.add(neighbour);
        }
    }
}
