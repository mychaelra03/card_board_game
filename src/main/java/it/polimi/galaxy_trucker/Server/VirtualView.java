package it.polimi.galaxy_trucker.Server;

import java.beans.PropertyChangeEvent;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class VirtualView implements Runnable {
    BufferedReader in = null;
     PrintWriter out = null;
    Socket clientSocket = null;
    Controller controller = null;
    ArrayList<VirtualView> clients = new ArrayList<VirtualView>();
    ModelListener listener;

    public VirtualView(Socket clientSocket, Controller controller, ArrayList<VirtualView> clients) {
        this.clientSocket = clientSocket;
        this.controller = controller;
        this.clients = clients;

        CallBack callBack = (PropertyChangeEvent evt) -> {
            String debugStr= evt.getPropertyName() + ": " + evt.getOldValue() + " -> " + evt.getNewValue();
            for(VirtualView client : clients) {
                client.out.println(debugStr);

            }
        };

        this.listener = new ModelListener(callBack);
        controller.setListener(this.listener);


        try{
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void networkEventLoop(){
        String username = "";
        try{
            while(clientSocket.isConnected() ){

                username = in.readLine();

                this.controller.registerPlayer(this,username);

                System.out.println("a");
//                System.out.println(player + " tile positioned : ("+in.readInt()+","+in.readInt()+") of id: " + in.readUTF() + " with rotation: "+in.readInt());

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
