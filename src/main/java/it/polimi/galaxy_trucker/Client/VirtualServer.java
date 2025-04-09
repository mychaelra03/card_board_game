package it.polimi.galaxy_trucker.Client;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class VirtualServer {
    DataInputStream in = null;
    PrintWriter out = null;

    private static VirtualServer instance;

private VirtualServer() {

}
public static VirtualServer getInstance() {
    if(instance == null){
        instance = new VirtualServer();
    }
    return instance;
}


    void start(){
        String hostName = "localhost";
        int portNumber = 8080;

        Socket socket = null;
        try{
            socket = new Socket(hostName,portNumber);

        } catch (IOException e) {
           System.err.println(e.toString() + " " + hostName);
           System.exit(1);
        }


        BufferedReader stdIn = null;
        try{
            out = new PrintWriter(socket.getOutputStream(),true);
            in = new DataInputStream(socket.getInputStream());
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        }catch (IOException e) {
            System.err.println(e.toString() + " " + hostName);
            System.exit(1);
        }

        this.clientThread = new ClientThread(in);
        clientThread.start();

    }

    public void sendCmd(String cmd){
        System.out.println(cmd);
        out.println(cmd);
    }


    public void sendPlacedTileData(Integer col, Integer row, String id, int rot) {

        out.write(col);
        out.write(row);
        out.write(id);
        out.write(rot);
        out.flush();
        System.out.println("data sent to server");

    }


    ClientThread clientThread;
    private UpdateUICallBack updateUICallBack;

    void setUpdateUICallBack(UpdateUICallBack updateUICallBack) {
        this.updateUICallBack = updateUICallBack;
    }



}
