package board;

import board.enums.PieceColor;

public class Piece {
    private PieceColor color; // Kolor pionka

    public Piece(PieceColor color) {
        this.color = color;
    }

    public PieceColor getColor() {          // Od koloru piona zależy czyj on jest
        return color;
    }
}
