package it.polimi.galaxy_trucker.Server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class VirtualView implements Runnable {
    BufferedReader in = null;
    PrintWriter out = null;
    Socket clientSocket = null;
    Controller controller = null;
    ArrayList<VirtualView> clients = new ArrayList<VirtualView>();

    public VirtualView(Socket clientSocket, Controller controller, ArrayList<VirtualView> clients) {
        this.clientSocket = clientSocket;
        this.controller = controller;
        this.clients = clients;

        try{
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Object object) {

        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
        json = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        this.out.println(json);
    }

    void networkEventLoop(){
        String line;
        try {
            while((line = in.readLine()) != null ) {
                System.out.println("RECEIVED MESSAGE: " + line);
                this.controller.processCmd(this, line);
            }
            System.out.println("done");
        } catch (SocketException e) {
            System.err.println("Client disconnected: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading from client: " + e.getMessage());
        } finally {
            this.controller.clientDisconnect(this);
            //close();
        }
    }

    @Override
    public void run() {
        networkEventLoop();
    }


    public void close() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close(); // Closes the connection to the client
            }

            if (in != null) in.close();  // Input stream
            if (out != null) out.close(); // Output stream

            System.out.println("Client connection closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
