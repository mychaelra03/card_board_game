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
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.*;

public class ShipBuildingController implements VirtualServerAware {

    public static final String TILE_PATH = "/textures/tiles/";
    public static final double TILE_SIZE = 83.5;

    protected static ImageView selectedTile = null;

    protected static Set<StackPane> validCells = new HashSet<>();

    private final VirtualServer virtualServer = VirtualServer.getInstance();


    @FXML private StackPane rootPane;
    @FXML private AnchorPane mainContentPane;

    @FXML private ImageView shipBoardImage;

    @FXML private ImageView cardPile0;
    @FXML private ImageView cardPile1;
    @FXML private ImageView cardPile2;

    @FXML private Rectangle selectedTileSlot;

    @FXML private Button settingsButton;
    @FXML private GridPane gridPane;
    @FXML private Button rightButton,leftButton;

    public static AnimationHandler animationHandler;

    private final Map<Integer, ImageView> tileMap = new HashMap<>();
    private final Map<Integer, ImageView> cardsMap = new HashMap<>();

    private TileManager tileManager;

    public void initialize() {

        animationHandler = new AnimationHandler(mainContentPane);
        tileManager = new TileManager(mainContentPane, gridPane, selectedTileSlot);

        cardsMap.put(0, cardPile0);
        cardsMap.put(1, cardPile1);
        cardsMap.put(2, cardPile2);

        Image backTexture = new Image(Objects.requireNonNull(getClass().getResource(TILE_PATH + "tile_back.png")).toExternalForm());

        // initialize covered tiles
        List<ImageView> tileList = new ArrayList<>();
        for(int i = 1; i<151; i++) {

            ImageView tile = new ImageView();
            tile.setId(String.valueOf(i));
            tile.setImage(backTexture);
            tile.setFitWidth(TILE_SIZE);
            tile.setFitHeight(TILE_SIZE);
            tile.setCursor(Cursor.HAND);
            tile.setLayoutX((Math.random() * 588) + 712);
            tile.setLayoutY((Math.random() * 530) + 180);
            tile.setTranslateX(1500 - tile.getLayoutX() * .7);
            tile.setTranslateY(100 - tile.getLayoutY() * .7);
            tile.setScaleX(1.5);
            tile.setScaleY(1.5);
            tile.setOpacity(0.4);

            DropShadow dropShadow = new DropShadow();
            dropShadow.setColor(new Color(.2, .2, .2, .8));
            dropShadow.setWidth(40);
            dropShadow.setHeight(40);
            dropShadow.setRadius(20);
            tile.setEffect(dropShadow);

            tileList.add(tile);
            tileMap.put(i, tile);

            tileManager.enableEvents(tile);
        }
        Collections.shuffle(tileList);

        for(int i = 0; i < tileList.size(); i++) {
            ImageView tile = tileList.get(i);
            animationHandler.playTileReturn(tile, i * 20);
        }
        mainContentPane.getChildren().addAll(tileList);

        // initialize tiles sockets on board


        rightButton.setOnAction(this::onClickTurnTileRight);
        leftButton.setOnAction(this::onClickTurnTileLeft);

        selectedTileSlot.setOnMouseReleased(_ -> tileManager.moveToSelectedSlot());

        settingsButton.setOnAction(_ -> Popup.newSettingsPopup().show(rootPane, mainContentPane));

        Image image = new Image(Objects.requireNonNull(getClass().getResource(TILE_PATH + "tile_starter_cabin_green.png")).toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(TILE_SIZE);
        imageView.setFitHeight(TILE_SIZE);
        gridPane.add(imageView, 3, 2);
    }


    @Override
    public void updateCallBack() {
        virtualServer.setCallBack(cmd -> {
            ObjectMapper mapper = new ObjectMapper();
            String type = mapper.readTree(cmd).get("type").asText();
            switch (type) {

                case "gameState" -> {
                    GameStateMessage gameStateMessage = mapper.readValue(cmd, GameStateMessage.class);

                    if (gameStateMessage.getGameState() != GameModel.GameState.APPLYING_CARDS) throw new IllegalStateException("Invalid game state");
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

                String pilePath = switch (shipType) {
                    case LEARNING, LEVEL_ONE -> "/textures/cards/card_pile_level_1.png";
                    case LEVEL_TWO -> "/textures/cards/card_pile_level_2.png";
                };
                Image pile = new Image(Objects.requireNonNull(getClass().getResourceAsStream(pilePath)));
                cardPile0.setImage(pile);
                cardPile1.setImage(pile);
                cardPile2.setImage(pile);

                tileManager.initializeGridPane(shipType);

                // initialize with the center cabin tile
                tileManager.updateAdjacentCells(3, 2);
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
    public void onClickCardPile(Event event) {
        tileManager.finalizeSelectedTile();
        String pileId = ((Node) event.getSource()).getId();
        virtualServer.sendMessage(new CardsTakenMessage(null, Integer.parseInt(pileId)));
    }

    @FXML
    public void onClickTurnTileRight(Event event) {
        animationHandler.rotateTile(selectedTile, selectedTile, 90);
    }

    @FXML
    public void onClickTurnTileLeft(Event event) {
        animationHandler.rotateTile(selectedTile, selectedTile, -90);
    }
}