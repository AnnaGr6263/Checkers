package board;

import server.ChooseBoard;

import java.util.List;

public class NeighboursAssigner {

    private final BoardSetup board;
    private final List<Field> fieldsInsideAStar;

    public NeighboursAssigner(BoardSetup board) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null");
        }
        this.board = board; // Przekazana plansza
        this.fieldsInsideAStar = board.getFieldsInsideAStar(); // Lista pól gwiazdy
    }

    public void assignNeighbours() {
        for (Field field : fieldsInsideAStar) {
            int col = field.getCol();
            int row = field.getRow();

            // Dodajemy sąsiadów w sześciokątnym układzie
            tryAddNeighbour(field, row - 1, col + 1); // Prawy górny
            tryAddNeighbour(field, row + 1, col + 1); // Prawy dolny
            tryAddNeighbour(field, row + 1, col - 1); // Lewy dolny
            tryAddNeighbour(field, row - 1, col - 1); // Lewy górny
            tryAddNeighbour(field, row, col - 2);          // Lewy
            tryAddNeighbour(field, row, col + 2);          // Prawy
        }
    }

    private void tryAddNeighbour(Field field, int row, int col) {
        try {
            Field neighbour = board.getSpecificField(row, col);
            if (neighbour.isInStar()) {
                field.addNeighbour(neighbour);
            }
        } catch (IllegalArgumentException e) {
            // Pole jest poza planszą, więc nie interesuje nas jako sąsiad
        }
    }
}
