package it.polimi.galaxy_trucker.client.controllers.shipBuilding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.server.model.BoardType;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.client.*;
import it.polimi.galaxy_trucker.client.popups.PendingPopup;
import it.polimi.galaxy_trucker.client.popups.Popup;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.*;
import it.polimi.galaxy_trucker.common.messages.shipBuilding.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.*;

import static it.polimi.galaxy_trucker.client.controllers.shipBuilding.TileManager.TILES_PATH;

public class ShipBuildingController implements VirtualServerAware {

    private final VirtualServer virtualServer = VirtualServer.getInstance();

    @FXML private AnchorPane mainContentPane;

    @FXML private ImageView shipBoardImage;

    @FXML private ImageView cardPile0;
    @FXML private ImageView cardPile1;
    @FXML private ImageView cardPile2;

    @FXML private StackPane reservedSlot0;
    @FXML private StackPane reservedSlot1;

    @FXML private Rectangle focusSlot;

    @FXML private Button settingsButton;
    @FXML private GridPane gridPane;
    @FXML private Button rightButton,leftButton;

    @FXML private Label timerLabel;
    @FXML private VBox timerLabels;
    @FXML private Button timerFlipButton;
    @FXML private VBox timerBox;

    @FXML private Label doneLabel;
    @FXML private Button done_1;
    @FXML private Button done_2;
    @FXML private Button done_3;
    @FXML private Button done_4;

    public static AnimationHandler animationHandler;

    private final Map<Integer, ImageView> tileMap = new HashMap<>();
    private final Map<Integer, ImageView> cardsMap = new HashMap<>();
    private final Map<Integer, Button> doneButtonsMap = new HashMap<>();

    private final Map<Integer, ImageView> timerSlots = new HashMap<>();

    private TileManager tileManager;

    private boolean lastTimerWasFinal = false;

    public void initialize() {

        animationHandler = new AnimationHandler(mainContentPane);
        tileManager = new TileManager(gridPane, focusSlot, reservedSlot0, reservedSlot1);

        cardsMap.put(0, cardPile0);
        cardsMap.put(1, cardPile1);
        cardsMap.put(2, cardPile2);

        Image backTexture = new Image(Objects.requireNonNull(getClass().getResource(TILES_PATH + "tile_back.png")).toExternalForm());

        // initialize covered tiles
        List<ImageView> tileList = new ArrayList<>();

        for(int id = 0; id < 152; id++) {
            ImageView tile = tileManager.generateTile(id, backTexture);

            tileList.add(tile);
            tileMap.put(id, tile);
        }

        Collections.shuffle(tileList);
        mainContentPane.getChildren().addAll(tileList);

        for(int i = 0; i < tileList.size(); i++) {
            ImageView tile = tileList.get(i);
            animationHandler.playTileReturn(tile, i * 15);
        }

        rightButton.setOnAction(this::onClickTurnTileRight);
        leftButton.setOnAction(this::onClickTurnTileLeft);

        focusSlot.setOnMouseClicked(_ -> tileManager.moveSelectedToFocusSlot());

        settingsButton.setOnAction(_ -> Popup.newSettingsPopup().show());

        reservedSlot0.setOnMouseClicked(event -> tileManager.onReservedSlotClick(event));
        reservedSlot1.setOnMouseClicked(event -> tileManager.onReservedSlotClick(event));

        timerFlipButton.setOnAction(_ -> virtualServer.sendMessage(new TimerFlippedMessage()));

        doneButtonsMap.put(1, done_1);
        doneButtonsMap.put(2, done_2);
        doneButtonsMap.put(3, done_3);
        doneButtonsMap.put(4, done_4);

        for(Integer index : doneButtonsMap.keySet()) {
            doneButtonsMap.get(index).setOnAction(_ -> Popup.newConfirmationPopup("prompt.done_building", () -> doneBuilding(index)).show());
        }

        Platform.runLater(() -> Popup.newCountdownPopup("label.game_starts_in", 5).show());
    }

    private void doneBuilding(int index) {
        tileManager.handlePreviousSelectedTile();
        virtualServer.sendMessage(new DoneBuildingMessage(null, index));
    }


