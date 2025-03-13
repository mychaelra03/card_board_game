package it.polimi.galaxy_trucker;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.Model.PlayerUsername;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class HelloApplication extends Application {
  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("View/hello-view.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
    stage.setTitle("Galaxy Trucker");
    stage.setScene(scene);
    stage.setResizable(false);
    stage.show();
  }

  public static void main(String[] args) {
    launch();



  }
}
