package it.polimi.galaxy_trucker.client.controllers.flight;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.client.*;
import it.polimi.galaxy_trucker.client.popups.PendingPopup;
import it.polimi.galaxy_trucker.client.popups.Popup;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.BoardTypeMessage;
import it.polimi.galaxy_trucker.common.messages.GameResetMessage;
import it.polimi.galaxy_trucker.common.messages.GameStateMessage;
import it.polimi.galaxy_trucker.common.messages.SimpleMessage;
import it.polimi.galaxy_trucker.common.messages.flight.*;
import it.polimi.galaxy_trucker.common.messages.flight.CardPickedMessage;
import it.polimi.galaxy_trucker.common.messages.flight.cardActions.CargoDoneMessage;
import it.polimi.galaxy_trucker.common.messages.flight.cardActions.CargoSwapMessage;
import it.polimi.galaxy_trucker.common.messages.flight.cardActions.CargoUnloadMessage;
import it.polimi.galaxy_trucker.server.model.BoardType;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.ShipBoard;
import it.polimi.galaxy_trucker.server.model.tiles.BlockType;
import it.polimi.galaxy_trucker.server.model.tiles.CrewType;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.awt.*;
import java.util.*;
import java.util.List;


public class FlightController implements VirtualServerAware {

    private final VirtualServer virtualServer = VirtualServer.getInstance();

    @FXML private AnchorPane mainContentPane;

    @FXML private Label statusLabel;

    @FXML private StackPane gameBoardPane;
    @FXML private ImageView gameBoardImage;

    @FXML private Button settingsButton;

    @FXML private Button pickCardButton;

    @FXML private StackPane deckPane;
    @FXML private Region currentCardRegion;

    @FXML private StackPane shipboardP1;

    @FXML private ImageView shipImageP1, shipImageP2, shipImageP3, shipImageP4;

    @FXML private GridPane shipGridP1, shipGridP2, shipGridP3, shipGridP4;

    @FXML private GridPane batteriesGridP1, batteriesGridP2, batteriesGridP3, batteriesGridP4;
    @FXML private GridPane crewGridP1, crewGridP2, crewGridP3, crewGridP4;
    @FXML private GridPane cargoGridP1, cargoGridP2, cargoGridP3, cargoGridP4;

    @FXML private Label creditsLabelP1, creditsLabelP2, creditsLabelP3, creditsLabelP4;

    @FXML private HBox nameBoxP2, nameBoxP3, nameBoxP4;

    @FXML private VBox P1Box, P2Box, P3Box, P4Box;

    @FXML private Button doneButton;

    CardsHandler cardsHandler;

    private final Map<String, GridPane> playerGridMap = new HashMap<>();
    private final Map<String, GridPane> batteriesGridMap = new HashMap<>();
    private final Map<String, GridPane> crewGridMap = new HashMap<>();
    private final Map<String, GridPane> cargoGridMap = new HashMap<>();
    private final Map<String, Label> creditsLabelMap = new HashMap<>();

    private final Map<GridPane, Integer> gridCellsSize = new HashMap<>();

    public void initialize() {
        cardsHandler = new CardsHandler(statusLabel, cargoGridP1, shipboardP1, P1Box, deckPane, currentCardRegion, pickCardButton);
        FlightAnim.queueUpdateStatus(statusLabel, LanguageManager.get("status.validating_ship"), 4000);

        assignPlayerGrids(ClientSession.getPlayerNames());
        FlightAnim.generatePlayerPawns(ClientSession.getPlayerNames(), gameBoardPane);
        setGridSize(71, shipGridP1, batteriesGridP1, crewGridP1, cargoGridP1);
        setGridSize(28, shipGridP2, batteriesGridP2, crewGridP2, cargoGridP2,
                             shipGridP3, batteriesGridP3, crewGridP3, cargoGridP3,
                             shipGridP4, batteriesGridP4, crewGridP4, cargoGridP4);

        settingsButton.setOnAction(_ -> Popup.newSettingsPopup().show());
        doneButton.setOnAction(_ -> virtualServer.sendMessage(new SimpleMessage("done")));
        pickCardButton.setOnAction(_ -> virtualServer.sendMessage(new SimpleMessage("pick_card")));
    }