    @Override
    public void updateCallBack() {
        virtualServer.setCallBack(cmd -> {
            ObjectMapper mapper = new ObjectMapper();
            String type = mapper.readTree(cmd).get("type").asText();
            switch (type) {

                case "gameState" -> {
                    GameStateMessage message = mapper.readValue(cmd, GameStateMessage.class);

                    if (message.getGameState() == GameModel.GameState.CORRECTING_SHIP) {
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

    ObjectMapper mapper = new ObjectMapper();
    private void updateView(String cmd) throws JsonProcessingException {

        JsonNode node = mapper.readTree(cmd);

        String type = node.get("type").asText();

        switch (type) {
            case "gameState"     -> handleGameState(mapper.readValue(cmd, GameStateMessage.class));
            case "boardType"     -> handleBoardType(mapper.readValue(cmd, BoardTypeMessage.class));
            case "tileTaken"     -> handleTileTaken(mapper.readValue(cmd, TileTakenMessage.class));
            case "tileReturned"  -> handleTileReturned(mapper.readValue(cmd, TileReturnedMessage.class));
            case "cardsTaken"    -> handleCardsTaken(mapper.readValue(cmd, CardsTakenMessage.class));
            case "cardsReturned" -> handleCardsReturned(mapper.readValue(cmd, CardsReturnedMessage.class));
            case "cards"         -> handleCards(mapper.readValue(cmd, CardsMessage.class));
            case "timerStart"    -> handleTimerStart(mapper.readValue(cmd, TimerStartMessage.class));
            case "timerFinished" -> handleTimerFinished(mapper.readValue(cmd, TimerFinishedMessage.class));
            case "doneBuilding"  -> handleDoneBuilding(mapper.readValue(cmd, DoneBuildingMessage.class));
            default              -> Logger.logError("Unrecognized command: " + type);
        }
    }

    private void handleGameState(GameStateMessage message) {

        switch (message.getGameState()) {
            case BUILDING_SHIPS -> mainContentPane.setDisable(false);

            case WAITING -> {
                mainContentPane.setDisable(true);
                tileManager.handlePreviousSelectedTile();
                String popupMessage = lastTimerWasFinal? "label.times_up" : "label.players_ready";
                Popup.newCountdownPopup(popupMessage, 5).show();
            }

            default -> throw new IllegalStateException("Invalid game state");
        }
    }

    private void handleBoardType(BoardTypeMessage message) {
        BoardType shipType = message.getBoardType();

        updateShipBoard(shipType);
        updateCardPiles(shipType);
        updateTimerIcons(shipType);

        if(ClientSession.getPlayerCount() < 3) done_3.setDisable(true);
        if(ClientSession.getPlayerCount() < 4) done_4.setDisable(true);

        tileManager.initializeGridPane(shipType);
    }

    private void handleTileTaken(TileTakenMessage message) {

        boolean isSelf = ClientSession.isSelf(message.getPlayerName());
        ImageView tile = tileMap.get(message.getTileId());

        if (isSelf) {
            tileManager.takeTile(tile);
        } else {
            animationHandler.playTileTaken(tile);
        }
    }

    private void handleTileReturned(TileReturnedMessage message) {

        int tileId = message.getTileId();
        ImageView tile = tileMap.get(tileId);

        tile.setImage(new Image(Objects.requireNonNull(getClass().getResource("/textures/tiles/tile_" + tileId + ".png")).toExternalForm()));

        animationHandler.playTileReturn(tile, 0);
    }

    private void handleCardsTaken(CardsTakenMessage message) {

        animationHandler.playCardsTaken(cardsMap.get(message.getPileId()), message.getPlayerName());
    }

    private void handleCardsReturned(CardsReturnedMessage message) {

        animationHandler.playCardsReturn(cardsMap.get(message.getPileId()), 200);
    }

    private void handleCards(CardsMessage message) {

        Popup.newCardsPopup(message.getLevelOneCards(), message.getLevelTwoCards()).show();
    }

    private void handleTimerStart(TimerStartMessage message) {

        timerFlipButton.setOpacity(0);
        timerLabels.setOpacity(1);
        timerFlipButton.setDisable(true);

        startCountdown(message.getDuration());

        int timesFlipped = message.getTimesFlipped();
        if(timerSlots.containsKey(timesFlipped - 1)) {
            timerSlots.get(timesFlipped - 1).setOpacity(0);
        }
        if(timerSlots.containsKey(timesFlipped)) {
            timerSlots.get(timesFlipped).setOpacity(1);
        }
    }

    private void handleTimerFinished(TimerFinishedMessage message) {
        lastTimerWasFinal = message.isFinalFlip();

        if(!lastTimerWasFinal) {
            timerLabels.setOpacity(0);
            timerFlipButton.setOpacity(1);
            timerFlipButton.setDisable(false);
        }
    }

    private void handleDoneBuilding(DoneBuildingMessage message) {

        boolean isSelf = message.getPlayerName().equals(ClientSession.getUsername());
        Button doneButton = doneButtonsMap.get(message.getPositionIndex());

        if (doneButton == null) {
            System.err.println("No button found for position: " + message.getPositionIndex());
            return;
        }

        if(isSelf) {
            tileMap.values().forEach(tile -> tile.setDisable(true));
            cardsMap.values().forEach(card -> card.setDisable(true));
            doneButtonsMap.values().forEach(button -> button.setDisable(true));
            doneLabel.setText(LanguageManager.get("label.waiting_other_players"));
            tileManager.hideValidCells();
        }

        doneButton.setDisable(true);
        doneButton.setText("");
        if(doneButton.getParent() instanceof StackPane pane) {
            StackPane profilePic = ClientSession.getPlayerProfilePicture(message.getPlayerName(), 40);
            pane.getChildren().add(profilePic);
        }
    }

    private void updateShipBoard(BoardType shipType) {
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

    private void updateCardPiles(BoardType shipType) {
        String pilePath = switch (shipType) {
            case LEARNING, LEVEL_ONE -> "/textures/cards/card_pile_level_1.png";
            case LEVEL_TWO -> "/textures/cards/card_pile_level_2.png";
        };
        Image pile = new Image(Objects.requireNonNull(getClass().getResourceAsStream(pilePath)));
        cardsMap.forEach((id, card) -> {
            card.setImage(pile);
            animationHandler.playCardsReturn(card, 500 + id * 150);
        });
    }

    private void updateTimerIcons(BoardType shipType) {
        int num = shipType.getBuildingTimersCount();
        for(int i = 0; i < num; i++) {
            String iconPath = getTimerSlotIconPath(shipType, i, num);
            ImageView iconSlot = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath))));
            iconSlot.setFitWidth(25);
            iconSlot.setFitHeight(25);
            iconSlot.setPreserveRatio(true);

            ImageView timerIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/textures/icons/hourglass.png"))));
            timerIcon.setFitWidth(25);
            timerIcon.setFitHeight(25);
            timerIcon.setPreserveRatio(true);
            timerIcon.setOpacity(0);

            StackPane timerPane = new StackPane(iconSlot, timerIcon);
            timerPane.setPrefSize(25, 25);
            timerPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            timerPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

            timerSlots.put(i + 1, timerIcon);
            timerBox.getChildren().add(timerPane);
        }
    }

    private static String getTimerSlotIconPath(BoardType shipType, int i, int num) {
        String iconPath;
        if(i < num - 1) {
            iconPath = switch (shipType) {
                case LEARNING -> null;
                case LEVEL_ONE -> "/textures/icons/slot_1.png";
                case LEVEL_TWO -> "/textures/icons/slot_2.png";
            };
        } else {
            iconPath = switch (shipType) {
                case LEARNING -> null;
                case LEVEL_ONE -> "/textures/icons/slot_1_S.png";
                case LEVEL_TWO -> "/textures/icons/slot_2_S.png";
            };
        }
        return iconPath;
    }

    public void startCountdown(int durationSeconds) {
        Timeline timeline = new Timeline();
        IntegerProperty timeSeconds = new SimpleIntegerProperty(durationSeconds);

        timerLabel.textProperty().bind(timeSeconds.asString());

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), _ -> {
            int current = timeSeconds.get();
            if (current > 0) {
                timeSeconds.set(current - 1);
            } else {
                timeline.stop();
            }
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(durationSeconds);
        timeline.playFromStart();
    }

    @FXML
    public void onCardPileClick(Event event) {
        tileManager.handlePreviousSelectedTile();

        tileManager.hideValidCells();
        String pileId = ((Node) event.getSource()).getId();
        virtualServer.sendMessage(new CardsTakenMessage(null, Integer.parseInt(pileId)));
    }

    @FXML
    public void onClickTurnTileRight(Event event) {
        tileManager.rotateSelectedTile(90);
    }

    @FXML
    public void onClickTurnTileLeft(Event event) {
        tileManager.rotateSelectedTile(-90);
    }
}