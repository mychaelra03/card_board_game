package it.polimi.galaxy_trucker.Server;

import it.polimi.galaxy_trucker.BoardType;
import it.polimi.galaxy_trucker.GameModel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    static int portNumber = 8080;
    static GameModel gameModel = new GameModel(BoardType.LEVEL_TWO);

    public static void main(String[] args) {

        System.out.println("Server Started!");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);
        }catch (IOException e){
            e.printStackTrace();
        }

        System.out.println("Listening on port " + portNumber);


        while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Accepted");
            Controller controller = new Controller(gameModel);
            VirtualView virtualView = new VirtualView(clientSocket, controller);
            Thread t = new Thread(virtualView);
            t.start();
        }



    }
}
