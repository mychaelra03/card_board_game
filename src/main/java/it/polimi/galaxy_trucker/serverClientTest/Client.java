package it.polimi.galaxy_trucker.serverClientTest;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        String serverAddress = "localhost";
        int port = 8080;

        try (Socket socket = new Socket(serverAddress, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            // Print the server's welcome message(s)
            String response;
            if ((response = in.readLine()) != null) {
                System.out.println(response); // "Enter your username:"
            }
            String username = console.readLine();
            out.println(username);

            // Read the welcome confirmation from the server.
            if ((response = in.readLine()) != null) {
                System.out.println(response);
            }

            // Start a thread to listen for messages from the server.
            new Thread(() -> {
                String serverMsg;
                try {
                    while ((serverMsg = in.readLine()) != null) {
                        System.out.println(serverMsg);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            }).start();

            // Main thread: send messages to the server.
            String input;
            while ((input = console.readLine()) != null) {
                out.println(input);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
