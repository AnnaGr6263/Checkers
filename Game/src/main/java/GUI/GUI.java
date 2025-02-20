package GUI;

import board.BoardSetup;
import board.Field;
import board.Piece;
import board.enums.HomeColor;
import board.enums.PieceColor;
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

import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa odpowiedzialna za tworzenie i obsługę naszego GUI.
 */
public class GUI extends Application {

    private static List<GUI> guiInstances = new ArrayList<>(); // Lista wszystkich instancji GUI
    private static BoardSetup boardToInitialize; // Plansza przekazywana do GUI przed uruchomieniem
    private Pane root;                           // Główny kontener GUI
    private Stage stage;                        // Okno GUI
    private BoardSetup board;                    // Obiekt planszy
    private ClickHandler clickHandler = new ClickHandler(); // Obiekt do obsługi kliknięć
    private static GameManager gameManager = GameManager.getInstance();
    private int guiId = 0;
    private PieceColor playerColor;

    private static boolean isJavaFXRunning = false; // Flaga informująca, czy JavaFX działa


    /**
     * Konstruktor wymagany przez JavaFX (musi istnieć, nawet jeśli go nie używamy bezpośrednio).
     */
    public GUI() {};

    /**
     * Konstruktor z parametrem.
     *
     * @param id Id konkretnego GUI.
     */
    public GUI(int id) {
        this.board = boardToInitialize; // Ustawienie planszy po utworzeniu obiektu
        this.guiId = id;
    }

    /**
     * Ustawienie planszy do wykorzystania przez GUI
     *
     * @param board Wybrana plansza do ustawienia.
     */
    public static void setBoard(BoardSetup board) {
        boardToInitialize = board; // Przechowaj dowolną instancję klasy BoardSetup
    }

    /**
     * Metoda do sprawdzenia czy aktywowany został wariant YingAndYang
     *
     * @return Prawdę jeśli YingAndYang jest aktywny lub fałsz w przeciwnym wypadku.
     */
    private boolean isYinAndYangEnabled() {
        if (gameManager == null) {
            return false; // Jeśli gameManager nie został jeszcze zainicjalizowany, zakładamy brak aktywacji
        }

        YinAndYangManager yinAndYangManager = gameManager.getYinAndYangManager();
        return yinAndYangManager != null && yinAndYangManager.isYinAndYangEnabled();
    }

