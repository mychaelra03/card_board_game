package it.polimi.galaxy_trucker.Server.Model;

import it.polimi.galaxy_trucker.Client.VirtualServer;
import it.polimi.galaxy_trucker.Client.VirtualServerAware;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.util.Objects;
import java.io.IOException;

public class SceneSwitch {
    private static Stage primaryStage;
    private static VirtualServer virtualServer;

    public static void setVirtualServer(VirtualServer vs) {
        virtualServer = vs;
    }

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene( String fxmlFile ) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(SceneSwitch.class.getResource(fxmlFile)));
        Parent root = fxmlLoader.load();

        Object controller = fxmlLoader.getController();
        if(controller instanceof VirtualServerAware aware) {
            aware.setVirtualServer(VirtualServer.getInstance());
        }

        Scene newScene = new Scene(root);
        primaryStage.setScene(newScene);
        primaryStage.show();

    }
}