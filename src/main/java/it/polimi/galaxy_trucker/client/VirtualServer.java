package it.polimi.galaxy_trucker.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.client.popups.PendingPopup;
import it.polimi.galaxy_trucker.client.popups.Popup;
import it.polimi.galaxy_trucker.common.Logger;
import javafx.application.Platform;

import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;

public class VirtualServer {
    Socket clientSocket;
    BufferedReader in = null;
    PrintWriter out = null;

    private UICallBack UICallBack;
    private final Queue<String> messageQueue = new LinkedList<>();

    private static VirtualServer instance;

    private VirtualServer() {}

    public static VirtualServer getInstance() {
        if(instance == null){
            instance = new VirtualServer();
        }
        return instance;
    }

    public void attemptConnection(String hostName, int portNumber) throws IOException {
        clientSocket = new Socket();
        clientSocket.connect(new InetSocketAddress(hostName, portNumber), 500);
        disconnecting = false;

        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            Logger.logError("Error setting up streams: " + e.getMessage());
            return;
        }

        Thread readerThread = new Thread(this::listenForMessages);
        readerThread.setDaemon(true);
        readerThread.start();
    }

    private volatile boolean disconnecting = false;

    public void listenForMessages() {
        Logger.logInfo("ClientThread started");

        try {
            while (true) {
                String msg = in.readLine();
                Logger.logDebug("RECEIVED MESSAGE: " + msg);
                if (msg == null) {
                    Logger.logInfo("Client disconnected normally.");
                    PendingPopup.set(Popup.newAlertPopup("title.disconnected", "description.server_closed"));
                    break;
                }
                handleMessageFromServer(msg);
            }
        } catch (SocketException e) {
            if (disconnecting) {
                Logger.logInfo("Client disconnected itself normally.");
            } else {
                Logger.logWarning("Client forcibly disconnected.");
                PendingPopup.set(Popup.newAlertPopup("title.disconnected", "description.connection_lost"));
            }
        } catch (IOException e) {
            Logger.logError("IOException: " + e.getMessage());
            //PendingPopup.set(Popup.newAlertPopup("title.disconnected", "description.unexpected_error"));
        } finally {
            close();
            Platform.runLater(() -> SceneSwitch.switchScene("LoginScene"));
        }
    }

    public void close() {
        disconnecting = true;
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                Logger.logDebug("Client connection closed.");
            }
            if (in != null) in.close();
            if (out != null) out.close();

        } catch (IOException e) {
            Logger.logError("Error closing server connection: " + e.getMessage());
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

    public void handleMessageFromServer(String msg) {
        UICallBack callBack;
        synchronized(this) {
            callBack = this.UICallBack;
            if (callBack != null) {
                dispatchToUI(callBack, msg);
            } else {
                messageQueue.add(msg);
            }

        }
    }

    public synchronized void setCallBack(UICallBack newCallBack) {
        this.UICallBack = newCallBack;
        if (newCallBack != null) {
            while (!messageQueue.isEmpty()) {
                String queued = messageQueue.poll();
                dispatchToUI(newCallBack, queued);
            }
        }
    }

    private void dispatchToUI(UICallBack callBack, String msg) {
        try {
            callBack.process(msg);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
