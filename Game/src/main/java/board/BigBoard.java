package board;

import java.util.*;

public class BigBoard extends BoardSetup{

    private int height = 17;          // Liczba wierszy
    private int width = 25;         // Liczba kolumn
    private List<Field> whole_board = new ArrayList<>();
    private List<Field> fieldsInsideAStar = new ArrayList<>();     // Pola, które zawiera nasze pole gry czyli gwiazda

    public List<Field> getFieldsInsideAStar() {
        return fieldsInsideAStar;
    }

    @Override
    public void boardGenerator() {

        // Generuje całą tablicę na razie bez decydowania co należy do gwiazdy a co nie
        for(int r = 0; r < height; r++) {
            for(int c = 0; c < width; c++) {
                Field currentField = new Field(r, c);
                whole_board.add(currentField);
            }
        }
        // Generowanie pól gwiazdy
        centerGenerator();
        boardGenerator();
    }


    public void centerGenerator() {
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

    void cornersGenerator() {

        // Górny róg
        int i = 0;
        for(int r = 3; r >= 0; r-- ) {
            for (int c = 9 + i; c <= 15 - i; c += 2) {
                Field curField = getSpecificField(r, c);
                curField.setInStar();
                fieldsInsideAStar.add(curField);
            }
            i++;
        }
        // Lewy górny róg
        int k=0;
        for(int r = 4; r <= 7; r++) {
            for (int c = k; c <= 6 - k; c += 2) {
                Field currentField = getSpecificField(r, c);
                currentField.setInStar();
                fieldsInsideAStar.add(currentField);
            }
            k++;
        }

        // Lewy dolny róg
        int j = 0;
        for(int r = 12; r >= 9; r-- ) {
            for (int c = j; c <= 6 - j; c += 2) {
                Field curField = getSpecificField(r, c);
                curField.setInStar();
                fieldsInsideAStar.add(curField);
            }
            j++;
        }
        // Prawy górny róg
        int l=0;
        for(int r = 4; r <= 7; r++) {
            for (int c = 18 + l; c <= 24 - l; c += 2) {
                Field currentField = getSpecificField(r, c);
                currentField.setInStar();
                fieldsInsideAStar.add(currentField);
            }
            l++;
        }
        // Prawy dolny róg
        int m = 0;
        for(int r = 12; r >= 9; r-- ) {
            for (int c = 18 + m; c <= 24 - m; c += 2) {
                Field curField = getSpecificField(r, c);
                curField.setInStar();
                fieldsInsideAStar.add(curField);
            }
            m++;
        }
        // Dolny róg
        int n=0;
        for(int r = 4; r <= 7; r++) {
            for (int c = 18 + n; c <= 24 - n; c += 2) {
                Field currentField = getSpecificField(r, c);
                currentField.setInStar();
                fieldsInsideAStar.add(currentField);
            }
            n++;
        }
    }

    // Zwraca pole o konkretnych współrzędnych
    @Override
    public Field getSpecificField(int row, int column) {
        if(row < height && row > 0 && column < width && column > 0) {
            int index = row * width + column;
            return whole_board.get(index);
        }
        return null;    //in other case it returns null so there is no such a field
    }
}
