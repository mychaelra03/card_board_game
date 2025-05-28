package it.polimi.galaxy_trucker.client.controllers.flight;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.client.ClientSession;
import it.polimi.galaxy_trucker.client.LanguageManager;
import it.polimi.galaxy_trucker.client.VirtualServer;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.SimplePlayerMessage;
import it.polimi.galaxy_trucker.common.messages.flight.*;
import it.polimi.galaxy_trucker.common.messages.flight.cardActions.CardDecisionMessage;
import it.polimi.galaxy_trucker.common.messages.flight.cardActions.CargoDoneMessage;
import it.polimi.galaxy_trucker.common.messages.flight.cardActions.CargoSwapMessage;
import it.polimi.galaxy_trucker.common.messages.flight.cardActions.CargoUnloadMessage;
import it.polimi.galaxy_trucker.server.model.ShipBoard;
import it.polimi.galaxy_trucker.server.model.tiles.BlockType;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.awt.*;
import java.util.*;
import java.util.List;

public class CardsHandler {

    private final VirtualServer virtualServer = VirtualServer.getInstance();

    private final VBox blockPool = new VBox();

    private final GridPane cargoGridP1;
    private final StackPane shipboardP1;
    private final StackPane deckPane;
    private final Region currentCardRegion;
    private final Button pickCardButton;

    private ImageView selectedBlock;

    private ImageView currentCard;

    private final List<ImageView> faceDownCardPile = new LinkedList<>();

    private final Map<String, GridPane> tilesGridMap;

    private final Map<GridPane, Integer> gridCellsSize;

    public CardsHandler(GridPane cargoGridP1, StackPane shipboardP1, StackPane deckPane, Region currentCardRegion, Button pickCardButton, Map<String, GridPane> tilesGridMap, Map<GridPane, Integer> gridCellsSize) {
        this.cargoGridP1 = cargoGridP1;
        this.shipboardP1 = shipboardP1;
        this.deckPane = deckPane;
        this.currentCardRegion = currentCardRegion;
        this.pickCardButton = pickCardButton;
        this.tilesGridMap = tilesGridMap;
        this.gridCellsSize = gridCellsSize;

        blockPool.setPadding(new Insets(30));
        blockPool.setSpacing(30);
        blockPool.setAlignment(Pos.TOP_CENTER);
    }

