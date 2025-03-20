package it.polimi.galaxy_trucker.serverClientTest;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 8080;
    // Track used tile IDs globally
    private static Set<Integer> usedIds = new HashSet<>();
    // Keep track of all connected clients
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Server is running on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, usedIds, clients);
                synchronized(clients) {
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
