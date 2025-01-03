package board;
import board.enums.HomeColor;
import board.enums.PieceColor;
import server.ChooseBoard;
import server.Mediator;

import java.util.HashMap;
import java.util.List;

public class FillWIthPieces {

    private List<Mediator> players;         // Gracze, czyli klienci, dla których rozpoczęła się gra
    private List<Field> playingFields = ChooseBoard.getInstance().getBoard().getFieldsInsideAStar();        // Pola używane do gry

    //private HashMap<Piece, >

    public FillWIthPieces(List<Mediator> players) {
        this.players = players;
    }

    public void fill() {
        int numberOfPlayers = players.size();

        switch (numberOfPlayers) {
            case 2:
                for(Field field : playingFields) {
                    // Wypełnianie domków - jak mamy dwóch graczy to gramy tylko niebieskimi i czerwonymi pionami
                    if(field.getHome() == HomeColor.RED ) {
                        Piece redPiece = new Piece(PieceColor.RED_PIECE);       // Utworzenie czerwonego pionka
                        field.setPiece(redPiece);                               // Dodanie go w polu domka
                    } else if (field.getHome() == HomeColor.BLUE) {
                        Piece bluePiece = new Piece(PieceColor.BLUE_PIECE);
                        field.setPiece(bluePiece);
                    } else {
                        field.setHome(HomeColor.NONE);     // Ustawienie z powrotem pola jako normalne pole do gry
                    }
                }
                break;
            case 3:
                for(Field field : playingFields) {
                    // jak mamy trzech graczy to gramy tylko czerwonymi, żółtymi i czarnymi pionami
                    if(field.getHome() == HomeColor.RED) {
                        Piece redPiece = new Piece(PieceColor.RED_PIECE);
                        field.setPiece(redPiece);
                    } else if (field.getHome() == HomeColor.YELLOW) {
                        Piece yellowPiece = new Piece(PieceColor.YELLOW_PIECE);
                        field.setPiece(yellowPiece);
                    } else if (field.getHome() == HomeColor.BLACK) {
                        Piece blackPiece = new Piece(PieceColor.BLACK_PIECE);
                        field.setPiece(blackPiece);
                    } else {
                        field.setHome(HomeColor.NONE);     // Ustawienie z powrotem pola jako normalne pole do gry
                    }
                }
                break;
            case 4:
                for(Field field : playingFields) {
                    // jak mamy czterech graczy to gramy czerwonymi, białymi, niebieskimi i czarnymi pionami
                    if(field.getHome() == HomeColor.RED) {
                        Piece redPiece = new Piece(PieceColor.RED_PIECE);
                        field.setPiece(redPiece);
                    } else if (field.getHome() == HomeColor.WHITE) {
                        Piece whitePiece = new Piece(PieceColor.WHITE_PIECE);
                        field.setPiece(whitePiece);
                    } else if (field.getHome() == HomeColor.BLACK) {
                        Piece blackPiece = new Piece(PieceColor.BLACK_PIECE);
                        field.setPiece(blackPiece);
                    } else if (field.getHome() == HomeColor.BLUE) {
                        Piece bluePiece = new Piece(PieceColor.BLUE_PIECE);
                        field.setPiece(bluePiece);
                    } else {
                        field.setHome(HomeColor.NONE);     // Ustawienie z powrotem pola jako normalne pole do gry
                    }
                }
                break;
            case 6:
                PieceColor color = null;

                for(Field field : playingFields) {
                    if (field.getHome() == HomeColor.RED) color = PieceColor.RED_PIECE;
                    else if (field.getHome() == HomeColor.GREEN) color = PieceColor.GREEN_PIECE;
                    else if (field.getHome() == HomeColor.BLACK) color = PieceColor.BLACK_PIECE;
                    else if (field.getHome() == HomeColor.BLUE) color = PieceColor.BLUE_PIECE;
                    else if (field.getHome() == HomeColor.YELLOW) color = PieceColor.YELLOW_PIECE;
                    else if (field.getHome() == HomeColor.WHITE) color = PieceColor.WHITE_PIECE;

                    if (color != null) {
                        Piece piece = new Piece(color);
                        field.setPiece(piece);
                    }
                }
                break;
        }
    }

}
