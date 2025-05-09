package it.polimi.galaxy_trucker.client.popups;

import it.polimi.galaxy_trucker.client.LanguageManager;
import it.polimi.galaxy_trucker.client.VirtualServer;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.shipBuilding.CardsReturnedMessage;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.List;
import java.util.Objects;

public class CountdownPopupController implements PopupAware {

    @FXML private Label countdownLabel;

    private Popup popup;

    private AnchorPane mainContentPane;
    private int secondsLeft = 10;

    @Override
    public void bindPopup(Popup popup) {
        this.popup = popup;
    }

    @FXML
    public void onClose() {}

    @Override
    public void initialize(Object... args) {
        if(args != null && args.length > 0 && args[0] instanceof Integer duration) {
            secondsLeft = duration;
        }

        startCountdown();
    }

    public void startCountdown() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, _ -> updateCountdown()),
                new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(secondsLeft);
        timeline.setOnFinished(_ -> popup.playClosingAnimation());
        timeline.play();
    }

    private void updateCountdown() {
        countdownLabel.setText(String.valueOf(secondsLeft));

        ScaleTransition st = new ScaleTransition(Duration.millis(150), countdownLabel);
        st.setFromX(1.3);
        st.setFromY(1.3);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();

        secondsLeft--;
    }
}
