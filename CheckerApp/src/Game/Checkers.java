package Game;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Checkers extends Application {

    public static final int TILE_SIZE = 65;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;
    private String name;
    private ListView listView = new ListView();
    private TextField textField = new TextField();
    private TextField textField1 = new TextField();
    private Button button = new Button();
    private Button button1 = new Button();
    private Label label = new Label();
    private Label nameLabel = new Label();
    private Tile[][] board = new Tile[WIDTH][HEIGHT];
    private Stage stage1;
    private Button button2 = new Button();
    private Group tileGroup = new Group();
    private Group pieceGroup = new Group();
    private Pane root1 = new Pane();
    private Pane root3 = new Pane();


    private Parent createFirstContent() {
        root3.setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        root3.getChildren().addAll(tileGroup, pieceGroup);

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                board[x][y] = tile;

                tileGroup.getChildren().add(tile);

                Piece piece = null;

                if (y <= 2 && (x + y) % 2 != 0) {
                    piece = makePiece(PieceType.BLACK, x, y);
                }

                if (y >= 5 && (x + y) % 2 != 0) {
                    piece = makePiece(PieceType.WHITE, x, y);
                }

                if (piece != null) {
                    tile.setPiece(piece);
                    pieceGroup.getChildren().add(piece);
                }
            }
        }

        root3.setPrefSize(800, 600);
        listView.setTranslateX(540);
        listView.setTranslateY(30);
        listView.setPrefWidth(240);
        root3.getChildren().add(listView);
        textField.setTranslateX(560);
        textField.setTranslateY(450);
        root3.getChildren().add(textField);
        button.setTranslateX(710);
        button.setTranslateY(450);
        button.setText("Отправить");
        root3.getChildren().add(button);
        label.setTranslateX(350);
        label.setTranslateY(550);
        label.setText("Status");
        root3.getChildren().add(label);
        button2.setTranslateX(400);
        button2.setTranslateY(550);
        button2.setText("Connect");
        root3.getChildren().add(button2);

        return root3;
    }

    private Parent firstPanel() {
        root1.setPrefSize(400, 400);
        textField1.setTranslateX(130);
        textField1.setTranslateY(200);
        button1.setTranslateX(170);
        button1.setTranslateY(250);
        button1.setText("Далее");
        button1.setOnAction(this::nameAction);
        label.setTranslateX(170);
        label.setTranslateY(170);
        label.setText("Введите имя");
        root1.getChildren().add(label);
        root1.getChildren().add(textField1);
        root1.getChildren().add(button1);

        return root1;
    }

    public void nameAction(javafx.event.ActionEvent action) {
        String text = textField1.getText();
        name = text;
        System.out.println(name);
        this.stage1.close();
        Stage stage = new Stage();
        stage.setScene(new Scene(createFirstContent()));
        stage.show();
    }


    private MoveResult tryMove(Piece piece, int newX, int newY) {
        if (board[newX][newY].hasPiece() || (newX + newY) % 2 == 0) {
            return new MoveResult(MoveType.NONE);
        }

        int x0 = toBoard(piece.getOldX());
        int y0 = toBoard(piece.getOldY());

        if (Math.abs(newX - x0) == 1 && newY - y0 == piece.getType().moveDir) {
            return new MoveResult(MoveType.NORMAL);
        } else if (Math.abs(newX - x0) == 2 && newY - y0 == piece.getType().moveDir * 2) {

            int x1 = x0 + (newX - x0) / 2;
            int y1 = y0 + (newY - y0) / 2;

            if (board[x1][y1].hasPiece() && board[x1][y1].getPiece().getType() != piece.getType()) {
                return new MoveResult(MoveType.KILL, board[x1][y1].getPiece());
            }
        }

        return new MoveResult(MoveType.NONE);
    }

    private int toBoard(double pixel) {
        return (int)(pixel + TILE_SIZE / 2) / TILE_SIZE;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
//        Scene scene = new Scene(createContent());

        primaryStage.setTitle("CheckersApp");
        primaryStage.setScene(new Scene(firstPanel()));
//        primaryStage.setScene(new Scene(createFirstContent()));
//        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Piece makePiece(PieceType type, int x, int y) {
        Piece piece = new Piece(type, x, y);

        piece.setOnMouseReleased(e -> {
            int newX = toBoard(piece.getLayoutX());
            int newY = toBoard(piece.getLayoutY());

            MoveResult result;

            if (newX < 0 || newY < 0 || newX >= WIDTH || newY >= HEIGHT) {
                result = new MoveResult(MoveType.NONE);
            } else {
                result = tryMove(piece, newX, newY);
            }

            int x0 = toBoard(piece.getOldX());
            int y0 = toBoard(piece.getOldY());

            switch (result.getType()) {
                case NONE:
                    piece.abortMove();
                    break;
                case NORMAL:
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);
                    break;
                case KILL:
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);

                    Piece otherPiece = result.getPiece();
                    board[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
                    pieceGroup.getChildren().remove(otherPiece);
                    break;
            }
        });

        return piece;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
