package it.polimi.galaxy_trucker.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.PingMessage;
import it.polimi.galaxy_trucker.server.controller.MainController;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class VirtualView extends Thread {
    private final BufferedReader in;
    private final PrintWriter out;
    private final Socket clientSocket;
    private final MainController controller;
    private Timer pingTimer;

    public VirtualView(Socket clientSocket, MainController controller) {
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

    public void run() {
        startPingLoop();

        try {
            while (true) {
                String msg = in.readLine();
                Logger.logDebug("RECEIVED: " + msg);
                if (msg == null) {
                    Logger.logInfo("Client disconnected.");
                    break;
                }
                this.controller.processCommand(this, msg);
            }

        } catch (SocketException e) {
            Logger.logWarning("Client disconnected: " + e.getMessage());
        } catch (IOException e) {
            if ("Stream closed".equals(e.getMessage())) {
                Logger.logInfo("Stream closed locally.");
            } else {
                Logger.logError("IOException: " + e.getMessage());
            }
        } finally {
            close();
        }
    }

    private void startPingLoop() {
        pingTimer = new Timer(true);
        pingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendMessage(new PingMessage());
            }
        }, 3000, 3000);  // send every 3s
    }

    public void close() {

        this.controller.removeClient(this);

        try {

            if (in != null) in.close();
            if (out != null) out.close();

            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                Logger.logDebug("Client connection closed");
            }
            if (pingTimer != null) pingTimer.cancel();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
