package it.polimi.galaxy_trucker.Server.Model;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.galaxy_trucker.Client.LanguageManager;
import it.polimi.galaxy_trucker.Client.VirtualServer;
import it.polimi.galaxy_trucker.Client.VirtualServerAware;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.net.URL;
import java.util.Objects;
import java.io.IOException;
import java.util.ResourceBundle;

public class SceneSwitch {
    private static Stage primaryStage;
    private static VirtualServer virtualServer;

    public static void setVirtualServer(VirtualServer vs) {
        virtualServer = vs;
    }

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxmlFile) {
        URL resource = SceneSwitch.class.getResource(fxmlFile);

        Parent root;
        try {
            if (resource == null) throw new IOException("FXML file not found: " + fxmlFile);

            ResourceBundle bundle = LanguageManager.getBundle();

            FXMLLoader fxmlLoader = new FXMLLoader(resource, bundle);
            root = fxmlLoader.load();

            Object controller = fxmlLoader.getController();
            if (controller instanceof VirtualServerAware aware) {
                aware.setVirtualServer(VirtualServer.getInstance());
            }
        } catch (IOException e) {
            System.err.println("Error while switching scenes: " + e.getMessage());
            return;
        }

        Scene newScene = new Scene(root);
        primaryStage.setScene(newScene);
        primaryStage.show();
    }

    public static void closeScene(Node someNodeInTheScene) {
        Stage stage = (Stage) someNodeInTheScene.getScene().getWindow();
        stage.close();
    }
}