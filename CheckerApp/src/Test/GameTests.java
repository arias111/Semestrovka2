package Test;
import Game.MoveResult;
import Game.MoveType;
import Game.Piece;
import Game.PieceType;
import Socket.Server;
import Socket.ServerSomthing;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;

public class GameTests {
    private Server server;
    private Socket socket;
    private  ServerSomthing serverSomthing;
    private  ArrayList<Connection> list;

    @Test
    public void moveDir(){
        PieceType pieceType = new PieceType(1);
    }
    @BeforeEach
    public void startServer() throws Exception{
        if(serverSomthing != null) serverSomthing.downService();
        serverSomthing = new ServerSomthing(socket);
        serverSomthing.start();
        Thread.sleep(100);
    }
    @AfterEach
    public void disconnect() throws Exception{
        if(socket != null){
            socket.close();
        }
        socket = null;
        Thread.sleep(100);
    }


    private Socket createSocket() throws Exception{
        socket = new Socket("localhost",10000);
        Thread.sleep(200);
        return socket;
    }



}
