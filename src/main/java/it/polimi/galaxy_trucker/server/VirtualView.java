package it.polimi.galaxy_trucker.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.common.Logger;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class VirtualView extends Thread {
    BufferedReader in;
    PrintWriter out;
    Socket clientSocket;
    Controller controller;

    public VirtualView(Socket clientSocket, Controller controller) {
        this.clientSocket = clientSocket;
        this.controller = controller;

        try{
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    public void run(){
        String line;
        try {
            while((line = in.readLine()) != null ) {
                Logger.logDebug("RECEIVED: " + line);
                this.controller.processCmd(this, line);
            }
            Logger.logInfo("Client disconnected");
        } catch (SocketException e) {
            Logger.logWarning("Client disconnected: " + e.getMessage());
        } catch (IOException e) {
            Logger.logError("Error reading from client: " + e.getMessage());
        } finally {
            this.controller.removeClient(this);
            close();
        }
    }

    public void close() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                Logger.logDebug("Client connection closed");
            }
            if (in != null) in.close();
            if (out != null) out.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
