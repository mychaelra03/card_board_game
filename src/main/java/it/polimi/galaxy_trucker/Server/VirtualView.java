package it.polimi.galaxy_trucker.Server;

import java.beans.PropertyChangeEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class VirtualView implements Runnable {
    BufferedReader in = null;
    PrintWriter out = null;
    Socket clientSocket = null;
    Controller controller = null;
    ModelListener listener;

    public VirtualView(Socket clientSocket, Controller controller) {
        this.clientSocket = clientSocket;
        this.controller = controller;

        CallBack callBack = (PropertyChangeEvent evt) -> {
            String debugStr= evt.getPropertyName() + ": " + evt.getOldValue() + " -> " + evt.getNewValue();
            out.println(debugStr);
        };

        this.listener = new ModelListener(callBack);
        controller.setListener(this.listener);


        try{
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(),true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void networkEventLoop(){
        String uN,c = "";
        try{
            while(clientSocket.isConnected()){
                uN = in.readLine();
                c = in.readLine();
                String player = this.controller.processCmd(uN,c);
                System.out.println(player);

            }
            System.out.println("done");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        networkEventLoop();
    }
}
