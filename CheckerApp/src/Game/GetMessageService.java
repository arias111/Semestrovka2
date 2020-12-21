package Game;

import Game.Checkers;

import java.io.IOException;

public class GetMessageService extends Thread {
    Checkers game;
    private boolean isRun = true;
public GetMessageService(Checkers game){
    this.game = game;
}

public void stopService(){
isRun = false;
}

@Override
public void run(){
    try {
        while (isRun) {
            System.out.println("Run service");
            String str = game.in.readLine();
            System.out.println("Get: " + str);
            game.receiveMsg(str);
        }
    } catch (IOException e) {
        System.err.println("Ошибка при получении сообщения.");
        e.printStackTrace();
    }
}
}
