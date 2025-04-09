package it.polimi.galaxy_trucker.Client;

import it.polimi.galaxy_trucker.Client.Controller.Scene1.Scene1Controller;
import it.polimi.galaxy_trucker.GameLogic;
import it.polimi.galaxy_trucker.Server.Model.SceneSwitch;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;


public class ClientMain extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        SceneSwitch.setStage(stage);

        SceneSwitch.switchScene("/it/polimi/galaxy_trucker/View/hello-view.fxml");

        stage.setTitle("Galaxy Trucker");
        stage.setResizable(false);
        stage.show();

        virtualServer.start();
    }

    public static void main(String[] args) throws IOException {
        launch();
    }

    VirtualServer virtualServer;
    public ClientMain(){
        this.virtualServer = VirtualServer.getInstance();
    }
}
