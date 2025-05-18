package it.polimi.galaxy_trucker.client.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.client.*;
import it.polimi.galaxy_trucker.client.popups.PendingPopup;
import it.polimi.galaxy_trucker.client.popups.Popup;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.BoardTypeMessage;
import it.polimi.galaxy_trucker.common.messages.GameResetMessage;
import it.polimi.galaxy_trucker.common.messages.GameStateMessage;
import it.polimi.galaxy_trucker.common.messages.flight.*;
import it.polimi.galaxy_trucker.server.model.BoardType;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.ShipBoard;
import it.polimi.galaxy_trucker.server.model.tiles.CrewType;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;


public class FlightController implements VirtualServerAware {

    private final VirtualServer virtualServer = VirtualServer.getInstance();

    @FXML private AnchorPane mainContentPane;

    @FXML private Label statusLabel;

    @FXML private ImageView gameBoardImage;

    @FXML private Button settingsButton;

    @FXML private StackPane deckPane;

    @FXML private ImageView shipImageP1;
    @FXML private ImageView shipImageP2;
    @FXML private ImageView shipImageP3;
    @FXML private ImageView shipImageP4;

    @FXML private GridPane shipGridP1;
    @FXML private GridPane shipGridP2;
    @FXML private GridPane shipGridP3;
    @FXML private GridPane shipGridP4;

    @FXML private GridPane batteriesGridP1;
    @FXML private GridPane batteriesGridP2;
    @FXML private GridPane batteriesGridP3;
    @FXML private GridPane batteriesGridP4;
    @FXML private GridPane crewGridP1;
    @FXML private GridPane crewGridP2;
    @FXML private GridPane crewGridP3;
    @FXML private GridPane crewGridP4;
    @FXML private GridPane cargoGridP1;
    @FXML private GridPane cargoGridP2;
    @FXML private GridPane cargoGridP3;
    @FXML private GridPane cargoGridP4;

    @FXML private HBox nameBoxP2;
    @FXML private HBox nameBoxP3;
    @FXML private HBox nameBoxP4;

    @FXML private VBox P2Box;
    @FXML private VBox P3Box;
    @FXML private VBox P4Box;

    private BoardType boardType;
    private final Map<String, GridPane> playerGridMap = new HashMap<>();
    private final Map<String, GridPane> batteriesGridMap = new HashMap<>();
    private final Map<String, GridPane> crewGridMap = new HashMap<>();
    private final Map<String, GridPane> cargoGridMap = new HashMap<>();
    private final Map<GridPane, Integer> gridCellsSize = new HashMap<>();
    private final Map<String, StackPane> playerPawns = new HashMap<>();
    private final Map<Integer, Point> boardSlotCoordinates = new HashMap<>();

    public void initialize() {
        assignPlayerGrids(ClientSession.getPlayerNames());
        generatePlayerPawns(ClientSession.getPlayerNames());
        setGridSize(65, shipGridP1, batteriesGridP1, crewGridP1, cargoGridP1);
        setGridSize(28, shipGridP2, batteriesGridP2, crewGridP2, cargoGridP2,
                             shipGridP3, batteriesGridP3, crewGridP3, cargoGridP3,
                             shipGridP4, batteriesGridP4, crewGridP4, cargoGridP4);

        settingsButton.setOnAction(_ -> Popup.newSettingsPopup().show());
    }