    /**
     * Uruchomienie JavaFX (jeśli jeszcze nie działa) i otwarcie GUI dla każdego gracza.
     */
    public static void launchForPlayers(int numberOfPlayers, List<PieceColor> piecesInGame) {
        synchronized (GUI.class) {      // Synchronizacja na klasie GUI
        if (!isJavaFXRunning) {
            new Thread(() -> Application.launch(GUI.class)).start(); // Uruchomienie JavaFX

                try {
                    while (!isJavaFXRunning) {
                        GUI.class.wait();       // Czekamy na uruchomienie JavaFX, mając blokadę
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();     // Zachowaj flagę przerwania
                }
            }
        }

        // Tworzenie dodatkowych okien w wątku JavaFX
        Platform.runLater(() -> {
            for (int i = 0; i < numberOfPlayers; i++) {
                GUI gui = new GUI(i);
                PieceColor pieceColor = piecesInGame.get(i);
                gui.setColor(pieceColor);
                guiInstances.add(gui);
                gui.startNewWindow();
            }
        });
    }

    /**
     * Getter.
     *
     * @return guiId
     */
    public int getGuiId() {
        return guiId;
    }

    /**
     * Pobiera instancje gui (wszystkie).
     *
     * @return zwraca wszystkie instancje gui.
     */
    public static List<GUI> getGuiInstances() {
        return guiInstances;
    }

    /**
     * Ustawia dla gui kolor jego pionków.
     *
     * @param color kolor pionków.
     */
    public void setColor(PieceColor color) {
        this.playerColor = color;
    }

    /**
     * Getter.
     *
     * @return kolor pionków w danym gui.
     */
    public PieceColor getColor() {
        return playerColor;
    }

    /**
     * Metoda do uruchomienia okna GUI.
     */
    private void startNewWindow() {
        Stage newStage = new Stage();
        this.stage = newStage;
        this.root = new Pane();
        drawGrid(); // Rysowanie siatki i etykiet
        drawFields(); // Rysowanie pól planszy
        drawPieces(); // Rysowanie pionków

        Scene scene = new Scene(root, 800, 600);
        GUI lastGui = guiInstances.get(guiInstances.size() - 1);
        newStage.setTitle("Chinese Checkers - Player " + lastGui.getColor().toString());
        newStage.setScene(scene);
        newStage.show();
    }

    /**
     * Metoda start() wymagana przez JavaFX.
     *
     * @param primaryStage Główne okno GUI.
     */
    @Override
    public void start(Stage primaryStage) {
        synchronized (GUI.class) {      // Synchronizacja powiadomienia
            isJavaFXRunning = true;     // JavaFX jest uruchomiony
            GUI.class.notifyAll();      // Powiadom wątek, który czekał w launchForPlayers()
        }
    }

    /**
     * Odświeżanie GUI
     */
    public void refresh() {
        System.out.println("Refreshing GUI..."); // Informacja debug
        Platform.runLater(() -> { // Wykonaj w wątku JavaFX
            root.getChildren().clear(); // Wyczyść wszystkie elementy GUI
            drawGrid(); // Narysuj siatkę
            drawFields(); // Narysuj pola planszy
            drawPieces(); // Narysuj pionki
        });
    }

    /**
     * Odświeżenie wszystkich instancji GUI.
     */
    public static void refreshAll() {
        for (GUI gui : guiInstances) {
            gui.refresh();
        }
    }

    /**
     * Rysowanie siatki planszy
     */
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

    /**
     * Rysowanie pól planszy
     */
    private void drawFields() {
        boolean isYinAndYang = isYinAndYangEnabled();

        for (Field field : board.getFieldsInsideAStar()) {
            int row = field.getRow(); // Wiersz pola
            int col = field.getCol(); // Kolumna pola

            double x = OFFSET_X + (col - 12) * (CELL_SIZE / 2.0);
            double y = OFFSET_Y + (row - 8) * (CELL_SIZE * 0.866);

            Circle circle = new Circle(x, y, CELL_SIZE / 2.5); // Pole jako koło
            circle.setOnMouseClicked(mouseEvent -> handleFieldClick(mouseEvent, field, circle));
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

    /**
     * Rysowanie pionków
     */
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
                pieceCircle.setOnMouseClicked(event -> handleFieldClick(event, field, pieceCircle));
                pieceCircle.setFill(getColorForPiece(piece)); // Kolor pionka
                root.getChildren().add(pieceCircle);
            } else if (field.getHome() != HomeColor.NONE) {
                // Rysuj pole domku bez pionka
                Circle transparentCircle = new Circle(x, y, CELL_SIZE / 2.5);
                transparentCircle.setOnMouseClicked(ev -> handleFieldClick(ev, field, transparentCircle));
                transparentCircle.setStroke(Color.BLACK);
                transparentCircle.setFill(Color.TRANSPARENT);
                root.getChildren().add(transparentCircle);
            }
        }
    }

    /**
     * Metoda wywoływana podczas kliknięcia myszą na pole, pionek, lub pole domku.
     * W każdym przypadku pobieramy odpowiedni obiekt pola odpowiadający tej akcji oraz koło, które graficznie
     * je reprezentuje.
     *
     * @param event Zdarzenie kliknięcia myszą.
     * @param field Kliknięte pole
     * @param circle Element graficzny pola w GUI.
     */
    private void handleFieldClick(MouseEvent event, Field field, Circle circle) {
        System.out.println("Clicked on GUI instance ID: " + guiId); // Dodaj informacje o ID GUI
        clickHandler.handle(field, circle, guiId);
    }

    /**
     * Pobiera półprzezroczysty kolor dla domku na podstawie HomeColor.
     *
     * @param home Kolor domku.
     * @return Półprzezroczystą wersję danego koloru.
     */
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

    /**
     * Pobiera kolor dla domku na podstawie HomeColor.
     *
     * @param home Kolor domku.
     * @return Kolor domku.
     */
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

    /**
     * Pobiera kolor dla pionka na podstawie koloru pionka.
     *
     * @param piece Konkretny pionek.
     * @return Kolor pionka.
     */
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

    /**
     * Metoda main.
     *
     * @param args Argumenty.
     */
    public static void main(String[] args) {
        launch(args); // Uruchomienie aplikacji JavaFX
    }
}
