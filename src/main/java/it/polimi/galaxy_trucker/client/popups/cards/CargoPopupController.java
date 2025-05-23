package it.polimi.galaxy_trucker.client.popups.cards;

import it.polimi.galaxy_trucker.client.VirtualServer;
import it.polimi.galaxy_trucker.client.popups.Popup;
import it.polimi.galaxy_trucker.client.popups.PopupAware;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.flight.cardActions.CargoDoneMessage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CargoPopupController implements PopupAware {

    private Popup popup;
    private static boolean isPopupVisible = false;

    VirtualServer virtualServer = VirtualServer.getInstance();

    @FXML private StackPane shipPane;
    @FXML private StackPane blockPoolPane;

    private StackPane ship;
    private VBox originalShipPane;

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

        if (args[0] instanceof StackPane incomingShip) {
            this.ship = incomingShip;
            shipPane.getChildren().add(incomingShip);
        }
        if(args.length > 1 && args[1] instanceof VBox originalPane) {
            this.originalShipPane = originalPane;
        }
        if(args.length > 2 && args[2] instanceof VBox blockPool) {
            this.blockPoolPane.getChildren().add(blockPool);
        }
    }

    @FXML
    public void onDone() {
        virtualServer.sendMessage(new CargoDoneMessage());
        originalShipPane.getChildren().add(ship);
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
