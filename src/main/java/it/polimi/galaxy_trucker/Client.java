package it.polimi.galaxy_trucker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class Client extends Application {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;

    private static final int PORT = 8080;

    public Client() {
        try{
            this.socket = new Socket("localhost", PORT);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        } catch(IOException e) {
            closeEverything(socket,in,out);
        }
    }

    public Client(String username, Socket socket) {
        try{
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        }catch(IOException e){
            closeEverything(socket,in,out);
        }
    }


    public void sendPlacedTileData(Integer col, Integer row, String id, int rot) {
        try {
            out.write(col+" "+row+" "+id+" "+rot);
            out.newLine();
            out.flush();
            System.out.println("data sent to server");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }




    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("View/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1400, 800);
        stage.setTitle("Galaxy Trucker");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) throws IOException {
        launch();
    }
}
