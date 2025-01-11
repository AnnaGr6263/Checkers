package board;

import board.enums.PieceColor;

/**
 * Klasa reprezentująca pionek.
 */
public class Piece {
    private PieceColor color;

    /**
     * Konstruktor.
     *
     * @param color Kolor pionka - jego podstawowa cecha, od której później zależy czyj on jest.
     */
    public Piece(PieceColor color) {
        this.color = color;
    }

    /**
     * Getter koloru pionka.
     *
     * @return Zwraca kolor pionka.
     */
    public PieceColor getColor() {
        return color;
    }
}
