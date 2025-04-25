package it.polimi.galaxy_trucker.Client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketException;

public class ClientThread extends Thread {
    private DataInputStream reader;
    private VirtualServer virtualServer;

    public ClientThread(DataInputStream reader, VirtualServer virtualServer) {
        this.reader = reader;
        this.virtualServer = virtualServer;
    }

    public void run() {
        System.out.println("ClientThread started");

        try {
            while (true) {
                String msg = reader.readLine();
                System.out.println("RECEIVED MESSAGE: " + msg);
                if (msg == null) {
                    System.out.println("Client disconnected normally.");
                    virtualServer.handleServerDisconnect();
                    break;
                }
                virtualServer.handleMessageFromServer(msg);
            }
        } catch (SocketException e) {
            System.err.println("Client forcibly disconnected.");
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        } finally {
            close();
        }
    }

    public void close() {
        try {
            System.out.println("Closing connection with client...");

            if (reader != null) {
                reader.close();
            }

            System.out.println("Client connection closed.");
        } catch (IOException e) {
            System.err.println("Error closing client connection: " + e.getMessage());
        }
    }

    private UpdateUICallBack updateUICallBack;

    void setUpdateUICallBack(UpdateUICallBack updateUICallBack) {
        this.updateUICallBack = updateUICallBack;
    }
}
