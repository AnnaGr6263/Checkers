package GUI;

import board.BoardSetup;
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
import server.manager.GameManager;
import server.manager.YinAndYangManager;

public class GUI extends Application {

    private static GUI guiInstance; // Singleton - jedyna instancja klasy GUI
    private static BoardSetup boardToInitialize; // Plansza przekazywana do GUI przed uruchomieniem
    private Pane root; // Główny kontener GUI
    private BoardSetup board; // Obiekt planszy
    private static GameManager gameManager = GameManager.getInstance();

    // Ustawienie planszy do wykorzystania przez GUI
    public static void setBoard(BoardSetup board) {
        boardToInitialize = board; // Przechowaj dowolną instancję klasy BoardSetup
    }

    // Pobranie instancji GUI (Singleton)
    public static GUI getInstance() {
        if (guiInstance == null) {
            throw new IllegalStateException("GUI instance has not been initialized yet!");
        }
        return guiInstance;
    }

    private boolean isYinAndYangEnabled() {
        if (gameManager == null) {
            return false; // Jeśli gameManager nie został jeszcze zainicjalizowany, zakładamy brak aktywacji
        }

        YinAndYangManager yinAndYangManager = gameManager.getYinAndYangManager();
        return yinAndYangManager != null && yinAndYangManager.isYinAndYangEnabled();
    }


    @Override
    public void start(Stage primaryStage) {
        guiInstance = this; // Ustawienie instancji Singleton

        if (boardToInitialize == null) {
            throw new IllegalStateException("Board must be set before launching GUI!"); // Sprawdzenie, czy plansza została ustawiona
        }

        this.board = boardToInitialize; // Przypisz przekazaną planszę

        root = new Pane(); // Inicjalizacja głównego kontenera GUI
        drawGrid(); // Rysowanie siatki i etykiet
        drawFields(); // Rysowanie pól planszy
        drawPieces(); // Rysowanie pionków

        // Konfiguracja sceny
        Scene scene = new Scene(root, 800, 600, Color.WHITE);
        primaryStage.setTitle("Chinese Checkers Board"); // Tytuł okna
        primaryStage.setScene(scene);
        primaryStage.show(); // Wyświetlenie okna
    }

    // Odświeżanie GUI
    public void refresh() {
        System.out.println("Refreshing GUI..."); // Informacja debug
        Platform.runLater(() -> { // Wykonaj w wątku JavaFX
            root.getChildren().clear(); // Wyczyść wszystkie elementy GUI
            drawGrid(); // Narysuj siatkę
            drawFields(); // Narysuj pola planszy
            drawPieces(); // Narysuj pionki
        });
    }

    // Rysowanie siatki planszy
    private void drawGrid() {
        // Numery kolumn
        for (int col = 0; col < 25; col++) {
            double x = OFFSET_X + (col - 12) * (CELL_SIZE / 2.0);
            double y = OFFSET_Y - (9 * CELL_SIZE * 0.866) - 10;
            Text colNumber = new Text(String.valueOf(col));
            colNumber.setX(x - 5);
            colNumber.setY(y);
            colNumber.setFont(Font.font(12)); // Rozmiar czcionki
            root.getChildren().add(colNumber);
        }

        // Numery wierszy
        for (int row = 0; row < 17; row++) {
            double x = OFFSET_X - (13 * CELL_SIZE / 2.0) - 20;
            double y = OFFSET_Y + (row - 8) * (CELL_SIZE * 0.866);
            Text rowNumber = new Text(String.valueOf(row));
            rowNumber.setX(x);
            rowNumber.setY(y + 5);
            rowNumber.setFont(Font.font(12));
            root.getChildren().add(rowNumber);
        }

        // Linie siatki
        for (int row = 0; row < 17; row++) {
            for (int col = 0; col < 25; col++) {
                double x = OFFSET_X + (col - 12) * (CELL_SIZE / 2.0);
                double y = OFFSET_Y + (row - 8) * (CELL_SIZE * 0.866);

                Line horizontalLine = new Line(x - CELL_SIZE / 2.0, y, x + CELL_SIZE / 2.0, y); // Linie poziome
                Line verticalLine = new Line(x, y - CELL_SIZE / 2.0, x, y + CELL_SIZE / 2.0); // Linie pionowe

                horizontalLine.setStroke(Color.LIGHTGRAY); // Kolor linii
                verticalLine.setStroke(Color.LIGHTGRAY);

                root.getChildren().add(horizontalLine);
                root.getChildren().add(verticalLine);
            }
        }
    }

