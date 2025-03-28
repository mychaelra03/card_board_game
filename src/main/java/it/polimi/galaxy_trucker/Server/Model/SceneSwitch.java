package it.polimi.galaxy_trucker.Server.Model;

import it.polimi.galaxy_trucker.Client.ClientMain;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;

public class SceneSwitch {
    public static void switchScene(AnchorPane container, String fxmlFile) throws IOException {
        URL resource = ClientMain.class.getResource(fxmlFile);
        if (resource == null) {
            throw new IOException("FXML file not found: " + fxmlFile);
        }

        AnchorPane nextAnchorPane = FXMLLoader.load(resource);
        container.getChildren().clear(); // Clear previous content
        container.getChildren().add(nextAnchorPane);
    }
}