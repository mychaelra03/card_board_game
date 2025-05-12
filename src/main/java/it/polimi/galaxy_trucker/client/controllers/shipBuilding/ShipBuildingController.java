package it.polimi.galaxy_trucker.client.controllers.shipBuilding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.BoardType;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.client.*;
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

import java.util.*;

import static it.polimi.galaxy_trucker.client.controllers.shipBuilding.TileManager.TILES_PATH;

public class ShipBuildingController implements VirtualServerAware {

    private final VirtualServer virtualServer = VirtualServer.getInstance();


    @FXML private StackPane rootPane;
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

    public static AnimationHandler animationHandler;

    private final Map<Integer, ImageView> tileMap = new HashMap<>();
    private final Map<Integer, ImageView> cardsMap = new HashMap<>();

    private TileManager tileManager;

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

        settingsButton.setOnAction(_ -> Popup.newSettingsPopup().show(rootPane, mainContentPane));

        reservedSlot0.setOnMouseClicked(event -> tileManager.onReservedSlotClick(event));
        reservedSlot1.setOnMouseClicked(event -> tileManager.onReservedSlotClick(event));

        Platform.runLater(() -> Popup.newCountdownPopup(5).show(rootPane, mainContentPane));
    }


    @Override
    public void updateCallBack() {
        virtualServer.setCallBack(cmd -> {
            ObjectMapper mapper = new ObjectMapper();
            String type = mapper.readTree(cmd).get("type").asText();
            switch (type) {

                case "gameState" -> {
                    GameStateMessage gameStateMessage = mapper.readValue(cmd, GameStateMessage.class);

                    if (gameStateMessage.getGameState() != GameModel.GameState.FLIGHT_PHASE) throw new IllegalStateException("Invalid game state");
                    Platform.runLater(() -> SceneSwitch.switchScene("ApplyingCardsScene"));
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

                String pilePath = switch (shipType) {
                    case LEARNING, LEVEL_ONE -> "/textures/cards/card_pile_level_1.png";
                    case LEVEL_TWO -> "/textures/cards/card_pile_level_2.png";
                };
                Image pile = new Image(Objects.requireNonNull(getClass().getResourceAsStream(pilePath)));
                cardsMap.forEach((id, card) -> {
                    card.setImage(pile);
                    animationHandler.playCardsReturn(card, 500 + id * 150);
                });

                tileManager.initializeGridPane(shipType);
            }

            case "tileTaken" -> {

                TileTakenMessage message = mapper.readValue(cmd, TileTakenMessage.class);

                boolean isSelf = message.getPlayerName().equals(ClientSession.getUsername());
                ImageView tile = tileMap.get(message.getTileId());

                if (isSelf) {
                    tileManager.takeTile(tile);
                } else {
                    animationHandler.playTileTaken(tile);
                }
            }

            case "tileReturned" -> {

                TileReturnedMessage message = mapper.readValue(cmd, TileReturnedMessage.class);

                int tileId = message.getTileId();
                ImageView tile = tileMap.get(tileId);

                tile.setImage(new Image(Objects.requireNonNull(getClass().getResource("/textures/tiles/tile_" + tileId + ".png")).toExternalForm()));

                animationHandler.playTileReturn(tile, 0);
            }

            case "cardsTaken" -> {

                CardsTakenMessage message = mapper.readValue(cmd, CardsTakenMessage.class);

                animationHandler.playCardsTaken(cardsMap.get(message.getPileId()), message.getPlayerName());
            }

            case "cardsReturned" -> {

                CardsReturnedMessage message = mapper.readValue(cmd, CardsReturnedMessage.class);

                animationHandler.playCardsReturn(cardsMap.get(message.getPileId()), 200);
            }

            case "cards" -> {

                CardsMessage message = mapper.readValue(cmd, CardsMessage.class);

                Popup.newCardsPopup(message.getLevelOneCards(), message.getLevelTwoCards()).show(rootPane, mainContentPane);
            }

            default -> Logger.logError("Unrecognized command: " + type);
        }
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