package it.polimi.galaxy_trucker.client.controllers.playBuilding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.client.*;
import it.polimi.galaxy_trucker.client.controllers.shipBuilding.AnimationHandler;
import it.polimi.galaxy_trucker.client.popups.Popup;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.*;
import it.polimi.galaxy_trucker.common.messages.shipBuilding.*;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class PlayController {
    private final VirtualServer virtualServer = VirtualServer.getInstance();
    public static AnimationHandler animationHandler;
    @FXML private ImageView boardImage;
    @FXML private ImageView player1Image;
    @FXML private ImageView player2Image;
    @FXML private ImageView player3Image;

    @FXML private ImageView pileCards;
    @FXML private ImageView card;

    @FXML private Button settingsButton;

    public void initialize() {

    }
}
