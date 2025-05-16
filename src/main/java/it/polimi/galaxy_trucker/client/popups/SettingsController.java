package it.polimi.galaxy_trucker.client.popups;

import it.polimi.galaxy_trucker.client.LanguageManager;
import it.polimi.galaxy_trucker.client.SceneSwitch;
import it.polimi.galaxy_trucker.client.VirtualServer;
import it.polimi.galaxy_trucker.common.Logger;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SettingsController implements PopupAware {

    private Popup popup;
    private static boolean isPopupVisible = false;

    @FXML private Label languageLabel;
    @FXML private Button languageButton;
    @FXML private Label languageNoteLabel;

    @FXML private Button mainMenuButton;

    private boolean isMainMenu = false;

    @Override
    public void bindPopup(Popup popup) {
        this.popup = popup;
    }

    @FXML
    public void initialize() {
        updateTexts();
    }

    @FXML
    public void onLanguageChange() {
        LanguageManager.cycleLanguages();
        updateTexts();
    }

    @FXML
    public void onMainMenuPressed() {
        Popup.newConfirmationPopup("prompt.main_menu", () -> {
            Logger.logInfo("Returning to main menu...");
            VirtualServer.getInstance().close();
        }).show();
    }

    @FXML
    public void onCloseGamePressed() {
        Popup.newConfirmationPopup("prompt.close_game", () -> {
            Logger.logInfo("Closing game...");
            VirtualServer.getInstance().close();
            Platform.exit();
            System.exit(0);
        }).show();
    }

    @FXML
    public void onClose() {
        if (popup != null) popup.playClosingAnimation();
    }

    @Override
    public void initialize(Object... args) {
        if(args == null || args.length == 0) return;

        if(args[0] instanceof Boolean arg) {
            isMainMenu = arg;
        }

        updateTexts();
    }

    private void updateTexts() {
        languageLabel.setText(LanguageManager.get("label.language") + ": ");

        String langKey = LanguageManager.getPendingLocale().toLanguageTag().toLowerCase();
        boolean isCurrent = LanguageManager.getLocale().equals(LanguageManager.getPendingLocale());

        languageButton.setText(LanguageManager.get("button.language_" + langKey));
        languageButton.setStyle("-fx-background-color:" + (isCurrent ? "gold" : "orange"));

        languageNoteLabel.setText(isCurrent ? "" : LanguageManager.get("label.language_note"));

        mainMenuButton.setDisable(isMainMenu);
    }

    @Override
    public void setPopupVisible(boolean visible) {
        isPopupVisible = visible;
    }

    @Override
    public boolean isPopupVisible() {
        return isPopupVisible;
    }
}
