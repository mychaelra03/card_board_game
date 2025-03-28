package it.polimi.galaxy_trucker.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class VirtualServer {
    PrintWriter out = null;
    BufferedReader in = null;

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
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        }catch (IOException e) {
            System.err.println(e.toString() + " " + hostName);
            System.exit(1);
        }

        ClientThread clientThread = new ClientThread(in);
        clientThread.start();

    }

    public void sendCmd(String cmd){
        out.println(cmd);

    }
}
