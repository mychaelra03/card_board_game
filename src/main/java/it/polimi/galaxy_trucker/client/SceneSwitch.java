package it.polimi.galaxy_trucker.client;

import it.polimi.galaxy_trucker.common.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.net.URL;
import java.io.IOException;
import java.util.ResourceBundle;

public class SceneSwitch {
    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxmlFile) {
        URL resource = SceneSwitch.class.getResource("/it/polimi/galaxy_trucker/scenes/" + fxmlFile + ".fxml");

        Parent root;
        try {
            if (resource == null) throw new IOException("FXML file not found: " + fxmlFile + ".fxml");

            LanguageManager.applyPendingLocale();
            ResourceBundle bundle = LanguageManager.getBundle();

            FXMLLoader fxmlLoader = new FXMLLoader(resource, bundle);
            root = fxmlLoader.load();

            Object controller = fxmlLoader.getController();
            if (controller instanceof VirtualServerAware aware) {
                aware.updateCallBack();
            }
        } catch (IOException e) {
            Logger.logError("Error while switching scenes: " + e.getMessage());
            return;
        }

        Scene newScene = new Scene(root);
        primaryStage.setScene(newScene);

        if (!primaryStage.isShowing()) primaryStage.show();
    }

//    public static void closeScene(Node someNodeInTheScene) {
//        Stage stage = (Stage) someNodeInTheScene.getScene().getWindow();
//        stage.close();
//    }
}