package it.polimi.galaxy_trucker.client;

import it.polimi.galaxy_trucker.common.Logger;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.TimeZone;


public class ClientMain extends Application {

    @Override
    public void start(Stage stage) {

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
