package board;

import java.util.List;

public class Board {


    private List<Field> fields;         //fields of a star
    private BoardSetup boardSetup;

    public Board(BoardSetup boardSetup) {
        this.boardSetup = boardSetup;
    }

    public void genrateCenter(BoardSetup boardSetup) {
        int k = 0;

        //bottom part of a center
        for (int row = 5; row <= 13; row++) {
            for (int col = 9; col <= 13 - k; col++)
                fields.add(new Field(row, col));
            if (row >= 9) k++;
        }
        k = 0;
        //upper part of a center
        for (int row = 13; row >= 6; row--) {
            for (int col = 8; col >= 5 + k; col--)
                fields.add(new Field(row, col));
            if (row <= 9) k++;
        }
    }

}
