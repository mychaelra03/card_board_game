package it.polimi.galaxy_trucker.Client;

import it.polimi.galaxy_trucker.Server.Model.SceneSwitch;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.*;


public class ClientMain extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        SceneSwitch.setStage(stage);

        SceneSwitch.switchScene("login_scene.fxml");

        stage.setTitle("Galaxy Trucker");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) throws IOException {
        launch();
    }
}
