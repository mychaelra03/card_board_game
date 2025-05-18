package it.polimi.galaxy_trucker.server.model;

import it.polimi.galaxy_trucker.common.messages.flight.TileInfo;
import it.polimi.galaxy_trucker.server.model.cards.projectile.Projectile;
import it.polimi.galaxy_trucker.server.model.tiles.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static it.polimi.galaxy_trucker.server.model.Player.PlayerState.*;

public class Player {

    public enum Color {
        BLUE, RED, GREEN, YELLOW
    }
    public enum PlayerState {
        IDLE,
        READY_TO_PLAY,
        BUILDING_SHIP,
        DONE_BUILDING,
        RESOLVING_CARD,
        ELIMINATED
    }

    private final String username;
    private final int[] profilePictureIds;
    private Color color;
    private ShipBoard shipBoard;
    private int credits;
    private int position;

    private PlayerState playerState = IDLE;
    private final Integer[] reservedTiles;
    private boolean admin;

    private List<BlockType> cargoPool;

    public Player(String username, int[] profilePictureIds) {
        this.username = username;
        this.profilePictureIds = profilePictureIds;
        this.color = null;
        this.shipBoard = null;
        this.credits = 0;
        this.position = 0;
        this.reservedTiles = new Integer[2];
        Arrays.fill(reservedTiles, null);
        this.admin = false;
    }
    public Player(String username, int[] profilePictureIds, ShipBoard shipBoard) {
        this(username, profilePictureIds);
        this.shipBoard = shipBoard;
    }

