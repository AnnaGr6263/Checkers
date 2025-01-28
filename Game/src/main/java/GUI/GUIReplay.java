package GUI;

import board.BoardSetup;
import board.Field;
import board.Piece;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class GUIReplay extends Application {
    private static BoardSetup boardToInitialize; // Plansza przekazywana do GUIReplay
    private Pane root;                           // Główny kontener GUI
    private static boolean isJavaFXRunning = false; // Flaga informująca, czy JavaFX działa

    private static Map<Field, Circle> pieceMap = new HashMap<>(); // Mapowanie pól na pionki w GUI

    /**
     * Ustawienie planszy do odtwarzania.
     *
     * @param board Wybrana plansza.
     */
    public static void setBoard(BoardSetup board) {
        boardToInitialize = board; // Ustaw planszę do odtwarzania
    }

    /**
     * Uruchamia GUI do odtwarzania rozgrywki.
     */
    public static void launchForReplay() {
        synchronized (GUIReplay.class) {
            if (!isJavaFXRunning) {
                new Thread(() -> Application.launch(GUIReplay.class)).start();

                try {
                    while (!isJavaFXRunning) {
                        GUIReplay.class.wait(); // Czekamy, aż JavaFX się uruchomi
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Start JavaFX, wymagane przez framework.
     *
     * @param primaryStage Główne okno aplikacji.
     */
    @Override
    public void start(Stage primaryStage) {
        synchronized (GUIReplay.class) {
            isJavaFXRunning = true;
            GUIReplay.class.notifyAll();
        }

        root = new Pane();
        drawGrid(); // Rysowanie siatki i pól
        drawFields(); // Rysowanie pól planszy
        drawPieces(); // Rysowanie pionków

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Replay - Chinese Checkers");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Rysowanie pól planszy.
     */
    private void drawFields() {
        for (Field field : boardToInitialize.getFieldsInsideAStar()) {
            int row = field.getRow();
            int col = field.getCol();

            double x = OFFSET_X + (col - 12) * (CELL_SIZE / 2.0);
            double y = OFFSET_Y + (row - 8) * (CELL_SIZE * 0.866);

            Circle circle = new Circle(x, y, CELL_SIZE / 2.5);
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(1.5);
            circle.setFill(field.getHome() != null ? getColorForHome(field.getHome()) : Color.TRANSPARENT);
            root.getChildren().add(circle);
        }
    }

    /**
     * Rysowanie pionków.
     */
    private void drawPieces() {
        for (Field field : boardToInitialize.getFieldsInsideAStar()) {
            Piece piece = field.getPiece();
            if (piece != null) {
                int row = field.getRow();
                int col = field.getCol();

                double x = OFFSET_X + (col - 12) * (CELL_SIZE / 2.0);
                double y = OFFSET_Y + (row - 8) * (CELL_SIZE * 0.866);

                Circle pieceCircle = new Circle(x, y, CELL_SIZE / 2.5);
                pieceCircle.setFill(getColorForPiece(piece));
                root.getChildren().add(pieceCircle);
                pieceMap.put(field, pieceCircle); // Dodanie pionka do mapy
            }
        }
    }

    /**
     * Animacja ruchu pionka na planszy.
     *
     * @param startX Wiersz początkowy.
     * @param startY Kolumna początkowa.
     * @param endX   Wiersz końcowy.
     * @param endY   Kolumna końcowa.
     */
    public static void animateMove(int startX, int startY, int endX, int endY) {
        Platform.runLater(() -> {
            Field startField = boardToInitialize.getSpecificField(startX, startY);
            Field endField = boardToInitialize.getSpecificField(endX, endY);

            if (startField != null && endField != null) {
                Circle pieceCircle = pieceMap.remove(startField); // Pobierz pionek z pola początkowego
                if (pieceCircle != null) {
                    double newX = OFFSET_X + (endField.getCol() - 12) * (CELL_SIZE / 2.0);
                    double newY = OFFSET_Y + (endField.getRow() - 8) * (CELL_SIZE * 0.866);

                    pieceCircle.setCenterX(newX);
                    pieceCircle.setCenterY(newY);
                    pieceMap.put(endField, pieceCircle); // Aktualizacja mapy pól
                }
            }
        });
    }

    /**
     * Odświeżanie widoku.
     */
    private void refresh() {
        root.getChildren().clear();
        drawGrid();
        drawFields();
        drawPieces();
    }

    /**
     * Rysowanie siatki planszy.
     */
    private void drawGrid() {
        // Opcjonalnie, można dodać numery wierszy i kolumn, jak w klasie GUI
    }

    /**
     * Pobiera kolor dla domku.
     */
    private Color getColorForHome(Enum<?> home) {
        // Dopasowanie kolorów domków do Enum
        return Color.LIGHTGRAY; // Placeholder
    }

    /**
     * Pobiera kolor dla pionka.
     */
    private Color getColorForPiece(Piece piece) {
        return switch (piece.getColor()) {
            case RED_PIECE -> Color.RED;
            case BLUE_PIECE -> Color.BLUE;
            case GREEN_PIECE -> Color.GREEN;
            case YELLOW_PIECE -> Color.YELLOW;
            case PURPLE_PIECE -> Color.PURPLE;
            case BLACK_PIECE -> Color.BLACK;
            default -> Color.TRANSPARENT;
        };
    }

    private static final int CELL_SIZE = 30;
    private static final int OFFSET_X = 400;
    private static final int OFFSET_Y = 300;
}
