package it.polimi.galaxy_trucker.Server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.Common.PlayerInfo;
import it.polimi.galaxy_trucker.Common.PlayersInfoMessage;
import it.polimi.galaxy_trucker.Player;

import java.beans.PropertyChangeEvent;
import java.io.*;
import java.net.Socket;
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

//        CallBack callBack = ( evt) -> {
//            for(VirtualView client : clients) {
//                client.out.println(evt.getNewValue());
//
//            }
//        };


        try{
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPlayerAdded(PlayersInfoMessage players) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(players);

            this.out.println(json);
    }


    public void broadcastMessage(String message) {

        clients.forEach(client -> {client.out.println(message);});
    }

    void networkEventLoop(){
        String line;
        try{
            while((line = in.readLine()) != null ){
                this.controller.processCmd(this,line);
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
