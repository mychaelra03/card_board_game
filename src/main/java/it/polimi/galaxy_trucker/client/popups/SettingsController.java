package it.polimi.galaxy_trucker.client.popups;

import it.polimi.galaxy_trucker.client.LanguageManager;
import it.polimi.galaxy_trucker.client.SceneSwitch;
import it.polimi.galaxy_trucker.client.VirtualServer;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SettingsController implements PopupAware {

    private Popup popup;

    @FXML private Label languageLabel;
    @FXML private Button languageButton;
    @FXML private Label languageNoteLabel;

    @FXML private Button mainMenuButton;
    @FXML private Button closeGameButton;

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
        VirtualServer virtualServer = VirtualServer.getInstance();
        virtualServer.close();
    }

    @FXML
    public void onCloseGamePressed() {
        Stage stage = (Stage) closeGameButton.getScene().getWindow();
        WindowEvent closeEvent = new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST);
        Event.fireEvent(stage, closeEvent);
    }

    @FXML
    public void onClose() {
        if (popup != null) {
            popup.playClosingAnimation();
        }
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
}
