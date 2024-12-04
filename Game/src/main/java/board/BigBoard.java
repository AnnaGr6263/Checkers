package board;

import java.util.*;

public class BigBoard implements IBoardSetup{

    private int rows = 17;
    private int columns = 25;
    private List<Field> whole_board = new ArrayList<>();
    private List<Field> fieldsInsideAStar = new ArrayList<>();          //fields which our star shape include

    public List<Field> getFieldsInsideAStar() {
        return fieldsInsideAStar;
    }

    @Override
    public void centerGenerator() {
        //bottom part of a center
        int k=0;
        for(int r = 8; r <= 12; r++) {
            for (int c = 4 + k; c <= 20 - k; c+=2) {
                Field currentField = new Field(r, c);
                currentField.setInStar();
                fieldsInsideAStar.add(currentField);
            }
            k++;
        }
        //upper part of a center
        int i = 0;
        for(int r = 7; r >= 4; r-- ) {
            for(int c = 5; c <=19; c+=2) {
                Field curField = new Field(r, c);
                curField.setInStar();
                fieldsInsideAStar.add(curField);
            }
        }


    }
}
