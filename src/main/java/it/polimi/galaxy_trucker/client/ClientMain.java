package it.polimi.galaxy_trucker.client;

import it.polimi.galaxy_trucker.common.Logger;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Objects;

public class ClientMain extends Application {

    @Override
    public void start(Stage stage) {

        Font.loadFont(getClass().getResourceAsStream("/fonts/Rajdhani-Regular.ttf"), 12);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Rajdhani-Bold.ttf"), 12);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Orbitron-Regular.ttf"), 12);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Orbitron-Bold.ttf"), 12);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Mr Headlines Fancy.ttf"), 12);
        Font.loadFont(getClass().getResourceAsStream("/fonts/chango one.otf"), 12);
        Font.loadFont(getClass().getResourceAsStream("/fonts/SourGummy-Regular.ttf"), 12);
        Font.loadFont(getClass().getResourceAsStream("/fonts/SourGummy-Bold.ttf"), 12);
        Font.loadFont(getClass().getResourceAsStream("/fonts/DynaPuff-Regular.ttf"), 12);
        Font.loadFont(getClass().getResourceAsStream("/fonts/DynaPuff-Bold.ttf"), 12);

        SceneSwitch.setStage(stage);

        stage.setTitle("Galaxy Trucker");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/textures/icons/mainIcon.png"))));
        stage.setResizable(false);

        SceneSwitch.switchScene("LoginScene");

        stage.setOnCloseRequest(_ -> {
            Logger.logInfo("Window is closing...");
            VirtualServer.getInstance().close();
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
