package GUI;

import board.BigBoard;
import board.Field;
import board.Piece;
import board.enums.HomeColor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GUI extends Application {

    private static GUI guiInstance; // Singleton instance
    private Pane root; // Main pane
    private BigBoard bigBoard; // Board instance

    public static GUI getInstance() {
        return guiInstance;
    }

    @Override
    public void start(Stage primaryStage) {
        guiInstance = this; // Set the singleton instance
        bigBoard = new BigBoard(); // Initialize the board
        bigBoard.boardGenerator(); // Generate the board

        root = new Pane(); // Main GUI container
        drawGrid(); // Draw the grid and labels
        drawFields(); // Draw fields
        drawPieces(); // Draw pieces

        // Scene setup
        Scene scene = new Scene(root, 800, 600, Color.WHITE);
        primaryStage.setTitle("Chinese Checkers Board");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void refresh() {
        root.getChildren().clear(); // Wyczyść elementy GUI
        drawGrid();
        drawFields();
        drawPieces(); // Narysuj pionki zgodnie z liczbą graczy
    }

    private void drawGrid() {
        // Column numbers
        for (int col = 0; col < 25; col++) {
            double x = OFFSET_X + (col - 12) * (CELL_SIZE / 2.0);
            double y = OFFSET_Y - (9 * CELL_SIZE * 0.866) - 10;
            Text colNumber = new Text(String.valueOf(col));
            colNumber.setX(x - 5);
            colNumber.setY(y);
            colNumber.setFont(Font.font(12));
            root.getChildren().add(colNumber);
        }

        // Row numbers
        for (int row = 0; row < 17; row++) {
            double x = OFFSET_X - (13 * CELL_SIZE / 2.0) - 20;
            double y = OFFSET_Y + (row - 8) * (CELL_SIZE * 0.866);
            Text rowNumber = new Text(String.valueOf(row));
            rowNumber.setX(x);
            rowNumber.setY(y + 5);
            rowNumber.setFont(Font.font(12));
            root.getChildren().add(rowNumber);
        }

        // Grid lines
        for (int row = 0; row < 17; row++) {
            for (int col = 0; col < 25; col++) {
                double x = OFFSET_X + (col - 12) * (CELL_SIZE / 2.0);
                double y = OFFSET_Y + (row - 8) * (CELL_SIZE * 0.866);

                Line horizontalLine = new Line(x - CELL_SIZE / 2.0, y, x + CELL_SIZE / 2.0, y);
                Line verticalLine = new Line(x, y - CELL_SIZE / 2.0, x, y + CELL_SIZE / 2.0);

                horizontalLine.setStroke(Color.LIGHTGRAY);
                verticalLine.setStroke(Color.LIGHTGRAY);

                root.getChildren().add(horizontalLine);
                root.getChildren().add(verticalLine);
            }
        }
    }

    private void drawFields() {
        for (Field field : bigBoard.getFieldsInsideAStar()) {
            int row = field.getRow();
            int col = field.getCol();

            double x = OFFSET_X + (col - 12) * (CELL_SIZE / 2.0);
            double y = OFFSET_Y + (row - 8) * (CELL_SIZE * 0.866);

            Circle circle = new Circle(x, y, CELL_SIZE / 2.5);
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(1.5);

            if (field.getHome() != HomeColor.NONE) {
                circle.setFill(getColorForHome(field.getHome()));
            } else {
                circle.setFill(Color.TRANSPARENT); // Zwykłe pola nie mają koloru
            }

            root.getChildren().add(circle);
        }
    }


    private void drawPieces() {
        for (Field field : bigBoard.getFieldsInsideAStar()) {
            Piece piece = field.getPiece();
            int row = field.getRow();
            int col = field.getCol();

            double x = OFFSET_X + (col - 12) * (CELL_SIZE / 2.0);
            double y = OFFSET_Y + (row - 8) * (CELL_SIZE * 0.866);

            // Pole z pionkiem
            if (piece != null) {
                Circle pieceCircle = new Circle(x, y, CELL_SIZE / 3.0);
                pieceCircle.setFill(getColorForPiece(piece));
                root.getChildren().add(pieceCircle);
            }
            // Pole domku bez pionka (przezroczyste)
            else if (field.getHome() != HomeColor.NONE) {
                Circle transparentCircle = new Circle(x, y, CELL_SIZE / 2.5);
                transparentCircle.setStroke(Color.BLACK);
                transparentCircle.setFill(Color.TRANSPARENT);
                root.getChildren().add(transparentCircle);
            }
        }
    }


    private Color getColorForHome(HomeColor home) {
        switch (home) {
            case RED:
                return Color.RED;
            case BLUE:
                return Color.BLUE;
            case GREEN:
                return Color.GREEN;
            case YELLOW:
                return Color.YELLOW;
            case PURPLE:
                return Color.PURPLE;
            case BLACK:
                return Color.BLACK;
            default:
                return Color.TRANSPARENT;
        }
    }

    private Color getColorForPiece(Piece piece) {
        switch (piece.getColor()) {
            case RED_PIECE:
                return Color.RED;
            case BLUE_PIECE:
                return Color.BLUE;
            case GREEN_PIECE:
                return Color.GREEN;
            case YELLOW_PIECE:
                return Color.YELLOW;
            case PURPLE_PIECE:
                return Color.PURPLE;
            case BLACK_PIECE:
                return Color.BLACK;
            default:
                return Color.TRANSPARENT;
        }
    }

    private static final int CELL_SIZE = 30;
    private static final int OFFSET_X = 400;
    private static final int OFFSET_Y = 300;

    public static void main(String[] args) {
        launch(args);
    }
}