    @Override
    public void updateCallBack() {
        virtualServer.setCallBack(cmd -> {
            ObjectMapper mapper = new ObjectMapper();
            String type = mapper.readTree(cmd).get("type").asText();
            switch (type) {

                case "game_state" -> {
                    GameStateMessage message = mapper.readValue(cmd, GameStateMessage.class);

                    if (message.getGameState() == GameModel.GameState.GAME_OVER) {
                        virtualServer.setCallBack(null);
                        Platform.runLater(() -> SceneSwitch.switchScene("LobbyScene"));
                        return;
                    }
                }

                case "game_reset" -> {
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
            case "game_state"        -> handleGameState(mapper.readValue(cmd, GameStateMessage.class));
            case "board_type"        -> handleBoardType(mapper.readValue(cmd, BoardTypeMessage.class));
            case "ship_board"        -> handleShipBoard(mapper.readValue(cmd, ShipBoardMessage.class));
            case "player_position"   -> handlePlayerPosition(mapper.readValue(cmd, PlayerPositionMessage.class));
            case "batteries"         -> handleBatteries(mapper.readValue(cmd, BatteriesMessage.class));
            case "crew"              -> handleCrew(mapper.readValue(cmd, CrewMessage.class));
            case "cargo"             -> handleCargo(mapper.readValue(cmd, CargoMessage.class));
            case "credits"           -> handleCredits(mapper.readValue(cmd, CreditsMessage.class));
            case "invalid_tiles"     -> handleInvalidTiles(mapper.readValue(cmd, TilesMessage.class));
            case "alien_cabin_tiles" -> handleAlienCabinTiles(mapper.readValue(cmd, TilesMessage.class));
            case "pick_available"    -> handlePickAvailable(mapper.readValue(cmd, PickAvailableMessage.class));

            default                  -> cardsHandler.handleCardCommand(type, cmd);
        }
    }

    private void handleGameState(GameStateMessage message) {

        switch (message.getGameState()) {

            case ASSIGNING_CREW -> {}
            case PICKING_CARDS -> doneButton.setVisible(false);

            default -> throw new IllegalStateException("Invalid game state");
        }
        Logger.logDebug("new state: " + message.getGameState());
    }

    private void handleBoardType(BoardTypeMessage message) {
        BoardType boardType = message.getBoardType();

        updateShipTypes(boardType);
        updateGameBoardType(boardType);
        FlightAnim.generateGameBoardSlots(boardType);
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

    private void handleShipBoard(ShipBoardMessage message) {
        Set<TileInfo> tiles = message.getTiles();
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
        FlightAnim.movePlayerPawn(message.getPlayerName(), message.getNewPosition(), gameBoardImage);
    }

    private void handleBatteries(BatteriesMessage message) {
        Set<ShipBoard.BatteriesData> batteriesData = message.getBatteries();
        String playerName = message.getPlayerName();

        GridPane grid = batteriesGridMap.get(playerName);

        if (grid != null) {
            updateBatteriesPane(grid, batteriesData);
        } else {
            Logger.logError("No batteries grid assigned for player: " + playerName);
        }
    }

    private void handleCrew(CrewMessage message) {
        Set<ShipBoard.CrewData> crewData = message.getCrew();
        String playerName = message.getPlayerName();

        GridPane grid = crewGridMap.get(playerName);

        if (grid != null) {
            updateCrewPane(grid, crewData);
        } else {
            Logger.logError("No crew grid assigned for player: " + playerName);
        }
    }

    private void handleCargo(CargoMessage message) {
        Set<ShipBoard.CargoData> crewData = message.getCargo();
        String playerName = message.getPlayerName();

        GridPane grid = cargoGridMap.get(playerName);

        if (grid != null) {
            cardsHandler.updateCargoPane(grid, crewData, gridCellsSize.get(grid));
        } else {
            Logger.logError("No crew grid assigned for player: " + playerName);
        }
    }

    private void handleCredits(CreditsMessage message) {

        String playerName = message.getPlayerName();

        Label label = creditsLabelMap.get(playerName);

        if (label != null) {
            label.setText(message.getCredits() + " ¢");
        } else {
            Logger.logError("No label assigned for player: " + playerName);
        }
    }

    private void handleInvalidTiles(TilesMessage message) {
        String playerName = message.getPlayerName();
        if(!ClientSession.isSelf(playerName)) {
            Logger.logError("Should only receive its own invalid tiles");
            return;
        }
        if(message.getTiles().isEmpty()) {
            FlightAnim.queueUpdateStatus(statusLabel, LanguageManager.get("status.valid_ship"), 800);
            return;
        }
        FlightAnim.queueUpdateStatus(statusLabel, LanguageManager.get("status.invalid_ship"), 800);
        for(Node node : shipGridP1.getChildren()) {
            if(!(node instanceof ImageView tile)) continue;
            int x = GridPane.getColumnIndex(node);
            int y = GridPane.getRowIndex(node);
            if(setContainsPoint(x, y, message.getTiles())) {
                FlightAnim.markTile(tile, new Color(1, 0, 0, .5));
                tile.setOnMouseClicked(_ -> virtualServer.sendMessage(new SelectedTileMessage(x, y)));
            }
        }
    }

    private void handleAlienCabinTiles(TilesMessage message) {
        String playerName = message.getPlayerName();
        if(!ClientSession.isSelf(playerName)) {
            Logger.logError("Should only receive its own invalid tiles");
            return;
        }
        if(message.getTiles().isEmpty()) {
            FlightAnim.queueUpdateStatus(statusLabel, LanguageManager.get("status.no_alien_cabin"), 800);
            if(doneButton.isVisible()) doneButton.setDisable(true);
            FlightAnim.unmarkTiles(shipGridP1);
            return;
        }
        FlightAnim.queueUpdateStatus(statusLabel, LanguageManager.get("status.choose_cabin"), 800);
        doneButton.setVisible(true);
        for(Node node : shipGridP1.getChildren()) {
            if(!(node instanceof ImageView tile)) continue;
            int x = GridPane.getColumnIndex(node);
            int y = GridPane.getRowIndex(node);
            if(setContainsPoint(x, y, message.getTiles())) {
                FlightAnim.markTile(tile, new Color(1, 1, 1, .5));
                tile.setOnMouseClicked(_ -> virtualServer.sendMessage(new SelectedTileMessage(x, y)));
            }
        }
    }

    private void handlePickAvailable(PickAvailableMessage message) {

        String pickerName = message.getPlayerName();
        boolean isSelf = ClientSession.isSelf(pickerName);

        if(isSelf) {
            pickCardButton.setDisable(false);
            FlightAnim.queueUpdateStatus(statusLabel, LanguageManager.get("status.pick_card"), 800);
        } else {
            FlightAnim.queueUpdateStatus(statusLabel, LanguageManager.get("status.waiting_pick", pickerName), 800);
        }
    }

    private boolean setContainsPoint(int x, int y, Set<Point> points) {
        for(Point point : points) {
            if(point.x == x && point.y == y) return true;
        }
        return false;
    }

    private void updateShipPane(GridPane shipGrid, Set<TileInfo> tiles, String color) {
        double cellSize = gridCellsSize.get(shipGrid);

        if(shipGrid == shipGridP1) FlightAnim.unmarkTiles(shipGrid);

        Map<Point, Node> existingTiles = getCurrentTileMap(shipGrid);
        Set<Point> newTilePositions = placeNewTiles(shipGrid, tiles, color, cellSize, existingTiles);
        animateAndRemoveOldTiles(shipGrid, existingTiles, newTilePositions);
    }

    private Map<Point, Node> getCurrentTileMap(GridPane shipGrid) {
        Map<Point, Node> map = new HashMap<>();
        for (Node node : shipGrid.getChildren()) {
            Integer x = GridPane.getColumnIndex(node);
            Integer y = GridPane.getRowIndex(node);
            if (x != null && y != null) {
                map.put(new Point(x, y), node);
            }
        }
        return map;
    }

    private Set<Point> placeNewTiles(GridPane shipGrid, Set<TileInfo> tiles, String color, double cellSize, Map<Point, Node> existingTiles) {
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

                ParallelTransition animation = FlightAnim.getTileRemovalAnimation(dx, dy, node);
                animation.setOnFinished(_ -> shipGrid.getChildren().remove(node));
                animation.play();
            }
        }
    }

    private void updateBatteriesPane(GridPane batteriesGrid, Set<ShipBoard.BatteriesData> batteries) {
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

    private void updateCrewPane(GridPane crewGrid, Set<ShipBoard.CrewData> crewData) {
        crewGrid.getChildren().clear();

        for (ShipBoard.CrewData cabinData : crewData) {
            if(cabinData.crew() == CrewType.NONE) continue;
            String imagePath = switch (cabinData.crew()) {
                case SINGLE_HUMAN -> "/textures/sprites/crew_human_1.png";
                case DOUBLE_HUMAN -> "/textures/sprites/crew_human_2.png";
                case PURPLE_ALIEN -> "/textures/sprites/crew_purple_alien.png";
                case BROWN_ALIEN -> "/textures/sprites/crew_brown_alien.png";

                default -> throw new IllegalStateException("Unexpected value: " + cabinData.crew());
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
        creditsLabelMap.put(self, creditsLabelP1);

        // Assign other players to P2, P3, P4
        List<GridPane> otherGrids = List.of(shipGridP2, shipGridP3, shipGridP4);
        List<GridPane> otherBatteriesGrids = List.of(batteriesGridP2, batteriesGridP3, batteriesGridP4);
        List<GridPane> otherCrewGrids = List.of(crewGridP2, crewGridP3, crewGridP4);
        List<GridPane> otherCargoGrids = List.of(cargoGridP2, cargoGridP3, cargoGridP4);
        List<Label> otherCreditsLabels = List.of(creditsLabelP2, creditsLabelP3, creditsLabelP4);
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
                creditsLabelMap.put(name, otherCreditsLabels.get(i));
                updateNameBox(nameBoxes.get(i), name);
                shipBoxes.get(i).setVisible(true);
                i++;
            }
        }
    }

    private void updateNameBox(HBox nameBox, String name) {
        nameBox.getChildren().clear();

        StackPane profilePic = ClientSession.getPlayerProfilePicture(name, 30);
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("white-label");
        nameLabel.setStyle("-fx-font-weight: bold");
        nameBox.getChildren().addAll(profilePic, nameLabel);
    }

    private void setGridSize(int size, GridPane... panes) {
        for (GridPane pane : panes) {
            gridCellsSize.put(pane, size);
        }
    }
}