package board;

import board.enums.HomeColor;

import java.util.*;

/**
 * Klasa reprezentująca "dużą" planszę tzn. taką, której wewnętrzna część (sześciokąt) ma bok długości 5-ciu pól,
 * a w każdym domku mieści się 10 pionów (W ogólności, ponieważ dla dwóch graczy później jest mała zmiana i wrzucamy
 * po 15 pionków do domku).
 */
public class BigBoard extends BoardSetup{

    private int height = 17;          // Liczba wierszy
    private int width = 25;         // Liczba kolumn
    private List<Field> whole_board = new ArrayList<>();
    private List<Field> fieldsInsideAStar = new ArrayList<>();     // Pola, które zawiera nasze pole gry czyli gwiazda

    /**
     * Getter.
     *
     * @return Listę pól, które należą do planszy (na których toczy się gra).
     */
    @Override
    public List<Field> getFieldsInsideAStar() {
        return fieldsInsideAStar;
    }

    /**
     * Ustawia cały cały układ planszy.
     */
    @Override
    public void boardGenerator() {

        // Generuje całą tablicę na razie bez decydowania co należy do gwiazdy a co nie
        for(int r = 0; r < height; r++) {
            for(int c = 0; c < width; c++) {
                Field currentField = new Field(r, c);       // Tworzy pola, na razie wszystkie
                whole_board.add(currentField);
            }
        }
        // Generowanie pól gwiazdy
        centerGenerator();
        cornersGenerator();
        assignNeighbours();
    }


    /**
     * Metoda pomocnicza, generuje środek planszy.
     */
    private void centerGenerator() {
        // Dolna część środka
        int k=0;
        for(int r = 8; r <= 12; r++) {
            for (int c = 4 + k; c <= 20 - k; c+=2) {
                Field currentField = getSpecificField(r, c);
                currentField.setInStar();
                fieldsInsideAStar.add(currentField);
            }
            k++;
        }
        // Górna część środka
        int i = 0;
        for(int r = 7; r >= 4; r-- ) {
            for(int c = 5+i; c <=19-i; c+=2) {
                Field curField = getSpecificField(r, c);
                curField.setInStar();
                fieldsInsideAStar.add(curField);
            }
            i++;
        }
    }

    /**
     * Metoda pomocnicza, generuje rogi planszy (ramiona gwiazdy).
     */
    private void cornersGenerator() {

        // Górny róg - CZERWONY
        int i = 0;
        for(int r = 3; r >= 0; r-- ) {
            for (int c = 9 + i; c <= 15 - i; c += 2) {
                Field curField = getSpecificField(r, c);
                curField.setInStar();                   // Dodanie pola do gwiazdy
                curField.setHome(HomeColor.RED);        // Dodanie pola jako czerwony domek
                fieldsInsideAStar.add(curField);
            }
            i++;
        }
        // Lewy górny róg - FIOLETOWY
        int k=0;
        for(int r = 4; r <= 7; r++) {
            for (int c = k; c <= 6 - k; c += 2) {
                Field currentField = getSpecificField(r, c);
                currentField.setInStar();
                currentField.setHome(HomeColor.PURPLE);
                fieldsInsideAStar.add(currentField);
            }
            k++;
        }

        // Lewy dolny róg - ŻÓŁTY
        int j = 0;
        for(int r = 12; r >= 9; r-- ) {
            for (int c = j; c <= 6 - j; c += 2) {
                Field curField = getSpecificField(r, c);
                curField.setInStar();
                curField.setHome(HomeColor.YELLOW);
                fieldsInsideAStar.add(curField);
            }
            j++;
        }
        // Prawy górny róg - ZIELONY
        int l=0;
        for(int r = 4; r <= 7; r++) {
            for (int c = 18 + l; c <= 24 - l; c += 2) {
                Field currentField = getSpecificField(r, c);
                currentField.setInStar();
                currentField.setHome(HomeColor.GREEN);
                fieldsInsideAStar.add(currentField);
            }
            l++;
        }
        // Prawy dolny róg - CZARNY
        int m = 0;
        for(int r = 12; r >= 9; r-- ) {
            for (int c = 18 + m; c <= 24 - m; c += 2) {
                Field curField = getSpecificField(r, c);
                curField.setInStar();
                curField.setHome(HomeColor.BLACK);
                fieldsInsideAStar.add(curField);
            }
            m++;
        }
        // Dolny róg - BLUE
        int n=0;
        for(int r = 13; r <= 16; r++) {
            for (int c = 9 + n; c <= 15 - n; c += 2) {
                Field currentField = getSpecificField(r, c);
                currentField.setInStar();
                currentField.setHome(HomeColor.BLUE);
                fieldsInsideAStar.add(currentField);
            }
            n++;
        }
    }

    /**
     * Przypisuje każdemu utworzonemu polu, które jest wewnątrzz planszy, jego sąsiadów
     */
    private void assignNeighbours() {
        NeighboursAssigner neighboursAssigner = new NeighboursAssigner(this);
        neighboursAssigner.assignNeighbours();
    }

    /**
     * Zwraca pole o konkretnych współrzędnych.
     *
     * @param row Wiersz.
     * @param column Kolumna.
     * @return Pole o przekazanych współrzędnych.
     */
    @Override
    public Field getSpecificField(int row, int column) {
        if(row >= 0 && row < height && column >= 0 && column < width) {
            int index = row * width + column;
            return whole_board.get(index);
        }
        throw new IllegalArgumentException("Coordinates out of bounds: row = " + row + ", column = " + column);
    }
}
