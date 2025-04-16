package it.polimi.galaxy_trucker.Client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;

public class ClientThread extends Thread {
    private DataInputStream reader;
    private VirtualServer virtualServer;

    public ClientThread(DataInputStream reader, VirtualServer virtualServer) {
        this.reader = reader;
        this.virtualServer = virtualServer;
    }

    public void run() {
        System.out.println("ClientThread started");

        while (true) {
            try{
                String answer = reader.readLine();
                System.out.println(answer);

                virtualServer.handleMessageFromServer(answer);


            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private UpdateUICallBack updateUICallBack;

    void setUpdateUICallBack(UpdateUICallBack updateUICallBack) {
        this.updateUICallBack = updateUICallBack;
    }
}
