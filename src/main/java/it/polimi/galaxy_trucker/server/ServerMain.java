package it.polimi.galaxy_trucker.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.common.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Enumeration;

public class ServerMain {
    static Integer portNumber;
    static Controller controller = new Controller(new GameModel());

    public static void main(String[] args) {
        if(args.length > 0) {
            portNumber = Integer.parseInt(args[0]);
        } else {
            try {
                InputStream config = ServerMain.class.getClassLoader().getResourceAsStream("public/config.json");
                if(config == null) throw new IOException("Could not find config.json");
                portNumber = new ObjectMapper().readTree(config).get("port").asInt();
            } catch (IOException e) {
                Logger.logError("Could not read config.json: " + e.getMessage());
            }
        }

        Logger.logInfo("Server Started!");
        try {
            Logger.logInfo("Server running on IP: " + getLocalAddress() + " (LocalHostAddress:" + InetAddress.getLocalHost().getHostAddress() + ")");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        ServerSocket serverSocket = null;
        try {
            serverSocket = (portNumber == null)? new ServerSocket(0) : new ServerSocket(portNumber);
        } catch (IOException e){
            Logger.logError("Could not listen on port: " + portNumber);
            System.exit(1);
        }

        Logger.logInfo("Listening on port " + serverSocket.getLocalPort());

        while(true) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                Logger.logError("Accept failed");
                System.exit(1);
            }
            Logger.logInfo("New client accepted");
            VirtualView virtualView = new VirtualView(clientSocket, controller);
            virtualView.start();

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
            Logger.logError("Error fetching network interfaces: " + e.getMessage());
        }
        return "Unknown Address";
    }
}
