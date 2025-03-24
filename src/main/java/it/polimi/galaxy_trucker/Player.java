package it.polimi.galaxy_trucker;

import it.polimi.galaxy_trucker.cards.AbandonedShipCard;
import it.polimi.galaxy_trucker.tiles.BlockType;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Player {

    public enum Color{
        BLUE, RED, YELLOW, GREEN
    }
    private final String name;
    private final Color color;
    private final ShipBoard shipBoard;
    private int credits;
    private int position;
    private boolean isEliminated;

    public Player(String name, Color color, int shipType) {
        this.name = name;
        this.color = color;
        this.shipBoard = new ShipBoard(shipType, color);
        this.credits = 0;
        this.position = 0;
        this.isEliminated = false;
    }

    // GETTERS AND SETTERS
    public String getName() {
        return name;
    }
    public Color getColor() {
        return color;
    }
    public ShipBoard getShipBoard() {
        return shipBoard;
    }
    public void addCredits(int credits) {
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
    public boolean isEliminated() {
        return isEliminated;
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

    // NEW METHODS
    public boolean acceptsCardEffect() {
        // TODO
        return true;
    }
    public void giveCargoReward(BlockType[] cargo) {
        List<BlockType> cargoList = Arrays.asList(cargo);
        int chosenBlockIndex = 0;
        List<Point> cargoTiles = new ArrayList<>();
        int chosenCargoTileIndex = 0;
        int chosenContainerIndex = 0;
        cargoList.add(shipBoard.loadCargoAt(cargoTiles.get(chosenCargoTileIndex).x,cargoTiles.get(chosenCargoTileIndex).y, chosenContainerIndex, cargoList.get(chosenBlockIndex)));
        // TODO
    }
    public void removeCrew(int amount) {
        // TODO
    }


    // TODO: public void getShot(CannonShot[] cannonShots);
}
