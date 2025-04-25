package it.polimi.galaxy_trucker.Server;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.BoardType;
import it.polimi.galaxy_trucker.GameModel;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ServerMain {
    static Integer portNumber;
    static ArrayList<VirtualView> clients = new ArrayList<>();
    static GameModel gameModel = new GameModel(BoardType.LEVEL_TWO);
    static Controller controller = new Controller(gameModel);

    public static void main(String[] args) {
        if(args.length > 0) portNumber = Integer.parseInt(args[0]);
        if(args.length > 0){
            portNumber = Integer.parseInt(args[0]);
        } else {
            try {
                final String CONFIG_PATH = "src/main/resources/it/polimi/galaxy_trucker/public/config.json";
                portNumber = new ObjectMapper().readTree(new File(CONFIG_PATH)).get("port").asInt();
            } catch (IOException e) {
                System.err.println("Could not read config.json: " + e.getMessage());
            }
        }

        System.out.println("Server Started!");
        try {
            System.out.println("Server running on IP: " + InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        ServerSocket serverSocket = null;
        try {
            serverSocket = (portNumber == null)? new ServerSocket(0) : new ServerSocket(portNumber);
        } catch (IOException e){
            System.err.println("Could not listen on port: " + portNumber);
            System.exit(1);
        }

        System.out.println("Listening on port " + serverSocket.getLocalPort());


        while(true) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();

            } catch (IOException e) {
                System.err.println("Accept failed");
                System.exit(1);
            }
            System.out.println("Accepted");
            VirtualView virtualView = new VirtualView(clientSocket, controller, clients);
            Thread t = new Thread(virtualView);
            clients.add(virtualView);
            t.start();
        }

    }
}
