package board;

import server.ChooseBoard;

import java.util.List;

/**
 * Klasa przypisująca każdemu polu jego sąsiadów.
 */
public class NeighboursAssigner {

    private final BoardSetup board;
    private final List<Field> fieldsInsideAStar;

    /**
     * Konstruktor.
     *
     * @param board Plansza o określonym układzie, dla której będziemy przeprowadzać przypisywanie sąsaidów.
     */
    public NeighboursAssigner(BoardSetup board) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null");
        }
        this.board = board; // Przekazana plansza
        this.fieldsInsideAStar = board.getFieldsInsideAStar(); // Lista pól gwiazdy
    }

    /**
     * Metoda przypisująca sąsiadów.
     */
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

    /**
     * Metoda pomocnicza do przypisywania sąsiadów
     * @param field Pole, któremu przydzielamy sąsiadów.
     * @param row Wiersz.
     * @param col Kolumna.
     */
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
