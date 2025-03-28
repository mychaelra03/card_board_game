package it.polimi.galaxy_trucker;

import it.polimi.galaxy_trucker.Client.ClientMain;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class TestLoginMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("View/login-view.fxml"));
        primaryStage.setTitle("Welcome to the Game");
        primaryStage.setScene(new Scene(fxmlLoader.load(), 500, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}