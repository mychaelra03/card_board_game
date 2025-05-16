package it.polimi.galaxy_trucker.client.popups;

import it.polimi.galaxy_trucker.client.LanguageManager;
import it.polimi.galaxy_trucker.common.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.text.MessageFormat;

public class AlertPopupController implements PopupAware {

    @FXML private Label title;
    @FXML private Label description;

    private Popup popup;
    private static boolean isPopupVisible = false;

    @Override
    public void bindPopup(Popup popup) {
        this.popup = popup;
    }

    @FXML
    public void onClose() {
        if (popup != null) popup.playClosingAnimation();
    }

    @Override
    public void initialize(Object... args) {
        if(args == null || args.length == 0) {
            Logger.logWarning("No arguments provided");
            return;
        }

        if(args[0] instanceof String titleText) {
            title.setText(LanguageManager.get(titleText.trim()));
        }
        if(args.length > 1 && args[1] instanceof String descriptionText) {
            if(args.length > 2 && args[2] instanceof String descriptionArgs) {
                description.setText(MessageFormat.format(LanguageManager.get(descriptionText), descriptionArgs));
            } else {
                description.setText(LanguageManager.get(descriptionText.trim()));
            }
        }
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