    @Override
    public void updateCallBack() {
        virtualServer.setCallBack(cmd -> {
            ObjectMapper mapper = new ObjectMapper();
            String type = mapper.readTree(cmd).get("type").asText();
            switch (type) {

                case "gameState" -> {
                    GameStateMessage message = mapper.readValue(cmd, GameStateMessage.class);

//                    if (message.getGameState() == GameModel.GameState.CORRECTING_SHIP) {
//                        virtualServer.setCallBack(null);
//                        Platform.runLater(() -> SceneSwitch.switchScene("FlightScene"));
//                        return;
//                    }
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

        String type = mapper.readTree(cmd).get("type").asText();

        switch (type) {
            case "gameState"      -> handleGameState(mapper.readValue(cmd, GameStateMessage.class));
            case "boardType"      -> handleBoardType(mapper.readValue(cmd, BoardTypeMessage.class));
            case "shipBoard"      -> handleShipBoard(mapper.readValue(cmd, ShipBoardMessage.class));
            case "playerPosition" -> handlePlayerPosition(mapper.readValue(cmd, PlayerPositionMessage.class));
            case "deckLevels"     -> handleDeckLevels(mapper.readValue(cmd, DeckLevelsMessage.class));
            case "batteries"      -> handleBatteries(mapper.readValue(cmd, BatteriesMessage.class));
            case "crew"           -> handleCrew(mapper.readValue(cmd, CrewMessage.class));
            case "invalidShip"    -> handleInvalidShip(mapper.readValue(cmd, InvalidShipMessage.class));
            default               -> Logger.logError("Unrecognized command: " + type);
        }
    }

    private void handleGameState(GameStateMessage message) {

        switch (message.getGameState()) {

            case CORRECTING_SHIP, ASSIGNING_CREW -> Logger.logDebug("new state: " + message.getGameState());



            default -> throw new IllegalStateException("Invalid game state");
        }
    }

    private void handleBoardType(BoardTypeMessage message) {
        boardType = message.getBoardType();

        updateShipTypes(boardType);
        updateGameBoardType(boardType);
        generateGameBoardSlots(boardType);
    }

    private void updateShipTypes(BoardType boardType) {
        String boardPath = switch (boardType) {
            case LEARNING, LEVEL_ONE -> "/textures/cardboards/shipboard_1.png";
            case LEVEL_TWO -> "/textures/cardboards/shipboard_2.png";
        };
        Image ship = new Image(Objects.requireNonNull(getClass().getResourceAsStream(boardPath)));

        List<ImageView> shipImages = List.of(shipImageP1, shipImageP2, shipImageP3, shipImageP4);
        for (ImageView imageView : shipImages) {
            imageView.setImage(ship);

            FadeTransition boardFade = new FadeTransition(Duration.millis(1500), imageView);
            boardFade.setFromValue(0);
            boardFade.setToValue(1);
            boardFade.play();
        }
    }

    private void updateGameBoardType(BoardType boardType) {
        String boardPath = switch (boardType) {
            case LEARNING  -> "/textures/cardboards/cardboard_L.png";
            case LEVEL_ONE -> "/textures/cardboards/cardboard_1.png";
            case LEVEL_TWO -> "/textures/cardboards/cardboard_2.png";
        };
        Image ship = new Image(Objects.requireNonNull(getClass().getResourceAsStream(boardPath)));

            gameBoardImage.setImage(ship);

            FadeTransition boardFade = new FadeTransition(Duration.millis(1500), gameBoardImage);
            boardFade.setFromValue(0);
            boardFade.setToValue(1);
            boardFade.play();
    }

    private void generateGameBoardSlots(BoardType boardType) {
        boardSlotCoordinates.clear();

        try (InputStream is = getClass().getResourceAsStream("/public/slot_positions.json")) {
            if (is == null) throw new IOException("JSON not found!");

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(is);

            String key = boardType.toString();
            JsonNode boardNode = root.get(key);

            for (Iterator<String> it = boardNode.fieldNames(); it.hasNext(); ) {
                String index = it.next();
                JsonNode pos = boardNode.get(index);
                int x = pos.get("x").asInt();
                int y = pos.get("y").asInt();
                boardSlotCoordinates.put(Integer.parseInt(index), new Point(x, y));
            }

        } catch (IOException e) {
            Logger.logError("Error reading json:" + e.getMessage());
        }
    }

    private void handleShipBoard(ShipBoardMessage message) {
        List<TileInfo> tiles = message.getTiles();
        String playerName = message.getPlayerName();
        String color = ClientSession.getPlayerInfo(playerName).getColor();

        GridPane grid = playerGridMap.get(playerName);

        if (grid != null) {
            updateShipPane(grid, tiles, color);
        } else {
            Logger.logError("No grid assigned for player: " + playerName);
        }
    }

    private void handlePlayerPosition(PlayerPositionMessage message) {
        double originalWidth = 1055; // <-- Width of the original PNG
        double scale = 600 / originalWidth;

        String playerName = message.getPlayerName();
        StackPane pawn = playerPawns.get(playerName);

        int oldPosition = flightTrackMod(message.getOldPosition());
        int newPosition = flightTrackMod(message.getNewPosition());
        double gameBoardX = gameBoardImage.getLayoutX();
        double gameBoardY = gameBoardImage.getLayoutY();
        double oldX = gameBoardX + boardSlotCoordinates.get(oldPosition).x * scale - pawn.getPrefWidth() / 2;
        double oldY = gameBoardY + boardSlotCoordinates.get(oldPosition).y * scale - pawn.getPrefHeight() / 2;
        double newX = gameBoardX + boardSlotCoordinates.get(newPosition).x * scale - pawn.getPrefWidth() / 2;
        double newY = gameBoardY + boardSlotCoordinates.get(newPosition).y * scale - pawn.getPrefHeight() / 2;

        TranslateTransition move = new TranslateTransition(Duration.millis(400), pawn);
        move.setFromX(oldX - pawn.getLayoutX());
        move.setFromY(oldY - pawn.getLayoutY());
        move.setToX(newX - pawn.getLayoutX());
        move.setToY(newY - pawn.getLayoutY());
        move.play();

        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), pawn);
        fadeOut.setToValue(.7);
        fadeOut.play();

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(150), pawn);
        scaleOut.setToX(1.1);
        scaleOut.setToY(1.1);
        scaleOut.play();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(150), pawn);
        fadeIn.setDelay(Duration.millis(250));
        fadeIn.setToValue(1);
        fadeIn.play();

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(150), pawn);
        scaleIn.setDelay(Duration.millis(250));
        scaleIn.setToX(1);
        scaleIn.setToY(1);
        scaleIn.play();
    }

    private int flightTrackMod(int position) {
        int trackLength = boardType.getFlightTrackLength();
        return (position % trackLength + trackLength) % trackLength;
    }

    private void handleDeckLevels(DeckLevelsMessage message) {

        List<Integer> deckLevels = message.getDeckLevels().reversed();
        for(Integer cardLevel : deckLevels) {
            if (cardLevel < 1 || cardLevel > 2) {
                throw new IllegalStateException("Invalid card level: " + cardLevel);
            }
            String path = "/textures/cards/card_level_" + cardLevel + "_back.png";
            Image card = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
            ImageView imageView = new ImageView(card);
            imageView.setFitWidth(200);
            imageView.setFitHeight(250);
            imageView.setPreserveRatio(true);
            imageView.setRotate(Math.random() * 10 - 5);
            imageView.setTranslateX(Math.random() * 10 - 5);
            imageView.setTranslateY(Math.random() * 10 - 5);
            DropShadow shadow = new DropShadow();
            shadow.setColor(new Color(0, 0, 0, .5));
            shadow.setWidth(100);
            shadow.setHeight(100);
            shadow.setRadius(20);
            imageView.setEffect(shadow);
            deckPane.getChildren().add(imageView);
        }
    }

    private void handleBatteries(BatteriesMessage message) {
        List<ShipBoard.BatteriesData> batteriesData = message.getBatteries();
        String playerName = message.getPlayerName();

        GridPane grid = batteriesGridMap.get(playerName);

        if (grid != null) {
            updateBatteriesPane(grid, batteriesData);
        } else {
            Logger.logError("No batteries grid assigned for player: " + playerName);
        }
    }

    private void handleCrew(CrewMessage message) {
        List<ShipBoard.CrewData> crewData = message.getCrew();
        String playerName = message.getPlayerName();

        GridPane grid = crewGridMap.get(playerName);

        if (grid != null) {
            updateCrewPane(grid, crewData);
        } else {
            Logger.logError("No crew grid assigned for player: " + playerName);
        }
    }

    private void handleInvalidShip(InvalidShipMessage message) {
        String playerName = message.getPlayerName();
        if(!ClientSession.isSelf(playerName)) {
            Logger.logError("Should only receive its own invalid tiles");
            return;
        }
        if(message.getInvalidTiles().isEmpty()) {
            statusLabel.setText(LanguageManager.get("status.valid_ship"));
            return;
        }
        statusLabel.setText(LanguageManager.get("status.invalid_ship"));
        for(Node node : shipGridP1.getChildren()) {
            if(!(node instanceof ImageView tile)) continue;
            int x = GridPane.getColumnIndex(node);
            int y = GridPane.getRowIndex(node);
            if(isInvalid(x, y, message.getInvalidTiles())) {
                markAsInvalid(tile);
                tile.setOnMouseClicked(_ -> virtualServer.sendMessage(new SelectedTileMessage(x, y)));
            }
        }
    }

    private boolean isInvalid(int x, int y, Set<Point> invalidPoints) {
        for(Point point : invalidPoints) {
            if(point.x == x && point.y == y) return true;
        }
        return false;
    }

    private void markAsInvalid(ImageView tile) {
        tile.setCursor(Cursor.HAND);
        InnerShadow shadow = new InnerShadow();
        shadow.setColor(new Color(1, 0, 0, .5));
        shadow.setBlurType(BlurType.THREE_PASS_BOX);
        shadow.setWidth(50);
        shadow.setHeight(50);
        shadow.setRadius(10);
        shadow.setChoke(1.0);
        tile.setEffect(shadow);
    }

    private void updateShipPane(GridPane shipGrid, List<TileInfo> tiles, String color) {
        double cellSize = gridCellsSize.get(shipGrid);

        Map<Point, Node> existingTiles = getCurrentTileMap(shipGrid);
        Set<Point> newTilePositions = placeNewTiles(shipGrid, tiles, color, cellSize, existingTiles);
        animateAndRemoveOldTiles(shipGrid, existingTiles, newTilePositions);
    }

    private Map<Point, Node> getCurrentTileMap(GridPane shipGrid) {
        Map<Point, Node> map = new HashMap<>();
        for (Node node : shipGrid.getChildren()) {
            node.setEffect(null);
            Integer x = GridPane.getColumnIndex(node);
            Integer y = GridPane.getRowIndex(node);
            if (x != null && y != null) {
                map.put(new Point(x, y), node);
            }
        }
        return map;
    }

    private Set<Point> placeNewTiles(GridPane shipGrid, List<TileInfo> tiles, String color, double cellSize, Map<Point, Node> existingTiles) {
        Set<Point> positions = new HashSet<>();

        for (TileInfo tileInfo : tiles) {
            Point pos = new Point(tileInfo.getX(), tileInfo.getY());
            positions.add(pos);

            if (existingTiles.containsKey(pos)) continue;

            Image image = loadTileImage(tileInfo.getId(), color);
            ImageView tile = createTileImageView(image, tileInfo.getRotation(), cellSize);
            shipGrid.add(tile, tileInfo.getX(), tileInfo.getY());
        }

        return positions;
    }

    private Image loadTileImage(int id, String color) {
        String path = (id < 0 || id > 151)
                ? "/textures/tiles/tile_starter_cabin_" + color.toLowerCase() + ".png"
                : "/textures/tiles/tile_" + id + ".png";
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
    }

    private ImageView createTileImageView(Image image, int rotation, double cellSize) {
        ImageView tile = new ImageView(image);
        tile.setPreserveRatio(true);
        tile.setCache(true);
        tile.setFitWidth(cellSize);
        tile.setFitHeight(cellSize);
        tile.setRotate(rotation * 90);
        return tile;
    }

    private void animateAndRemoveOldTiles(GridPane shipGrid, Map<Point, Node> existingTiles, Set<Point> newTilePositions) {
        for (Map.Entry<Point, Node> entry : existingTiles.entrySet()) {
            if (!newTilePositions.contains(entry.getKey())) {
                Node node = entry.getValue();

                int col = GridPane.getColumnIndex(node);
                int row = GridPane.getRowIndex(node);

                // Center point of the grid
                double dx = col - 3;
                double dy = row - 2;

                ParallelTransition animation = getTileRemovalAnimation(dx, dy, node);
                animation.setOnFinished(_ -> shipGrid.getChildren().remove(node));
                animation.play();
            }
        }
    }

    private static ParallelTransition getTileRemovalAnimation(double dx, double dy, Node node) {
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance == 0) distance = 1;
        double moveFactor = 100;
        double translateX = (dx / distance) * moveFactor;
        double translateY = (dy / distance) * moveFactor;

        FadeTransition fade = new FadeTransition(Duration.millis(500), node);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        RotateTransition rotate = new RotateTransition(Duration.millis(500), node);
        rotate.setByAngle(180);

        TranslateTransition translate = new TranslateTransition(Duration.millis(500), node);
        translate.setByX(translateX);
        translate.setByY(translateY);

        return new ParallelTransition(fade, rotate, translate);
    }

    private void updateBatteriesPane(GridPane batteriesGrid, List<ShipBoard.BatteriesData> batteries) {
        batteriesGrid.getChildren().clear();

        for (ShipBoard.BatteriesData batteriesData : batteries) {

            if(batteriesData.batteries() == batteriesData.capacity()) return;

            String imagePath = "/textures/sprites/batteries_" + batteriesData.capacity() + "_" + batteriesData.batteries() + ".png";

            ImageView overlay = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath))));

            overlay.setPreserveRatio(true);
            overlay.setCache(true);
            overlay.setRotate(batteriesData.rotation() * 90);

            double cellSize = gridCellsSize.get(batteriesGrid);
            overlay.setFitWidth(cellSize);
            overlay.setFitHeight(cellSize);

            batteriesGrid.add(overlay, batteriesData.x(), batteriesData.y());
        }
    }

    private void updateCrewPane(GridPane crewGrid, List<ShipBoard.CrewData> crewData) {
        crewGrid.getChildren().clear();

        for (ShipBoard.CrewData cabinData : crewData) {

            String imagePath = switch (cabinData.crew()) {
                case SINGLE_HUMAN -> "/textures/sprites/crew_human_1.png";
                case DOUBLE_HUMAN -> "/textures/sprites/crew_human_2.png";
                case PURPLE_ALIEN -> "/textures/sprites/crew_purple_alien.png";
                case BROWN_ALIEN -> "/textures/sprites/crew_brown_alien.png";

                case NONE -> null;
            };

            ImageView overlay = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath))));

            overlay.setPreserveRatio(true);
            overlay.setCache(true);

            double cellSize = gridCellsSize.get(crewGrid);
            overlay.setFitWidth(cellSize);
            overlay.setFitHeight(cellSize);

            crewGrid.add(overlay, cabinData.x(), cabinData.y());
        }
    }

    private void assignPlayerGrids(Set<String> playerNames) {
        playerGridMap.clear();

        // Always assign self to shipGridP1
        String self = ClientSession.getUsername();
        playerGridMap.put(self, shipGridP1);
        batteriesGridMap.put(self, batteriesGridP1);
        crewGridMap.put(self, crewGridP1);
        cargoGridMap.put(self, cargoGridP1);

        // Assign other players to P2, P3, P4
        List<GridPane> otherGrids = List.of(shipGridP2, shipGridP3, shipGridP4);
        List<GridPane> otherBatteriesGrids = List.of(batteriesGridP2, batteriesGridP3, batteriesGridP4);
        List<GridPane> otherCrewGrids = List.of(crewGridP2, crewGridP3, crewGridP4);
        List<GridPane> otherCargoGrids = List.of(cargoGridP2, cargoGridP3, cargoGridP4);
        List<HBox> nameBoxes = List.of(nameBoxP2, nameBoxP3, nameBoxP4);
        List<VBox> shipBoxes = List.of(P2Box, P3Box, P4Box);
        int i = 0;

        for (String name : playerNames) {
            if (!name.equals(self)) {
                if (i >= otherGrids.size()) {
                    Logger.logWarning("Too many players for available grids");
                    return;
                }
                playerGridMap.put(name, otherGrids.get(i));
                batteriesGridMap.put(name, otherBatteriesGrids.get(i));
                crewGridMap.put(name, otherCrewGrids.get(i));
                cargoGridMap.put(name, otherCargoGrids.get(i));
                updateNameBox(nameBoxes.get(i), name);
                shipBoxes.get(i).setVisible(true);
                i++;
            }
        }
    }

    private void generatePlayerPawns(Set<String> playerNames) {
        for (String name : playerNames) {
            StackPane pawn = ClientSession.getPlayerProfilePicture(name, 40);
            playerPawns.put(name, pawn);
            mainContentPane.getChildren().add(pawn);
        }
    }

    private void updateNameBox(HBox nameBox, String name) {
        nameBox.getChildren().clear();

        StackPane profilePic = ClientSession.getPlayerProfilePicture(name, 30);
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("white-label");
        nameBox.getChildren().addAll(profilePic, nameLabel);
    }

    private void setGridSize(int size, GridPane... panes) {
        for (GridPane pane : panes) {
            gridCellsSize.put(pane, size);
        }
    }
}