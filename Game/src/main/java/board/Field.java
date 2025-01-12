package board;

import board.enums.HomeColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa reprezentująca jedno pole.
 */
public class Field {

    private boolean inStar = false;         // Czy pole zawiera się w gwieżdzie? (Pole gry)
    private int col;                        // Kolumna
    private int row;                        // Wiersz
    private HomeColor home;                 // Czy pole jest domkiem, jak tak to jakim
    private Piece piece;                    // Pion, który znajduje się na danym polu
    private List<Field> neighbours = new ArrayList<>();

    /**
     * Konstruktor. Zawiera wiersz i kolumnę, dwie najbarzdiej podstwowe cechy pola służące do jego jednoznacznego zidentyfikowania.
     *
     * @param row Wiersz, w którym znajduje się pole.
     * @param col Kolumna, w której znajduje się pole.
     */
    public Field(int row, int col) {
        this.col = col;
        this.row = row;
        this.inStar = false;
        this.home = HomeColor.NONE;         // Domyślnie pole nie należy do żadnego domku
        this.piece = null;                  // Domyślnie pole nie jest zajomwane przez żaden pion
    }

    /**
     * Metoda.
     *
     * @return Zwraca czy pole zawiera się w "gwieżdzie" tzn. w obrębie naszej planszy.
     */
    public boolean isInStar() {
        return inStar;
    }

    /**
     * Metoda zmienia wartość zmiennej inStart na true. To znaczy, że dane pole od tego momentu jest wewnątrz planszy.
     */
    public void setInStar(){
        inStar = true;
    }

    /**
     * Getter.
     *
     * @return Kolumnę danego pola.
     */
    public int getCol() {
        return col;
    }

    /**
     * Getter.
     *
     * @return wiersz danego pola.
     */
    public int getRow() {
        return row;
    }

    /**
     * Getter.
     *
     * @return Zwraca kolor domku dla danego pola. Jeśli pole nie jest domkiem dostaniemy null.
     */
    public HomeColor getHome() {
        return home;
    }

    /**
     * Setter. Ustawia przekazany do metody kolor domku jako kolor domku danego pola.
     *
     * @param home Kolor domku.
     */
    public void setHome(HomeColor home) {
        this.home = home;
    }

    /**
     * Getter.
     *
     * @return Zwraca pionek stojący aktualnie na polu.
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Ustawia pionek na polu.
     *
     * @param piece Konkretny pionek, który chcemy ustawić.
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    /**
     * Usuwa pionek z pola.
     */
    public void removePiece() {
        this.piece = null;
    }

    /**
     * Metoda udziela odpowiedzi na pytanie: Czy na polu stoi jakikolwiek pionek?
     *
     * @return Prawdę jeśli pole jest zajęte i fałsz gdy jest puste.
     */
    public boolean hasPiece() {
        return this.piece != null;
    }

    /**
     * Getter.
     *
     * @return Listę zawierajacą sąsiadów danego pola.
     */
    public List<Field> getNeighbours() {
        return neighbours;
    }

    /**
     * Metoda dodaje obiekt innego pola jako sąsiad pola.
     *
     * @param neighbour Sąsiad.
     */
    public void addNeighbour(Field neighbour) {
        if(!neighbours.contains(neighbour)) {
            neighbours.add(neighbour);
        }
    }
}
