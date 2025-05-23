package it.polimi.galaxy_trucker.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.server.controller.MainController;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Enumeration;

public class ServerMain {
    static MainController controller = new MainController(new GameModel());

    public static void main(String[] args) {
        Integer portNumber = null;
        boolean debugMode = false;

        // Parse command-line arguments
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-p", "--port" -> {
                    if (i + 1 < args.length) {
                        try {
                            portNumber = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            Logger.logWarning("Invalid port number: " + args[i] + ". Falling back to config file.");
                        }
                    } else {
                        Logger.logWarning("Missing port number after --port. Falling back to config file.");
                    }
                }

                case "-d", "--debug" -> debugMode = true;

                default -> Logger.logWarning("Unknown argument: " + args[i]);
            }
        }

        // Load from config if no valid port provided
        if (portNumber == null) {
            try (InputStream config = ServerMain.class.getClassLoader().getResourceAsStream("public/config.json")) {
                if (config == null) throw new IOException("Could not find config.json");
                portNumber = new ObjectMapper().readTree(config).get("port").asInt();
            } catch (IOException e) {
                Logger.logError("Could not read config.json: " + e.getMessage());
            }
        }

        Logger.logInfo("Server Started!");
        Logger.setDebugMode(debugMode);

        try {
            Logger.logInfo("Server running on IP: " + getLocalAddress()
                    + " (LocalHostAddress: " + InetAddress.getLocalHost().getHostAddress() + ")");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        // Start server socket
        ServerSocket serverSocket;
        try {
            serverSocket = (portNumber == null) ? new ServerSocket(0) : new ServerSocket(portNumber);
        } catch (IOException e) {
            Logger.logError("Could not listen on port: " + portNumber);
            System.exit(1);
            return;
        }

        Logger.logInfo("Listening on port " + serverSocket.getLocalPort());

        // Main server loop
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                Logger.logInfo("New client accepted");
                VirtualView virtualView = new VirtualView(clientSocket, controller);
                virtualView.start();
            } catch (IOException e) {
                Logger.logError("Accept failed: " + e.getMessage());
                System.exit(1);
            }
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
