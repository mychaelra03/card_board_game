package it.polimi.galaxy_trucker.Client;

import java.io.BufferedReader;
import java.io.IOException;

public class ClientThread extends Thread {
    private BufferedReader reader;

    public ClientThread(BufferedReader reader) {
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
