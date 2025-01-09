package board;
import board.enums.HomeColor;
import board.enums.PieceColor;
import server.ChooseBoard;
import server.Mediator;

import java.util.List;

public class FillWIthPieces {

    private int numberOfPlayers;         // Liczba graczy, czyli klientów, dla których rozpoczęła się gra
    private List<Field> playingFields = ChooseBoard.getInstance().getBoard().getFieldsInsideAStar();        // Pola używane do gry


    public FillWIthPieces(int players) {
        this.numberOfPlayers = players;
        fill();
    }

    public void fill() {

        switch (numberOfPlayers) {
            case 2:
                for(Field field : playingFields) {
                    // Wypełnianie domków - jak mamy dwóch graczy to gramy tylko niebieskimi i czerwonymi pionami
                    if(field.getHome() == HomeColor.RED ) {

                        Piece redPiece = new Piece(PieceColor.RED_PIECE);       // Utworzenie czerwonego pionka
                        field.setPiece(redPiece);

                        // Jesteśmy w grze dla 2 osób, więc musimy grać 15 pionami, nie 10 tak jak jest to domyślnie
                        // Wobec tego pobieramy sobie sąsiadów dla aktualnego ustwienia domku i Ci sąsiedzi
                        // (dla 4 granicznych pól jest to 5 pól) też zostają domkiem
                        for(Field neighbour : field.getNeighbours()) {
                            if(neighbour.getHome() == HomeColor.NONE) {
                                neighbour.setHome(HomeColor.RED);
                                neighbour.setPiece(redPiece);
                            }
                        }

                    } else if (field.getHome() == HomeColor.BLUE) {
                        Piece bluePiece = new Piece(PieceColor.BLUE_PIECE);
                        field.setPiece(bluePiece);

                        for(Field neighbour : field.getNeighbours()) {
                            if(neighbour.getHome() == HomeColor.NONE) {
                                neighbour.setHome(HomeColor.BLUE);
                                neighbour.setPiece(bluePiece);
                            }
                        }
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
                    } else if (field.getHome() == HomeColor.PURPLE) {
                        Piece whitePiece = new Piece(PieceColor.PURPLE_PIECE);
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
                    else if (field.getHome() == HomeColor.PURPLE) color = PieceColor.PURPLE_PIECE;

                    if (color != null) {
                        Piece piece = new Piece(color);
                        field.setPiece(piece);
                    }
                }
                break;
        }
    }
}
