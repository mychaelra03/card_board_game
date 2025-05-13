package it.polimi.galaxy_trucker.client.popups;

import it.polimi.galaxy_trucker.client.LanguageManager;
import it.polimi.galaxy_trucker.common.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ConfirmationPopupController implements PopupAware {

    @FXML private Label prompt;

    private Runnable onConfirmAction = null;

    private Popup popup;

    @Override
    public void bindPopup(Popup popup) {
        this.popup = popup;
    }

    @FXML
    public void onClose() {
        if (popup != null) popup.playClosingAnimation();
    }
    @FXML
    public void onConfirm() {
        if (popup != null) popup.playClosingAnimation();
        if (onConfirmAction != null) onConfirmAction.run();
    }

    @Override
    public void initialize(Object... args) {
        if(args == null || args.length == 0) {
            Logger.logWarning("No arguments provided");
            return;
        }

        if(args[0] instanceof String promptText) {
            prompt.setText(LanguageManager.get(promptText.trim()));
        }

        if (args.length > 1 && args[1] instanceof Runnable action) {
            this.onConfirmAction = action;
        }
    }
}
