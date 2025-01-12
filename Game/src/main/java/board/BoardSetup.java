package board;

import java.util.List;

/**
 * Klasa abstarkcyjna. Zawiera metody charaktrystyczne dla planszy. Jednak każsa klasa po niej dziedzicząca, będzie
 * trochę inną planszą dlatego będzie zawierała inną implementację tych metod.
 */
public abstract class BoardSetup {

    /**
     * Opdowiedzialna za wygenerowanie planszy.
     */
    public abstract void boardGenerator();

    /**
     * Pobiera listę pól należących do planszy.
     *
     * @return Listę pół należących do planszy.
     */
    public abstract List<Field> getFieldsInsideAStar();

    /**
     * Znajduje i zwraca konkretne pole.
     *
     * @param row Wiersz.
     * @param column Kolumna.
     * @return Pole należące do planszy o konkretnym wierszu i kolumnie.
     */
    public abstract Field getSpecificField(int row, int column);
}

