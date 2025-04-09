package it.polimi.galaxy_trucker.Client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;

public class ClientThread extends Thread {
    private DataInputStream reader;

    public ClientThread(DataInputStream reader) {
        this.reader = reader;
    }

    public void run() {
        System.out.println("ClientThread started");

        while (true) {
            try{
                String answer = reader.readLine();
                System.out.println(answer);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
