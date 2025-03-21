package it.polimi.galaxy_trucker.serverClientTest;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 8080;
    private static Set<Integer> usedIds = new HashSet<>();
    //private static List<Integer> cards = new ArrayList<>();
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Server is running on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, usedIds, clients);
                synchronized(clients) {
                    System.out.println("new player connected.");
                    //if(clients.size() > 4) continue;
                    clients.add(clientHandler);
                }
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcast a message to all clients except optionally the sender.
    public static void broadcast(String message, ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                // Exclude the sender if provided
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
    }

    // Remove a client when they disconnect.
    public static void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }
}
