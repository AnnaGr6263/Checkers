package board;
import board.enums.HomeColor;
import server.ChooseBoard;
import server.Mediator;

import java.util.List;

public class FillWIthPieces {

    private List<Mediator> players;         // Gracze, czyli klienci, dla których rozpoczęła się gra
    private List<Field> playingFields = ChooseBoard.getInstance().getBoard().getFieldsInsideAStar();        // Pola używane do gry


    public FillWIthPieces(List<Mediator> players) {
        this.players = players;
    }

    public void fill() {
        int numberOfPlayers = players.size();

        switch (numberOfPlayers) {
            case 2:
                for(Field field : playingFields) {
                    // jak mamy dwóch graczy to gramy tylko niebieskimi i czerwonymi pionami
                    if(field.getHome() == HomeColor.RED || field.getHome() == HomeColor.BLUE) {
                        // Wypełnianie
                    } else {
                        field.setHome(HomeColor.NONE);     // Ustawienie z powrotem pola jako normalne pole do gry
                    }
                }
                break;
            case 3:
                for(Field field : playingFields) {
                    // jak mamy trzech graczy to gramy tylko czerwonymi, żółtymi i czarnymi pionami
                    if(field.getHome() == HomeColor.RED || field.getHome() == HomeColor.YELLOW || field.getHome() == HomeColor.BLACK) {
                        // Wypełnianie
                    } else {
                        field.setHome(HomeColor.NONE);     // Ustawienie z powrotem pola jako normalne pole do gry
                    }
                }
                break;
            case 4:





        }
    }

}
