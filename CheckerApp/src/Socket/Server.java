package Socket;
import Game.Checkers;
import Game.Piece;
import Game.PieceType;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;

public class Server {

    public static final int PORT = 10000;
    public static LinkedList<ServerSomthing> serverList = new LinkedList<>();
    static private boolean gameStarted = false;

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Server Started");
        try {
            while (true) {
                Socket socket = server.accept();
                try {
                    if(serverList.size() <=1) {
                        serverList.add(new ServerSomthing(socket));
                        System.out.println("Get new connection");
                        if(serverList.size() == 2){
                            if(!gameStarted){
                                System.out.println("StartGame");
                                gameStarted = true;
                                Iterator<ServerSomthing> listIter = serverList.iterator();
                                listIter.next().send("0Black");
                                listIter.next().send("0White");
                            }
                        }
                    }
                    else{
                        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                        printWriter.println("Переполнение");
                        printWriter.flush();
                    }
                } catch (IOException e) {
                    socket.close();
                }
            }
        } finally {
            server.close();
        }
    }
}