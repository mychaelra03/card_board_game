package it.polimi.galaxy_trucker.client.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.client.*;
import it.polimi.galaxy_trucker.client.controllers.shipBuilding.AnimationHandler;
import it.polimi.galaxy_trucker.client.controllers.shipBuilding.TileManager;
import it.polimi.galaxy_trucker.client.popups.PendingPopup;
import it.polimi.galaxy_trucker.client.popups.Popup;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.BoardTypeMessage;
import it.polimi.galaxy_trucker.common.messages.GameResetMessage;
import it.polimi.galaxy_trucker.common.messages.GameStateMessage;
import it.polimi.galaxy_trucker.common.messages.flight.ShipBoardMessage;
import it.polimi.galaxy_trucker.common.messages.flight.TileInfo;
import it.polimi.galaxy_trucker.server.model.BoardType;
import it.polimi.galaxy_trucker.server.model.GameModel;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.*;


public class FlightController implements VirtualServerAware {

    private final VirtualServer virtualServer = VirtualServer.getInstance();

    @FXML private AnchorPane mainContentPane;

    @FXML private ImageView shipBoardImage;

    @FXML private Button settingsButton;
    @FXML private GridPane gridPane;

    public static AnimationHandler animationHandler;

    private final Map<Integer, ImageView> tileMap = new HashMap<>();
    private final Map<Integer, ImageView> cardsMap = new HashMap<>();
    private final Map<Integer, Button> doneButtonsMap = new HashMap<>();

    private final Map<Integer, ImageView> timerSlots = new HashMap<>();

    public void initialize() {

    }

    @Override
    public void updateCallBack() {
        virtualServer.setCallBack(cmd -> {
            ObjectMapper mapper = new ObjectMapper();
            String type = mapper.readTree(cmd).get("type").asText();
            switch (type) {

                case "gameState" -> {
                    GameStateMessage message = mapper.readValue(cmd, GameStateMessage.class);

                    if (message.getGameState() == GameModel.GameState.FLIGHT_PHASE) {
                        virtualServer.setCallBack(null);
                        Platform.runLater(() -> SceneSwitch.switchScene("FlightScene"));
                        return;
                    }
                }

                case "gameReset" -> {
                    GameResetMessage message = mapper.readValue(cmd, GameResetMessage.class);

                    virtualServer.setCallBack(null);
                    PendingPopup.set(Popup.newAlertPopup("title.player_left", "description.player_left", message.getPlayerName()));
                    Platform.runLater(() -> SceneSwitch.switchScene("LobbyScene"));
                    return;
                }
            }
            safeUpdateView(cmd);
        });
    }

    private void safeUpdateView(String cmd) {
        Platform.runLater(() -> {
            try {
                updateView(cmd);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void updateView(String cmd) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(cmd);

        String type = node.get("type").asText();

        switch (type) {

            case "boardType" -> {

                BoardTypeMessage boardTypeMessage = mapper.readValue(cmd, BoardTypeMessage.class);
                BoardType shipType = boardTypeMessage.getBoardType();

                updateSceneWithBoardType(shipType);

                //tileManager.initializeGridPane(shipType);
            }

            case "shipBoard" -> {

                ShipBoardMessage message = mapper.readValue(cmd, ShipBoardMessage.class);
                List<TileInfo> tiles = message.getTiles();

                String color = ClientSession.getPlayerInfo(message.getPlayerName()).getColor();
                GridPane shipGrid = getShipBoardPane(tiles, color, 80);
                mainContentPane.getChildren().add(shipGrid);
            }

            case "gameState" -> {

                GameStateMessage message = mapper.readValue(cmd, GameStateMessage.class);

                switch (message.getGameState()) {
                    case BUILDING_SHIPS -> mainContentPane.setDisable(false);

                    case WAITING -> {
                        mainContentPane.setDisable(true);
                        //tileManager.handlePreviousSelectedTile();
                        //String popupMessage = lastTimerWasFinal? "label.times_up" : "label.players_ready";
                        //Popup.newCountdownPopup(popupMessage, 5).show();
                    }

                    default -> throw new IllegalStateException("Invalid game state");
                }
            }

            default -> Logger.logError("Unrecognized command: " + type);
        }
    }

    private GridPane getShipBoardPane(List<TileInfo> tiles, String color, double tileSize) {
        GridPane shipGrid = new GridPane();
        for (int col = 0; col < 7; col++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPrefWidth(tileSize);
            cc.setMinWidth(tileSize);
            cc.setMaxWidth(tileSize);
            shipGrid.getColumnConstraints().add(cc);
        }
        for (int row = 0; row < 5; row++) {
            RowConstraints rc = new RowConstraints();
            rc.setPrefHeight(tileSize);
            rc.setMinHeight(tileSize);
            rc.setMaxHeight(tileSize);
            shipGrid.getRowConstraints().add(rc);
        }
        shipGrid.setHgap(2);
        shipGrid.setVgap(2);
        for (TileInfo tile : tiles) {
            Image img;
            if(tile.getId() < 0 || tile.getId() > 151) {
                img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/textures/tiles/tile_starter_cabin_" + color.toLowerCase() + ".png")));
            } else {
                img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/textures/tiles/tile_" + tile.getId() + ".png")));
            }
            ImageView view = new ImageView(img);

            view.setFitWidth(tileSize);
            view.setFitHeight(tileSize);
            view.setRotate(tile.getRotation() * 90);

            shipGrid.add(view, tile.getX(), tile.getY());
        }
        return shipGrid;
    }

    private void updateSceneWithBoardType(BoardType shipType) {
        String boardPath = switch (shipType) {
            case LEARNING, LEVEL_ONE -> "/textures/cardboards/shipboard_1.png";
            case LEVEL_TWO -> "/textures/cardboards/shipboard_2.png";
        };
        Image board = new Image(Objects.requireNonNull(getClass().getResourceAsStream(boardPath)));
        shipBoardImage.setImage(board);
        FadeTransition boardFade = new FadeTransition(Duration.millis(1000), shipBoardImage);
        boardFade.setToValue(1);
        boardFade.play();
        ScaleTransition boardScale = new ScaleTransition(Duration.millis(1000), shipBoardImage);
        boardScale.setFromX(1.2);
        boardScale.setFromY(1.2);
        boardScale.setToX(1);
        boardScale.setToY(1);
        boardScale.play();
    }

}