package it.polimi.galaxy_trucker.serverClientTest;

import it.polimi.galaxy_trucker.ShipBoard;
import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ShipBoard board;
    private Set<Integer> usedIds;
    private List<ClientHandler> clients;
    private String username;

    public ClientHandler(Socket socket, Set<Integer> usedIds, List<ClientHandler> clients) {
        this.socket = socket;
        this.usedIds = usedIds;
        this.clients = clients;
        this.board = new ShipBoard(1);
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Ask the client for a username
            out.println("Enter your username:");
            username = in.readLine();
            if (username == null || username.trim().isEmpty()) {
                username = "Guest" + socket.getPort();
            }
            out.println("Welcome, " + username + "! You can send tile commands (x y id rot) or chat messages starting with \".\".");

            // Notify everyone that a new client joined.
            String joinMsg = "🟢 " + username + " has joined the game.";
            System.out.println(joinMsg);
            Server.broadcast(joinMsg, null);

            String input;
            while ((input = in.readLine()) != null) {
                input = input.trim();
                // Check if this is a chat message.
                if (input.toLowerCase().startsWith(".")) {
                    String chatMessage = input.substring(1).trim();
                    // Broadcast the chat message to everyone (excluding sender)
                    String fullChat = "💬 " + username + ": " + chatMessage;
                    Server.broadcast(fullChat, this);
                    // Optionally, echo to sender as well:
                    out.println("💬 You: " + chatMessage);
                } else {
                    // Otherwise, assume it's a tile placement command "x y id rot"
                    try {
                        String[] parts = input.split(" ");
                        if (parts.length != 4) {
                            throw new IllegalArgumentException("Invalid format! Use: x y id rot or \".\" your message");
                        }
                        int x = Integer.parseInt(parts[0]);
                        int y = Integer.parseInt(parts[1]);
                        int id = Integer.parseInt(parts[2]);
                        int rot = Integer.parseInt(parts[3]);

                        // Check if id is already used
                        synchronized (usedIds) {
                            if (usedIds.contains(id)) {
                                throw new IllegalArgumentException("ID " + id + " already used!");
                            }
                        }

                        // Try to set the tile. Only if setTile succeeds, add the id.
                        try {
                            board.setTile(x, y, id, rot);
                            // Mark id as used since placement succeeded.
                            synchronized (usedIds) {
                                usedIds.add(id);
                            }
                            String successMsg = "SUCCESS: " + username + " placed tile at (" + x + "," + y + ") with ID " + id + " and rotation " + rot;
                            out.println(successMsg);
                        } catch (IllegalArgumentException e) {
                            out.println("ERROR: " + e.getMessage());
                        }
                    } catch (NumberFormatException e) {
                        out.println("ERROR: Coordinates, ID, and rotation must be integers.");
                    } catch (IllegalArgumentException e) {
                        out.println("ERROR: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Connection with " + username + " lost.");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Notify others of client departure.
            String leaveMsg = "🔴 " + username + " has left the game.";
            System.out.println(leaveMsg);
            Server.broadcast(leaveMsg, this);
            Server.removeClient(this);
        }
    }

    // Used by Server.broadcast to send messages.
    public void sendMessage(String message) {
        out.println(message);
    }
}
