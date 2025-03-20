package it.polimi.galaxy_trucker.Model;

import it.polimi.galaxy_trucker.Client;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;

public class SceneSwitch {
    public static void SceneSwitch(AnchorPane container, String fxmlFile) throws IOException {
        AnchorPane nextAnchorPane = FXMLLoader.load(Objects.requireNonNull(Client.class.getResource(fxmlFile)));
        container.getChildren().removeAll();
        container.getChildren().setAll(nextAnchorPane);
    }
}
