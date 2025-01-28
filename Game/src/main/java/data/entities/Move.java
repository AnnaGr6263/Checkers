package data.entities;
import jakarta.persistence.*;


@Entity
public class Move {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private int startPositionX;
    private int startPositionY;
    private int endPositionX;
    private int endPositionY;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    // Gettery i settery
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public int getEndPositionX() {
        return endPositionX;
    }

    public int getStartPositionX() {
        return startPositionX;
    }

    public void setStartPositionX(int startPositionX) {
        this.startPositionX = startPositionX;
    }

    public int getStartPositionY() {
        return startPositionY;
    }

    public int getEndPositionY() {
        return endPositionY;
    }

    public void setEndPositionX(int endPositionX) {
        this.endPositionX = endPositionX;
    }

    public void setStartPositionY(int startPositionY) {
        this.startPositionY = startPositionY;
    }

    public void setEndPositionY(int endPositionY) {
        this.endPositionY = endPositionY;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
