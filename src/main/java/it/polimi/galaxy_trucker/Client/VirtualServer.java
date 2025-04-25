package it.polimi.galaxy_trucker.Client;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.galaxy_trucker.Client.Network.MessageSender;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class VirtualServer {
    DataInputStream in = null;
    PrintWriter out = null;

    ClientThread clientThread;
    private UpdateUICallBack updateUICallBack;
    private final Queue<String> messageQueue = new LinkedList<>();


    private static VirtualServer instance;

    private VirtualServer() {

    }
    public static VirtualServer getInstance() {
        if(instance == null){
            instance = new VirtualServer();
        }
        return instance;
    }

    public void start(String hostName, int portNumber) throws IOException {

        Socket socket = new Socket(hostName, portNumber);

        BufferedReader stdIn = null;
        try{
            out = new PrintWriter(socket.getOutputStream(),true);
            in = new DataInputStream(socket.getInputStream());
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException e) {
            System.err.println(e + " " + hostName);
            System.exit(1);
        }

        this.clientThread = new ClientThread(in,this);
        clientThread.setUpdateUICallBack(this.updateUICallBack);
        clientThread.start();
    }

    public void sendCmd(Object obj){

        MessageSender sender = new MessageSender(out);
        sender.send(obj);

    }

    public void sendPlacedTileData(Integer col, Integer row, String id, int rot) {

        out.write(col);
        out.write(row);
        out.write(id);
        out.write(rot);
        out.flush();
        System.out.println("data sent to server");

    }

    public void setUpdateUICallBack(UpdateUICallBack updateUICallBack) throws JsonProcessingException {
        this.updateUICallBack = updateUICallBack;

        while (!messageQueue.isEmpty()) {
            updateUICallBack.process(messageQueue.poll());
        }
    }

    public void handleMessageFromServer(String msg) throws JsonProcessingException {
        if(updateUICallBack != null){
            updateUICallBack.process(msg);
        } else {
            System.err.println("UiCallback not set yet. Queuing: " + msg);
            messageQueue.add(msg);
        }
    }

    public void handleServerDisconnect(){
        updateUICallBack.disconnect();
    }
}
