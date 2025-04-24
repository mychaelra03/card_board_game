package it.polimi.galaxy_trucker;

import it.polimi.galaxy_trucker.Server.Model.cards.projectile.Projectile;
import it.polimi.galaxy_trucker.Server.Model.tiles.*;

import java.awt.*;
import java.util.List;

public class Player {

    public enum Color {
        BLUE, RED, GREEN, YELLOW
    }
    public enum PlayerState {
        IDLE,
        BUILDING_SHIP,
        RESOLVING_CARD,
        ELIMINATED
    }

    private final String name;
    private final int[] profilePictureIds;
    private Color color;
    private ShipBoard shipBoard;
    private int credits;
    private int position;
    private PlayerState playerState;
    private boolean admin;

    private List<BlockType> cargoPool;

    private int chosenPlanet;

    public Player(String name, int[] profilePictureIds) {
        this.name = name;
        this.profilePictureIds = profilePictureIds;
        this.color = null;
        this.shipBoard = null;
        this.credits = 0;
        this.position = 0;
        this.admin = false;
    }
    public Player(String name, int[] profilePictureIds, ShipBoard shipBoard) {
        this(name, profilePictureIds);
        this.shipBoard = shipBoard;
    }

    // GETTERS AND SETTERS
    public String getName() {
        return name;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public String getColor() {
        return color.toString();
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
    protected void setPosition(int position) {
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
    public int getCargoHoldsValue() {
        return shipBoard.getTotalHoldsValue();
    }

    // DELEGATE METHODS

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

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    // NEW METHODS
    public void ensureCrewRemovedAt(Point cabinCoords, int index) {
        CrewType removedMember = shipBoard.removeCrewMemberAt(cabinCoords.x, cabinCoords.y, index);
        if(removedMember == null) throw new IllegalArgumentException("Cabin tile does not have a crew member at index " + index + ".");
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

}