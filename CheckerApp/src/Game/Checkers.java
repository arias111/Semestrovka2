package Game;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Checkers extends Application {

    public static final int TILE_SIZE = 65;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;
    private TextArea chatArea = new TextArea();
    private TextField chatField = new TextField();
    private TextField nameTextField = new TextField();
    private TextField connectionTextField = new TextField();
    private Button sendMessageButton = new Button();
    private Button sendNameButton = new Button();
    private Label enterNameLabel = new Label();
    private Label sendIdButton = new Label();
    private Tile[][] board = new Tile[WIDTH][HEIGHT];
    private Group tileGroup = new Group();
    private Group pieceGroup = new Group();
    private Pane root1 = new Pane();
    private Pane root3 = new Pane();
    public boolean youTurn = false;
    public String playerColor;
    public PrintWriter out;
    public BufferedReader in;
    private String username = "";
    public Socket playerSocket;
    public static String ipAddr = "localhost";
    public static int port = 10000;
    public GetMessageService messageService;
    private boolean gameIsStart;

    private void startConnection(){
        try {
            System.out.println("Try to create connection");
            playerSocket = new Socket(ipAddr,port);
            in = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
            out = new PrintWriter(playerSocket.getOutputStream());
            out.println(username);
            out.flush();
            sendMsg("2"+username+" connect");
            messageService = new GetMessageService(this);
            messageService.start();
            System.out.println("Connection create");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


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
        chatArea.setTranslateX(540);
        chatArea.setTranslateY(30);
        chatArea.setPrefWidth(240);
        root3.getChildren().add(chatArea);
        chatField.setTranslateX(560);
        chatField.setTranslateY(450);
        root3.getChildren().add(chatField);
        sendMessageButton.setTranslateX(710);
        sendMessageButton.setTranslateY(450);
        sendMessageButton.setText("Отправить");
        sendMessageButton.setOnAction(this::sendChatAction);
        root3.getChildren().add(sendMessageButton);


        return root3;
    }

    private void sendChatAction(ActionEvent actionEvent) {
        String chatmsg = chatField.getText().trim();
        if(!chatmsg.isEmpty()){
            sendMsg("2"+username+": "+chatmsg);
            chatField.setText("");
        }
    }

    private Parent firstPanel() {
        root1.setPrefSize(400, 400);
        nameTextField.setTranslateX(130);
        nameTextField.setTranslateY(200);
        sendNameButton.setTranslateX(170);
        sendNameButton.setTranslateY(250);
        sendNameButton.setText("Далее");
        sendNameButton.setOnAction(this::nameAction);
        enterNameLabel.setTranslateX(170);
        enterNameLabel.setTranslateY(170);
        enterNameLabel.setText("Введите имя");
        sendIdButton.setTranslateX(130);
        sendIdButton.setTranslateY(300);
        sendIdButton.setText("Введите ip и port");
        connectionTextField.setTranslateX(130);
        connectionTextField.setTranslateY(320);
        root1.getChildren().add(sendIdButton);
        root1.getChildren().add(connectionTextField);
        root1.getChildren().add(enterNameLabel);
        root1.getChildren().add(nameTextField);
        root1.getChildren().add(sendNameButton);

        return root1;
    }

    public void nameAction(javafx.event.ActionEvent action) {
        String text = nameTextField.getText();
        username = text;
        System.out.println(username);
        Stage stage = new Stage();
        stage.setScene(new Scene(createFirstContent()));
        stage.show();
        ((Node)(action.getSource())).getScene().getWindow().hide();
        startConnection();
    }


    private MoveResult tryMove(Piece piece, int newX, int newY) {
        System.out.println("tryMove");
        return move(piece,newX,newY);

    }

    private MoveResult move(Piece piece, int newX, int newY){
        System.out.println("NewX: "+ newX);
        System.out.println("NewY: "+ newY);
        System.out.println("Move action");
        if (board[newX][newY].hasPiece() || (newX + newY) % 2 == 0) {
            System.out.println("Move1");
            return new MoveResult(MoveType.NONE);
        }

        int x0 = toBoard(piece.getOldX());
        int y0 = toBoard(piece.getOldY());
        System.out.println("x0: "+x0);
        System.out.println("y0: "+y0);
        if (Math.abs(newX - x0) == 1 && newY - y0 == piece.getType().moveDir) {
            System.out.println("MoveNorm");
            return new MoveResult(MoveType.NORMAL);
        } else if (Math.abs(newX - x0) == 2 && newY - y0 == piece.getType().moveDir * 2) {
            System.out.println("Move2");

            int x1 = x0 + (newX - x0) / 2;
            int y1 = y0 + (newY - y0) / 2;

            if (board[x1][y1].hasPiece() && board[x1][y1].getPiece().getType() != piece.getType()) {
                System.out.println("Move kill");
                return new MoveResult(MoveType.KILL, board[x1][y1].getPiece());
            }
        }
        System.out.println("FinalMove");
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
            if(piece.type.getColor().equals(playerColor)) {
                System.out.println(piece.getLayoutX() + " / " + piece.getLayoutY());
                System.out.println(toBoard(piece.getLayoutX()) + " / " + toBoard(piece.getLayoutY()));
                turnCheck(piece, type, toBoard(piece.getLayoutX()), toBoard(piece.getLayoutY()));
            }else{
                piece.abortMove();
            }
        });

        return piece;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void turnCheck(Piece piece, PieceType type, int newX, int newY){
        System.out.println("turnCheck");
        System.out.println("Color1: " + type.getColor());
        System.out.println("Color2: " + playerColor);
            System.out.println(newX);
            System.out.println(newY);

            MoveResult result;

            if (newX < 0 || newY < 0 || newX >= WIDTH || newY >= HEIGHT) {
                result = new MoveResult(MoveType.NONE);
            } else {

                    result = tryMove(piece, newX, newY);


            }
            int pieceIndex = pieceGroup.getChildren().indexOf(piece);
            int x0 = toBoard(piece.getOldX());
            int y0 = toBoard(piece.getOldY());
            System.out.println("Result: " + result.getType().name());
            switch (result.getType()) {
                case NONE:
                    System.out.println("abort");
                    piece.abortMove();
                    break;
                case NORMAL:
                    System.out.println("normal");
                    sendMsg("1#" + pieceIndex + ";" + newX + ";" + newY);
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);
                    break;
                case KILL:
                    System.out.println("kill");
                    sendMsg("1#" + pieceIndex + ";" + newX + ";" + newY);
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);
                    Piece otherPiece = result.getPiece();
                    board[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
                    otherPiece.deletePiece();
                    break;
            }
    }

    public void receiveMsg(String str) {
        System.out.println("ReceiveMsg: " + str);
        if(str.startsWith("0")){
            if(str.startsWith("0Black")){
                System.out.println("Black");
                playerColor = "Black";;
                youTurn = false;
                startGame();
            } else if(str.startsWith("0White")){
                System.out.println("White");
                playerColor = "White";
                youTurn = true;
                startGame();
            }
        } else {
            if(str.startsWith("1#")){
                String info = str.substring(2);
                String [] data = info.split(";");
                Piece actionPiece = getPiece(Integer.parseInt(data[0]));
                System.out.println("ActionX: " + actionPiece.getOldX());
                System.out.println("ActionY: " + actionPiece.getOldY());
                turnCheck(actionPiece,PieceType.getType(playerColor),Integer.parseInt(data[1]),Integer.parseInt(data[2]));
            }
        }
        if(str.startsWith("2")){
            displayText(str.substring(1));
        }

    }

    private void displayText(String text) {
        chatArea.appendText(text+"\n");
    }

    private Piece getPiece(int index){
        System.out.println("x: " + index);
        return (Piece)pieceGroup.getChildren().get(index);
    }

    private void startGame() {
        System.out.println("GameStart");
        gameIsStart = true;
    }

    public void sendMsg(String str){
        System.out.println("Send: "+str);
        out.println(str);
        out.flush();
    }
}