    // GETTERS AND SETTERS
    public String getName() {
        return username;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public Color getColor() {
        return color;
    }
    public int[] getProfilePictureIds() {
        return profilePictureIds;
    }
    public void setNewShipBoard(BoardType shipType) {
        this.shipBoard = new ShipBoard(shipType);
    }
    public ShipBoard getShipBoard() {
        return shipBoard;
    }
    public void giveCredits(int credits) {
        this.credits += credits;
    }
    public int getCredits() {
        return credits;
    }
    public void setPosition(int position) {
        this.position = position;
    }
    public int getPosition() {
        return position;
    }
    public PlayerState getState() {
        return playerState;
    }
    public void setState(PlayerState state) {
        this.playerState = state;
    }
    public List<BlockType> getCargoPool() {
        return cargoPool;
    }
    public void setCargoPool(List<BlockType> cargoPool) {
        this.cargoPool = cargoPool;
    }

    // DELEGATE METHODS

    public void setTile(int x, int y, int tileId, int rotation) {
        shipBoard.setTile(x, y, tileId, rotation);
    }
    public void removeTile(int x, int y) {
        shipBoard.removeTile(x, y);
    }
    public int getTotalTileCount() {
        return shipBoard.getTotalTileCount();
    }
    public int getCrewCount() {
        return shipBoard.getTotalCrewCount();
    }
    public int getHumanCount() {
        return shipBoard.getTotalHumanCount();
    }
    public int getExposedConnectorsCount() {
        return shipBoard.getExposedConnectorsCount();
    }
    public void applyEpidemicCard() {
        shipBoard.applyEpidemicCard();
    }
    public void removeMostValuableCargo(int quantity) {
        shipBoard.removeMostValuableCargo(quantity);
    }
    public double getMaxStrength(Class<? extends ActivatableTile> tileClass) {
        return shipBoard.getMaxStrength(tileClass);
    }
    public double getMinStrength(Class<? extends ActivatableTile> tileClass) {
        return shipBoard.getMinStrength(tileClass);
    }
    public double getStrength(Class<? extends ActivatableTile> tileClass, int doubleVertical, int doubleRotated) {
        return shipBoard.getStrength(tileClass, doubleVertical, doubleRotated);
    }
    public double getStrength(Class<? extends ActivatableTile> tileClass, int doubleVertical) {
        return shipBoard.getStrength(tileClass, doubleVertical, 0);
    }
    public void removeBatteries(List<Point> coordinates) {
        shipBoard.removeBatteries(coordinates);
    }
    public Point removeFirstTileInPath(Projectile.Source source, int index) {
        return shipBoard.removeFirstTileInPath(source, index);
    }
    public ShipBoard.DefendingCannonType getDefendingCannonType(Projectile.Source source, int index) {
        return shipBoard.getDefendingCannonType(source, index);
    }
    public boolean isPathEmpty(Projectile.Source source, int index) {
        return shipBoard.isPathEmpty(source, index);
    }
    public boolean isSideShielded(int side) {
        return shipBoard.isSideShielded(side);
    }
    public boolean hasExposedConnectorInPath(Projectile.Source source, int index) {
        return shipBoard.hasExposedConnectorInPath(source, index);
    }
    public int getCargoHoldsValue() {
        return shipBoard.getTotalHoldsValue();
    }
    public void updateConnectivity() {
        shipBoard.updateConnectivity();
    }
    public Set<Point> getInvalidTiles() {
        return shipBoard.getInvalidTiles();
    }
    public ShipBoard.ShipContent getShipContent() {
        return shipBoard.getShipContent();
    }

// NEW METHODS

    public boolean isAdmin() {
        return admin;
    }
    public boolean isReady() {
        return playerState == READY_TO_PLAY;
    }
    public void toggleReady() {
        switch (playerState) {
            case IDLE -> setState(READY_TO_PLAY);
            case READY_TO_PLAY -> setState(IDLE);
            default -> throw new IllegalStateException("Cannot toggle ready state from " + playerState);
        }
    }
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public void reserveTile(int tileId, int index) {
        if(index < 0 || index > 1) throw new IllegalArgumentException("Invalid index: " + index + ".");
        if(reservedTiles[index] != null) throw new IllegalArgumentException("Slot occupied.");
        reservedTiles[index] = tileId;
    }
    public int useReservedTile(int index) {
        if(index < 0 || index > 1) throw new IllegalArgumentException("Invalid index: " + index + ".");
        if(reservedTiles[index] == null) throw new IllegalArgumentException("Slot is empty.");
        int tile = reservedTiles[index];
        reservedTiles[index] = null;
        return tile;
    }
    public Integer getPrivateTile(int index) {
        if(index < 0 || index > 1) throw new IllegalArgumentException("Invalid index: " + index + ".");
        return reservedTiles[index];
    }

    public void ensureCrewRemovedAt(Point cabinCoords) {
        CrewType removedMember = shipBoard.removeCrewMemberAt(cabinCoords.x, cabinCoords.y);
        if(removedMember == null) throw new IllegalArgumentException("Cabin tile does not have any crew members.");
    }

    public void ensureBatteryRemovedFrom(Point batteryCoords) {
        boolean isBatteryRemoved = shipBoard.removeBatteryFrom(batteryCoords.x, batteryCoords.y);
        if(!isBatteryRemoved) throw new IllegalArgumentException("Battery tile does not have any battery.");
    }

    public void swapCargo(int blockIndex, Point tileCoords, int containerIndex) {
        if(cargoPool == null) throw new RuntimeException("cargoPool has not been initialized.");
        if(blockIndex < 0 || blockIndex >= cargoPool.size()) throw new IllegalArgumentException("Invalid block index.");

        BlockType selectedBlock = cargoPool.get(blockIndex);
        BlockType replaced = shipBoard.loadCargoAt(tileCoords.x, tileCoords.y, containerIndex, selectedBlock);

        cargoPool.remove(blockIndex);
        if(replaced != null) cargoPool.add(replaced);
    }

    public void unloadCargo(Point tileCoords, int containerIndex) {
        if(cargoPool == null) throw new RuntimeException("cargoPool has not been initialized.");

        BlockType removed = shipBoard.unloadCargoAt(tileCoords.x, tileCoords.y, containerIndex);

        if(removed != null) cargoPool.add(removed);
    }

    public void clearCargoPool() {
        this.cargoPool = null;
    }


    public List<TileInfo> getSimplifiedShipBoard() {
        List<TileInfo> simplifiedShipBoard = new ArrayList<>();
        Tile[][] tiles = shipBoard.getTiles();
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 7; x++) {
                Tile tile = tiles[y][x];
                if (tile != null && !(tile instanceof EmptyTile)) {
                    simplifiedShipBoard.add(new TileInfo(tile.getId(), tile.getRotation(), x, y));
                }
            }
        }
        return simplifiedShipBoard;
    }

}