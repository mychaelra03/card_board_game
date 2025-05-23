package it.polimi.galaxy_trucker.client;

import it.polimi.galaxy_trucker.client.popups.Popup;
import it.polimi.galaxy_trucker.common.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.Objects;

public class ClientMain extends Application {

    @Override
    public void start(Stage stage) {

        Font.loadFont(getClass().getResourceAsStream("/fonts/DynaPuff-Regular.ttf"), 12);
        Font.loadFont(getClass().getResourceAsStream("/fonts/DynaPuff-Bold.ttf"), 12);

        SceneSwitch.setStage(stage);

        stage.setTitle("Galaxy Trucker");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/textures/icons/mainIcon.png"))));
        stage.setResizable(false);

        SceneSwitch.switchScene("LoginScene");

        stage.setOnCloseRequest(event -> {
            event.consume();
            Popup.newConfirmationPopup("prompt.close_game", () -> {
                Logger.logInfo("Closing game...");
                VirtualServer.getInstance().close();
                Platform.exit();
                System.exit(0);
            }).show();
        });

        stage.show();
    }

    public static void main(String[] args) {

        Logger.logInfo("Starting Galaxy Trucker...");

        Locale selectedLocale = Locale.ENGLISH;
        boolean tui = false;
        boolean debugMode = false;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-l", "--lang" -> {
                    if (i + 1 < args.length) {
                        String langCode = args[++i].toLowerCase();
                        switch (langCode) {
                            case "en":
                                selectedLocale = Locale.ENGLISH;
                                break;
                            case "it":
                                selectedLocale = Locale.ITALIAN;
                                break;
                            default:
                                Logger.logWarning("Unsupported language: " + langCode + ". Defaulting to English.");
                        }
                    } else {
                        Logger.logWarning("Missing language code after --lang. Defaulting to English.");
                    }
                }

                case "-t", "-tui" -> tui = true;

                case "-d", "--debug" -> debugMode = true;

                default -> Logger.logWarning("Unknown argument: " + args[i]);
            }
        }
        // Apply selected settings
        LanguageManager.setPendingLocale(selectedLocale);
        if(tui) Logger.logWarning("TUI is still WIP.");
        Logger.setDebugMode(debugMode);

        launch();
    }
}