    public void handleCardCommand(String type, String cmd) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        switch (type) {
            case "deck_levels"       -> handleDeckLevels(mapper.readValue(cmd, DeckLevelsMessage.class));
            case "card_picked"       -> handleCardPicked(mapper.readValue(cmd, CardPickedMessage.class));

            case "player_status"     -> handlePlayerStatus(mapper.readValue(cmd, StatusUpdateMessage.class));
            case "card_status"       -> handleCardStatus(mapper.readValue(cmd, StatusUpdateMessage.class));

            case "planet_selection"  -> handlePlanetSelection(mapper.readValue(cmd, PlanetSelectionMessage.class));
            case "cargo_decision"    -> handleCargoDecision(mapper.readValue(cmd, CargoDecisionMessage.class));
            case "swap_cargo"        -> handleCargoSwap(mapper.readValue(cmd, CargoSwapMessage.class));
            case "unload_cargo"      -> handleCargoUnload(mapper.readValue(cmd, CargoUnloadMessage.class));

            case "dice_throw"        -> handleDiceThrow(mapper.readValue(cmd, DiceThrowMessage.class));
            case "projectile"        -> handleProjectile(mapper.readValue(cmd, ProjectileMessage.class));
            case "battery_decision"  -> handleBatteryDecision(mapper.readValue(cmd, BatteryDecisionMessage.class));

            case "card_decision"     -> handleCardDecision(mapper.readValue(cmd, CardDecisionMessage.class));

            case "crew_decision"     -> handleCrewDecision(mapper.readValue(cmd, CrewMessage.class));

            case "cannon_selection"  -> handleCannonSelection(mapper.readValue(cmd, TileSelectionMessage.class));
            case "engine_selection"  -> handleEngineSelection(mapper.readValue(cmd, TileSelectionMessage.class));

            case "decision_end"      -> FlightAnim.closeCurrentPopup();

            default                  -> Logger.logError("Unrecognized command: " + type);
        }
    }

    private void handleDeckLevels(DeckLevelsMessage message) {

        List<Integer> deckLevels = message.getDeckLevels().reversed();
        for(Integer cardLevel : deckLevels) {
            if (cardLevel < 1 || cardLevel > 2) {
                throw new IllegalStateException("Invalid card level: " + cardLevel);
            }
            ImageView imageView = FlightAnim.getFaceDownCard(cardLevel);
            deckPane.getChildren().add(imageView);

            faceDownCardPile.addFirst(imageView);
        }
    }

    private void handleCardPicked(CardPickedMessage message) {
        pickCardButton.setDisable(true);
        if(faceDownCardPile.isEmpty()) {
            Logger.logError("There are no more cards.");
            return;
        }
        if(currentCard != null) FlightAnim.playCardRemove(currentCard);

        currentCard = faceDownCardPile.getFirst();
        faceDownCardPile.removeFirst();

        String path = "/textures/cards/card_level_" + message.getCardLevel() + "_" + message.getCardId() + ".png";
        Image frontFace = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
        FlightAnim.queueCardTaken(currentCard, deckPane, currentCardRegion, frontFace);
    }

    private final Map<BlockType, Image> blockMap = Map.of(
            BlockType.RED, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/textures/sprites/box_red.png"))),
            BlockType.YELLOW, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/textures/sprites/box_yellow.png"))),
            BlockType.GREEN, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/textures/sprites/box_green.png"))),
            BlockType.BLUE, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/textures/sprites/box_blue.png")))
    );

    private void handlePlanetSelection(PlanetSelectionMessage message) {

        boolean isSelf = ClientSession.isSelf(message.getPlayerName());

        List<String> planetChosenBy = message.getPlanetChosenBy();

        if(isSelf) {
            FlightAnim.queuePlanetsPopup(currentCard, planetChosenBy);
        } else {
            FlightAnim.queueUpdateStatus(LanguageManager.get("status.player_choosing_planet", message.getPlayerName()), 500);
        }
    }

    public void updateCargoPane(GridPane cargoGrid, Set<ShipBoard.CargoData> cargo, double gridSize) {
        cargoGrid.getChildren().clear();

        for (ShipBoard.CargoData tileData : cargo) {
            StackPane tile = new StackPane();

            for (int i = 0; i < tileData.capacity(); i++) {
                if (tileData.cargo()[i] != null) {
                    ImageView block = getDisplayBlock(tileData.cargo()[i], gridSize / 3);
                    Point offset = getOffsetForSlot(tileData.capacity(), i);
                    block.setTranslateX(offset.x * gridSize / gridCellsSize.get(cargoGridP1));
                    block.setTranslateY(offset.y * gridSize / gridCellsSize.get(cargoGridP1));
                    block.setRotate(-tileData.rotation() * 90);
                    block.setMouseTransparent(true);
                    tile.getChildren().add(block);
                }
            }
            tile.setRotate(tileData.rotation() * 90);

            cargoGrid.add(tile, tileData.x(), tileData.y());
        }
    }

    private void handleCargoDecision(CargoDecisionMessage message) {

        boolean isSelf = ClientSession.isSelf(message.getPlayerName());

        if (!isSelf) {
            FlightAnim.queueUpdateStatus(LanguageManager.get("status.player_deciding_cargo", message.getPlayerName()), 0);
            return;
        }
        updateCargoPool(message);
        cargoGridP1.getChildren().clear();
        renderInteractableCargoContent(message);
        cargoGridP1.setMouseTransparent(false);
        FlightAnim.queueCargoDecisionPopup(shipboardP1, blockPool);
    }

    private void renderInteractableCargoContent(CargoDecisionMessage message) {
        cargoGridP1.getChildren().clear();

        for (ShipBoard.CargoData tileData : message.getCargoTiles()) {
            StackPane tileSlot = new StackPane();

            for (int i = 0; i < tileData.capacity(); i++) {
                StackPane slot = getSlot(tileData, i);
                if (tileData.cargo()[i] != null) {
                    ImageView block = getBlock(tileData.cargo()[i]);
                    block.setMouseTransparent(true);
                    slot.getChildren().add(block);
                }
                tileSlot.getChildren().add(slot);
            }
            tileSlot.setRotate(tileData.rotation() * 90);

            cargoGridP1.add(tileSlot, tileData.x(), tileData.y());
        }
    }

    private void updateCargoPool(CargoDecisionMessage message) {
        blockPool.getChildren().clear();
        for (BlockType poolBlock : message.getCargoPool()) {
            if (poolBlock == null) continue;
            ImageView block = getBlock(poolBlock);
            block.setScaleX(2);
            block.setScaleY(2);
            blockPool.getChildren().add(block);
        }
    }

    private StackPane getSlot(ShipBoard.CargoData tileData, int i) {
        StackPane slot = new StackPane();
        Point offset = getOffsetForSlot(tileData.capacity(), i);
        slot.setTranslateX(offset.x);
        slot.setTranslateY(offset.y);
        slot.setPrefSize(24, 24);
        slot.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        slot.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        slot.setRotate(-tileData.rotation() * 90);
        slot.setCursor(Cursor.HAND);
        slot.setOnMouseClicked(event -> clickedSlot((StackPane) event.getSource()));
        return slot;
    }

    private ImageView getBlock(BlockType poolBlock) {
        ImageView block = getDisplayBlock(poolBlock, 24);
        block.setCursor(Cursor.HAND);
        block.setOnMouseClicked(event -> selectBlock((ImageView) event.getSource()));
        return block;
    }

    private ImageView getDisplayBlock(BlockType poolBlock, double size) {
        ImageView block = new ImageView(blockMap.get(poolBlock));
        block.setFitHeight(size);
        block.setFitWidth(size);
        block.setCache(true);
        return block;
    }

    private void selectBlock(ImageView block) {
        if(block == selectedBlock) {
            unselectBlock();
            return;
        }
        if(selectedBlock != null) {
            unselectBlock();
        }
        selectedBlock = block;
        markSelectedBlock(selectedBlock);
    }

    private void clickedSlot(StackPane slot) {

        StackPane tileSlot = (StackPane) slot.getParent();
        int colIndex = GridPane.getColumnIndex(tileSlot) != null ? GridPane.getColumnIndex(tileSlot) : 0;
        int rowIndex = GridPane.getRowIndex(tileSlot) != null ? GridPane.getRowIndex(tileSlot) : 0;
        int containerIndex = tileSlot.getChildren().indexOf(slot);

        if(selectedBlock != null) {
            selectedBlock.setEffect(null);
            int blockIndex = blockPool.getChildren().indexOf(selectedBlock);
            selectedBlock = null;
            virtualServer.sendMessage(new CargoSwapMessage(null, blockIndex, new Point(colIndex, rowIndex), containerIndex));
        } else {
            if(slot.getChildren().isEmpty()) return;
            virtualServer.sendMessage(new CargoUnloadMessage(null, new Point(colIndex, rowIndex), containerIndex));
        }
    }

    private void markSelectedBlock(ImageView block) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setBlurType(BlurType.THREE_PASS_BOX);
        dropShadow.setColor(new Color(1, 1, 1, .8));
        dropShadow.setWidth(1);
        dropShadow.setHeight(1);
        dropShadow.setRadius(10);
        dropShadow.setSpread(.8);
        block.setEffect(dropShadow);

        ScaleTransition scale = new ScaleTransition(Duration.millis(200), block);
        scale.setToX(2.2);
        scale.setToY(2.2);
        scale.play();
    }

    private void unselectBlock() {

        selectedBlock.setEffect(null);

        ScaleTransition scale = new ScaleTransition(Duration.millis(200), selectedBlock);
        scale.setToX(2);
        scale.setToY(2);
        scale.play();

        selectedBlock = null;
    }

    private Point getOffsetForSlot(int capacity, int index) {
        return switch (capacity) {
            case 1 -> new Point(0, 0);
            case 2 -> new Point(0, index == 0 ? -12 : 12);
            case 3 -> switch (index) {
                case 0 -> new Point(-12, 0);
                case 1 -> new Point(12, -12);
                case 2 -> new Point(12, 12);
                default -> throw new IllegalArgumentException("Invalid index for capacity 3: " + index);
            };
            default -> throw new IllegalArgumentException("Unsupported capacity: " + capacity);
        };
    }

    private void handleCargoSwap(CargoSwapMessage message) {
        if(!ClientSession.isSelf(message.getPlayerName())) {
            Logger.logWarning("Only the player loading the cargo should receive this message: " + message.getPlayerName());
            return;
        }
        ImageView loadedBlock = (ImageView) blockPool.getChildren().get(message.getBlockIndex());

        StackPane slot = getGridPaneSlot(message.getTileCoords().x, message.getTileCoords().y, message.getContainerIndex(), cargoGridP1);
        if (slot == null) return;

        moveBlockToPool(slot);

        loadedBlock.setMouseTransparent(true);
        loadedBlock.setScaleX(1);
        loadedBlock.setScaleY(1);
        slot.getChildren().add(loadedBlock);
    }

    private void handleCargoUnload(CargoUnloadMessage message) {
        if(!ClientSession.isSelf(message.getPlayerName())) {
            Logger.logWarning("Only the player loading the cargo should receive this message: " + message.getPlayerName());
            return;
        }

        StackPane slot = getGridPaneSlot(message.getTileCoords().x, message.getTileCoords().y, message.getContainerIndex(), cargoGridP1);
        if (slot == null) return;

        moveBlockToPool(slot);
    }

    private void handleDiceThrow(DiceThrowMessage message) {
        FlightAnim.queueDiceThrowPopup(message.getPlayerName(), message.getDice());
    }

    private void handleProjectile(ProjectileMessage m) {
        GridPane shipGrid = tilesGridMap.get(m.getPlayerName());
        StackPane shipPane = (StackPane) shipGrid.getParent();
        int cellSize = gridCellsSize.get(shipGrid);
        String color = ClientSession.getPlayerInfo(m.getPlayerName()).getColor();

        ImageView projectile = getProjectileImageView(m, cellSize, shipPane);

        FlightAnim.queueProjectile(projectile, m.getSource(), m.getPathIndex(), m.getHitPoint(), m.isDefended(), m.getTiles(), shipGrid, color, cellSize);
    }

    private ImageView getProjectileImageView(ProjectileMessage message, int cellSize, StackPane shipPane) {
        String projectileType = message.getProjectileType().toString().toLowerCase();
        String size = message.getSize().toString().toLowerCase();
        String path = "/textures/sprites/" + projectileType + "_" + size + ".png";

        ImageView projectile = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
        projectile.setOpacity(0);
        projectile.setFitHeight(cellSize);
        projectile.setFitWidth(cellSize);
        projectile.setRotate(message.getSource().toInt() * 90);
        shipPane.getChildren().add(projectile);
        return projectile;
    }

    private void moveBlockToPool(StackPane slot) {
        if(!slot.getChildren().isEmpty()) {
            ImageView removedBlock = (ImageView) slot.getChildren().getFirst();
            removedBlock.setMouseTransparent(false);
            removedBlock.setScaleX(2);
            removedBlock.setScaleY(2);
            blockPool.getChildren().add(removedBlock);
        }
    }

    private static StackPane getGridPaneSlot(int x, int y, int containerIndex, GridPane cargoGrid) {
        StackPane tileSlot = null;
        for (Node node : cargoGrid.getChildren()) {
            Integer col = GridPane.getColumnIndex(node);
            Integer row = GridPane.getRowIndex(node);
            col = (col == null) ? 0 : col;
            row = (row == null) ? 0 : row;

            if (col == x && row == y) {
                tileSlot = (StackPane) node;
                break;
            }
        }
        if(tileSlot == null || tileSlot.getChildren().isEmpty()) {
            Logger.logError("Can't find slot or block.");
            return null;
        }

        return (StackPane) tileSlot.getChildren().get(containerIndex);
    }

    private void handleBatteryDecision(BatteryDecisionMessage message) {

        if (ClientSession.isSelf(message.getPlayerName())) {
            FlightAnim.queueBatteryDecisionPopup(message.getBatteries(), message.getHitPoint());
        } else {
            FlightAnim.queueUpdateStatus(LanguageManager.get("status.player_deciding_battery", message.getPlayerName()), 500);
        }
    }

    private void handleCardDecision(CardDecisionMessage message) {

        if(ClientSession.isSelf(message.getPlayerName())) {
            FlightAnim.queueCardDecisionPopup(currentCard);
        } else {
            FlightAnim.queueUpdateStatus(LanguageManager.get("status.player_deciding", message.getPlayerName()), 500);
        }
    }

    private void handlePlayerStatus(StatusUpdateMessage message) {
        String status = message.getStatus().toLowerCase();
        if(ClientSession.isSelf(message.getPlayerName())) {
            FlightAnim.queueUpdateStatus(LanguageManager.get("status." + status), 1500);
        } else {
            FlightAnim.queueUpdateStatus(LanguageManager.get("status.player_" + status, message.getPlayerName()), 1500);
        }
    }
    private void handleCardStatus(StatusUpdateMessage message) {
        String status = message.getStatus().toLowerCase();
        FlightAnim.queueUpdateStatus(LanguageManager.get("status." + status), 1500);
    }

    private void handleCrewDecision(CrewMessage message) {

        if (ClientSession.isSelf(message.getPlayerName())) {
            FlightAnim.queueCrewDecisionPopup(message.getCrew());
        } else {
            FlightAnim.queueUpdateStatus(LanguageManager.get("status.player_deciding_crew", message.getPlayerName()), 500);
        }
    }

    private void handleCannonSelection(TileSelectionMessage m) {

        if(m.getOpenSpace()) Logger.logWarning("Open space card shouldn't send cannon messages.");

        if (ClientSession.isSelf(m.getPlayerName())) {
            FlightAnim.queueTileSelectionPopup("cannons", m.getMinPower(), m.getBatteries(), m.getVerticalTiles(), m.getRotatedTiles(), m.getOpenSpace(), m.getHasAlien());
        } else {
            FlightAnim.queueUpdateStatus(LanguageManager.get("status.player_deciding_cannons", m.getPlayerName()), 500);
        }
    }

    private void handleEngineSelection(TileSelectionMessage message) {

        if (ClientSession.isSelf(message.getPlayerName())) {
            FlightAnim.queueTileSelectionPopup("engines", message.getMinPower(), message.getBatteries(), message.getVerticalTiles(), null, message.getOpenSpace(), message.getHasAlien());
        } else {
            FlightAnim.queueUpdateStatus(LanguageManager.get("status.player_deciding_engines", message.getPlayerName()), 500);
        }
    }

}
