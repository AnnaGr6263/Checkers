package board;

import server.ChooseBoard;

import java.util.List;

public class NeighboursAssigner {

    private final BoardSetup board;
    private final List<Field> fieldsInsideAStar;

    public NeighboursAssigner() {
        this.board = ChooseBoard.getInstance().getBoard();          // Aktualnie używana plansza
        this.fieldsInsideAStar = board.getFieldsInsideAStar();      // Lista pól do grania dla aktualnie używanej planszy
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