    // Rysowanie pól planszy
    private void drawFields() {
        boolean isYinAndYang = isYinAndYangEnabled();

        for (Field field : board.getFieldsInsideAStar()) {
            int row = field.getRow(); // Wiersz pola
            int col = field.getCol(); // Kolumna pola

            double x = OFFSET_X + (col - 12) * (CELL_SIZE / 2.0);
            double y = OFFSET_Y + (row - 8) * (CELL_SIZE * 0.866);

            Circle circle = new Circle(x, y, CELL_SIZE / 2.5); // Pole jako koło
            circle.setStroke(Color.BLACK); // Obrys koła
            circle.setStrokeWidth(1.5); // Grubość obrysu

            if (isYinAndYang && field.getHome() != HomeColor.NONE) {
                // Jeśli Yin and Yang jest aktywne, wszystkie pola startowe i docelowe są przezroczyste
                circle.setFill(Color.TRANSPARENT);
            } else if (field.getHome() != HomeColor.NONE && !field.hasPiece()) {
                // Ustaw półprzezroczysty kolor dla pustego domku
                circle.setFill(getTransparentColorForHome(field.getHome()));
            } else if (field.getHome() != HomeColor.NONE) {
                // Ustaw pełny kolor dla domku z pionkiem
                circle.setFill(getColorForHome(field.getHome()));
            } else {
                circle.setFill(Color.TRANSPARENT); // Zwykłe pola są przezroczyste
            }

            root.getChildren().add(circle);
        }
    }

    // Rysowanie pionków
    private void drawPieces() {
        for (Field field : board.getFieldsInsideAStar()) {
            Piece piece = field.getPiece(); // Pobierz pionek na polu

            int row = field.getRow(); // Wiersz pola
            int col = field.getCol(); // Kolumna pola

            double x = OFFSET_X + (col - 12) * (CELL_SIZE / 2.0);
            double y = OFFSET_Y + (row - 8) * (CELL_SIZE * 0.866);

            if (piece != null) {
                // Rysuj pionek jako koło
                Circle pieceCircle = new Circle(x, y, CELL_SIZE / 2.5);
                pieceCircle.setFill(getColorForPiece(piece)); // Kolor pionka
                root.getChildren().add(pieceCircle);
            } else if (field.getHome() != HomeColor.NONE) {
                // Rysuj pole domku bez pionka
                Circle transparentCircle = new Circle(x, y, CELL_SIZE / 2.5);
                transparentCircle.setStroke(Color.BLACK);
                transparentCircle.setFill(Color.TRANSPARENT);
                root.getChildren().add(transparentCircle);
            }
        }
    }

    // Pobierz półprzezroczysty kolor dla domku na podstawie HomeColor
    private Color getTransparentColorForHome(HomeColor home) {
        switch (home) {
            case RED:
                return Color.rgb(255, 0, 0, 0.3); // Półprzezroczysty czerwony
            case BLUE:
                return Color.rgb(0, 0, 255, 0.3); // Półprzezroczysty niebieski
            case GREEN:
                return Color.rgb(0, 255, 0, 0.3); // Półprzezroczysty zielony
            case YELLOW:
                return Color.rgb(255, 255, 0, 0.3); // Półprzezroczysty żółty
            case PURPLE:
                return Color.rgb(128, 0, 128, 0.3); // Półprzezroczysty fioletowy
            case BLACK:
                return Color.rgb(0, 0, 0, 0.3); // Półprzezroczysty czarny
            default:
                return Color.TRANSPARENT; // Brak koloru
        }
    }

    // Pobierz kolor dla domku na podstawie HomeColor
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

    // Pobierz kolor dla pionka na podstawie PieceColor
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

    // Rozmiar komórki w pikselach
    private static final int CELL_SIZE = 30;
    // Offset X dla rysowania planszy
    private static final int OFFSET_X = 400;
    // Offset Y dla rysowania planszy
    private static final int OFFSET_Y = 300;

    public static void main(String[] args) {
        launch(args); // Uruchomienie aplikacji JavaFX
    }
}
