package board;


public class Board {

    private BoardSetup boardSetup;


    // Tworzymy odpowiedni setup np BoardSetup bigBoardSetup = new BigBoard();  i w konstruktorze podajemy właśnie ten obiekt
    public Board(BoardSetup boardSetup) {
        this.boardSetup = boardSetup;
    }

    public void genrateTheWholeBoard(BoardSetup boardSetup) {
        boardSetup.boardGenerator();
    }

}
