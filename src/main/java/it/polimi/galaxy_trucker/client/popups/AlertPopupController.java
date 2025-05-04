package it.polimi.galaxy_trucker.client.popups;

import it.polimi.galaxy_trucker.client.LanguageManager;
import it.polimi.galaxy_trucker.common.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class AlertPopupController implements PopupAware {

    @FXML private Label title;
    @FXML private Label description;
    @FXML private Button button;

    private Popup popup;

    @Override
    public void setPopup(Popup popup) {
        this.popup = popup;
    }

    @FXML
    public void onClose() {
        if (popup != null) {
            popup.close();
        }
    }

    @Override
    public void initializeWithArguments(Object... args) {
        if(args == null || args.length == 0) {
            Logger.logWarning("No arguments provided");
            return;
        }

        if(args[0] instanceof String arg) {
            title.setText(LanguageManager.get(arg.trim()));
        }
        if(args.length > 1 && args[1] instanceof String arg) {
            description.setText(LanguageManager.get(arg.trim()));
        }
        if(args.length > 2 && args[2] instanceof String arg) {
            button.setText(LanguageManager.get(arg.trim()));
        }
    }
}
