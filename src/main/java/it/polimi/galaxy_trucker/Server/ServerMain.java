package it.polimi.galaxy_trucker.Server;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.BoardType;
import it.polimi.galaxy_trucker.GameModel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

public class ServerMain {
    static Integer portNumber;
    static ArrayList<VirtualView> clients = new ArrayList<>();
    static GameModel gameModel = new GameModel();
    static Controller controller = new Controller(gameModel);

    public static void main(String[] args) {
        if(args.length > 0) {
            portNumber = Integer.parseInt(args[0]);
        } else {
            try {
                InputStream config = ServerMain.class.getClassLoader().getResourceAsStream("it/polimi/galaxy_trucker/public/config.json");
                if(config == null) throw new IOException("Could not find config.json");
                portNumber = new ObjectMapper().readTree(config).get("port").asInt();
            } catch (IOException e) {
                System.err.println("Could not read config.json: " + e.getMessage());
            }
        }

        System.out.println("Server Started!");
        try {
            System.out.println("Server running on IP: " + getLocalAddress() + " (LocalHostAddress:" + InetAddress.getLocalHost().getHostAddress() + ")");
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

    public static String getLocalAddress() {
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            while(nets.hasMoreElements()) {
                NetworkInterface netInt = nets.nextElement();
                String name = netInt.getDisplayName().toLowerCase();
                if(netInt.isUp() && !netInt.isLoopback() && !name.contains("virtual") && !name.contains("hyper-v")) {
                    Enumeration<InetAddress> addresses = netInt.getInetAddresses();
                    while(addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        String ip = addr.getHostAddress();
                        if(!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
                            return ip;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("Error fetching network interfaces: " + e.getMessage());
        }
        return "Unknown Address";
    }
}
