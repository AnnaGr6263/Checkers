package server.manager;

import data.entities.Game;
import data.entities.Move;
import data.repositories.GameRepository;
import data.repositories.MoveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class GameDataService {

    private final GameRepository gameRepository;
    private final MoveRepository moveRepository;

    @Autowired
    public GameDataService(final GameRepository gameRepository, final MoveRepository moveRepository) {
        this.gameRepository = gameRepository;
        this.moveRepository = moveRepository;
    }


    // Baza danych obsługa

    /**
     * Metoda odpowiedzialna za zarejstrowanie nowej gry przez bazę danych.
     *
     * @param numberOfPlayers Lista graczy
     * @return Aktualna gra
     */
    public Game whenGameStarted(int numberOfPlayers, boolean isYingAndYangEnabled) {
        Game currentGame = new Game();
        currentGame.setGameStarted(true);
        currentGame.setGameEnded(false);
        currentGame.setNumberOfPlayers(numberOfPlayers);
        currentGame.setYingAndYangEnabled(isYingAndYangEnabled);

        // skorzystanie z interfejsu, który rozszerza JpaRepository, dzięki temu mamy dostęp do takich metod jak np. save
        return gameRepository.save(currentGame);
    }

    /**
     * Metoda osdpowiedzialna za rejestrowanie pojedynczego ruchu do bazy danych.
     *
     * @param startX Wiersz/współrzędna X ruchu początkowego
     * @param startY Kolumna/współrzędna Y ruchu początkowego
     * @param endX Wiersz/współrzędna X ruchu końcowego
     * @param endY Kolumna/współrzędna Y ruchu końcowego
     */
    public void recordMove(Game game, int startX, int startY, int endX, int endY) {

        Move move = new Move();
        move.setStartPositionX(startX);
        move.setStartPositionY(startY);
        move.setEndPositionX(endX);
        move.setEndPositionY(endY);
        move.setGame(game);

        moveRepository.save(move);
    }

    /**
     * Metoda wywoływana gdy gra się skończyła. Rejestruje w bazie danych koniec gry.
     */
    public void whenGameEnded(Game currentGame) {
        if (currentGame != null && !currentGame.isGameEnded()) {
            currentGame.setGameEnded(true);
            gameRepository.save(currentGame); // Save the game state as ended
        }
    }
}
