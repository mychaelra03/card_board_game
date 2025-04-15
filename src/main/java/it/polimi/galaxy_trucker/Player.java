package it.polimi.galaxy_trucker;

import it.polimi.galaxy_trucker.Server.Model.cards.projectile.Meteor;
import it.polimi.galaxy_trucker.Server.Model.cards.projectile.Projectile;
import it.polimi.galaxy_trucker.Server.Model.tiles.*;

import java.awt.*;
import java.util.*;
import java.util.List;

import static it.polimi.galaxy_trucker.Server.Model.cards.projectile.Projectile.Size.*;

public class Player {

    public enum Color{
        BLUE, RED, YELLOW, GREEN
    }

    private final String name;
    private final int[] profilePictureIds;
    private Color color;
    private ShipBoard shipBoard;
    private int credits;
    private int position;
    private boolean isEliminated;

    private List<BlockType> cargoPool;

    public Player(String name, int[] profilePictureIds) {
        this.name = name;
        this.profilePictureIds = profilePictureIds;
        this.color = null;
        this.shipBoard = null;
        this.credits = 0;
        this.position = 0;
        this.isEliminated = false;
    }

    // GETTERS AND SETTERS
    public String getName() {
        return name;
    }
    public int[] getProfilePictureIds() {
        return profilePictureIds;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public Color getColor() {
        return color;
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
    public void setEliminated(boolean eliminated) {
        this.isEliminated = eliminated;
    }
    public boolean isEliminated() {
        return isEliminated;
    }
    public List<BlockType> getCargoPool() {
        return cargoPool;
    }
    public void setCargoPool(List<BlockType> cargoPool) {
        this.cargoPool = cargoPool;
    }

    // DELEGATE METHODS
    public int getCrewCount() {
        return shipBoard.getTotalCrewCount();
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
    public double getStrength(Class<? extends ActivatableTile> tileClass, int doubleVertical, int doubleRotated) {
        return shipBoard.getStrength(tileClass, doubleVertical, doubleRotated);
    }
    public double getStrength(Class<? extends ActivatableTile> tileClass, int doubleVertical) {
        return shipBoard.getStrength(tileClass, doubleVertical, 0);
    }
    public boolean removeBatteryFrom(int x, int y) {
        return shipBoard.removeBatteryFrom(x, y);
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
    public BlockType loadCargoAt(int x, int y, int index, BlockType blockType) {
        return shipBoard.loadCargoAt(x, y, index, blockType);
    }

    // NEW METHODS
    public void removeCrew(int amount) {
        int crewToRemove = amount;
        while(crewToRemove > 0 && getCrewCount() > 0) {
            List<Point> cabinsPositions = shipBoard.getTilesPositions(CabinTile.class, tile -> ((CabinTile)tile).hasCrew()); // TODO: to client

            int chosenCabin = 0;            // TODO: from javafx
            if(shipBoard.removeOneCrewMemberAt(cabinsPositions.get(chosenCabin).x, cabinsPositions.get(chosenCabin).y) != null) crewToRemove--;
        }
    }

    public void swapCargo(int blockIndex, Point tileCoords, int containerIndex) {
        if(cargoPool == null) throw new RuntimeException("cargoPool has not been initialized.");
        if(blockIndex < 0 || blockIndex >= cargoPool.size()) throw new IllegalArgumentException("Invalid block index.");

        BlockType selectedBlock = cargoPool.get(blockIndex);
        BlockType replaced = loadCargoAt(tileCoords.x, tileCoords.y, containerIndex, selectedBlock);

        cargoPool.remove(blockIndex);
        if (replaced != null) {
            cargoPool.add(replaced);
        }
    }

    public void clearCargoPool() {
        this.cargoPool = null;
    }

    public void checkIfAlive() {
        if(shipBoard.getTotalHumanCount() == 0) setEliminated(true);
        // TODO
    }
}