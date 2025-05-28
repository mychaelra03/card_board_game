package it.polimi.galaxy_trucker.client.popups.cards;

import it.polimi.galaxy_trucker.client.VirtualServer;
import it.polimi.galaxy_trucker.client.popups.Popup;
import it.polimi.galaxy_trucker.client.popups.PopupAware;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.flight.cardActions.CardDecisionMessage;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DecisionPopupController implements PopupAware {

    private Popup popup;
    private static boolean isPopupVisible = false;

    VirtualServer virtualServer = VirtualServer.getInstance();

    @FXML private ImageView cardImageView;

    @Override
    public void bindPopup(Popup popup) {
        this.popup = popup;
    }

    @FXML
    public void onClose() {}

    @Override
    public void initialize(Object... args) {

        if (args == null || args.length == 0) {
            Logger.logWarning("No arguments provided");
            return;
        }

        if (args[0] instanceof Image cardImage) {
            cardImageView.setImage(cardImage);
        }
    }

    @FXML
    public void onAccept() {
        virtualServer.sendMessage(new CardDecisionMessage(null, true));
        popup.playClosingAnimation();
    }

    @FXML
    public void onDecline() {
        virtualServer.sendMessage(new CardDecisionMessage(null, false));
        popup.playClosingAnimation();
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
