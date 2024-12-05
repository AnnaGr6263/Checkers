package board;

import java.util.*;

public class BigBoard extends BoardSetup{

    private int height = 17;          //number of rows
    private int width = 25;         //number of columns
    private List<Field> whole_board = new ArrayList<>();
    private List<Field> fieldsInsideAStar = new ArrayList<>();     //fields which our star shape include

    public List<Field> getFieldsInsideAStar() {
        return fieldsInsideAStar;
    }

    @Override
    public void boardGenerator() {

        //generate a whole board, without deciding which fields are in the star
        for(int r = 0; r < height; r++) {
            for(int c = 0; c < width; c++) {
                Field currentField = new Field(r, c);
                whole_board.add(currentField);
            }
        }
        //generating star shape
        centerGenerator();
        boardGenerator();
    }


    public void centerGenerator() {
        //bottom part of a center
        int k=0;
        for(int r = 8; r <= 12; r++) {
            for (int c = 4 + k; c <= 20 - k; c+=2) {
                Field currentField = getSpecificField(r, c);
                currentField.setInStar();
                fieldsInsideAStar.add(currentField);
            }
            k++;
        }
        //upper part of a center
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

        //upper corner
        int i = 0;
        for(int r = 3; r >= 0; r-- ) {
            for (int c = 9 + i; c <= 15 - i; c += 2) {
                Field curField = getSpecificField(r, c);
                curField.setInStar();
                fieldsInsideAStar.add(curField);
            }
            i++;
        }
        //left upper corner
        int k=0;
        for(int r = 4; r <= 7; r++) {
            for (int c = k; c <= 6 - k; c += 2) {
                Field currentField = getSpecificField(r, c);
                currentField.setInStar();
                fieldsInsideAStar.add(currentField);
            }
            k++;
        }

        //left bottom corner
        int j = 0;
        for(int r = 12; r >= 9; r-- ) {
            for (int c = j; c <= 6 - j; c += 2) {
                Field curField = getSpecificField(r, c);
                curField.setInStar();
                fieldsInsideAStar.add(curField);
            }
            j++;
        }
        //right upper corner
        int l=0;
        for(int r = 4; r <= 7; r++) {
            for (int c = 18 + l; c <= 24 - l; c += 2) {
                Field currentField = getSpecificField(r, c);
                currentField.setInStar();
                fieldsInsideAStar.add(currentField);
            }
            l++;
        }
        //right bottom corner
        int m = 0;
        for(int r = 12; r >= 9; r-- ) {
            for (int c = 18 + m; c <= 24 - m; c += 2) {
                Field curField = getSpecificField(r, c);
                curField.setInStar();
                fieldsInsideAStar.add(curField);
            }
            m++;
        }
        //bottom corner
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

    //returns field with specified coordinates
    @Override
    public Field getSpecificField(int row, int column) {
        if(row < height && row > 0 && column < width && column > 0) {
            int index = row * width + column;
            return whole_board.get(index);
        }
        return null;    //in other case it returns null so there is no such a field
    }
}
