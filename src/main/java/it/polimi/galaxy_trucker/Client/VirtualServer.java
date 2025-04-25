package it.polimi.galaxy_trucker.Client;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.galaxy_trucker.Client.Network.MessageSender;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.net.InetAddress;

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

    public void attemptConnection(String hostName, int portNumber) throws IOException {

        InetAddress address = InetAddress.getByName(hostName);
        if(!address.isReachable(500)) throw new IOException("Host is not reachable: " + hostName);

        Socket socket = new Socket(hostName, portNumber);

        try{
            out = new PrintWriter(socket.getOutputStream(),true);
            in = new DataInputStream(socket.getInputStream());
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

    public void handleMessageFromServer(String msg) {
        UpdateUICallBack callBack;
        synchronized(this) {
            callBack = updateUICallBack;
            if (callBack == null) {
                messageQueue.add(msg);
            }
        }
        if (callBack != null) {
            dispatchToUI(callBack, msg);
        }
    }

    public synchronized void setUpdateUICallBack(UpdateUICallBack newCallBack) {
        this.updateUICallBack = newCallBack;
        if (newCallBack != null) {
            while (!messageQueue.isEmpty()) {
                String queued = messageQueue.poll();
                dispatchToUI(newCallBack, queued);
            }
        }
    }

    private void dispatchToUI(UpdateUICallBack cb, String msg) {
        try {
            cb.process(msg);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void handleServerDisconnect(){
        updateUICallBack.disconnect();
    }
}
