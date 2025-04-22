package it.polimi.galaxy_trucker.Server.Model;

import it.polimi.galaxy_trucker.Client.ClientMain;
import it.polimi.galaxy_trucker.Client.LanguageManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class SceneSwitch {
    public static void switchScene(AnchorPane container, String fxmlFile) throws IOException {
        URL resource = ClientMain.class.getResource(fxmlFile);

        if(resource == null) throw new IOException("FXML file not found: " + fxmlFile);

        ResourceBundle bundle = LanguageManager.getBundle();
        FXMLLoader loader = new FXMLLoader(resource);
        loader.setResources(bundle);
        AnchorPane nextAnchorPane = loader.load();

        container.getChildren().setAll(nextAnchorPane);
    }
}
