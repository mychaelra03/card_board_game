package it.polimi.galaxy_trucker.client.popups;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class PendingPopup {

    private static Popup popup;

    public static void set(Popup popup) {
        PendingPopup.popup = popup;
    }

    public static void tryShow(StackPane rootPane, AnchorPane mainContentPane) {
        if (popup != null) {
            popup.show(rootPane, mainContentPane);
            popup = null;
        }
    }
}