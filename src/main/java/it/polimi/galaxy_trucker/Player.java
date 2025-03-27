package it.polimi.galaxy_trucker;

import it.polimi.galaxy_trucker.cards.projectile.*;
import it.polimi.galaxy_trucker.tiles.*;

import java.awt.*;
import java.util.*;
import java.util.List;

import static it.polimi.galaxy_trucker.cards.projectile.Projectile.Size.*;

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
        this.shipBoard = new ShipBoard(shipType);
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
    public double getMaxCannonStrength() {
        return shipBoard.getMaxCannonStrength();
    }
    public double getCurrentStrength(Class<? extends ActivatableTile> tileClass) {
        return shipBoard.getCurrentStrength(tileClass);
    }

    // NEW METHODS
    public boolean acceptsCardEffect() {
        // TODO
        return true;
    }
    public int choosePlanet(boolean[] planetTaken) {
        // TODO
        // selects a valid index between 0 and planetTaken.length (planetTaken.length must be false)
        // -1 to refuse
        return -1;
    }

    public void chooseTilesToActivate(Class<? extends ActivatableTile> tileClass) {
        shipBoard.deactivateAllTiles(tileClass);
        boolean done = false;
        do {
            List<Point> activatableTiles = shipBoard.getTilesPositions(tileClass, tile -> ((ActivatableTile)tile).isDouble());   // TODO: to client
            List<Point> batteries = shipBoard.getTilesPositions(BatteriesTile.class, tile -> true);                       // TODO: to client
            int chosenTileIndex = 0;   // TODO: from javafx
            int chosenBatteryIndex = 0;

            ActivatableTile chosenTileToToggle = (ActivatableTile)shipBoard.getTile(activatableTiles.get(chosenTileIndex).x,activatableTiles.get(chosenTileIndex).y);
            BatteriesTile chosenBatteryToUse = (BatteriesTile)shipBoard.getTile(activatableTiles.get(chosenBatteryIndex).x,activatableTiles.get(chosenBatteryIndex).y);

            if(chosenTileToToggle.isPowered() && !chosenBatteryToUse.isFull()) {
                chosenTileToToggle.setPowered(false);
                chosenBatteryToUse.addBattery();
            } else if(!chosenTileToToggle.isPowered() && chosenBatteryToUse.getStoredBatteries() > 0) {
                chosenBatteryToUse.removeBattery();
                chosenTileToToggle.setPowered(true);
            }
            chosenTileToToggle = null;
            chosenBatteryToUse = null;
            done = true;                    // TODO: from javafx
        } while(!done);
    }

    public void giveCargoReward(BlockType[] cargo) {
        List<BlockType> cargoList = Arrays.asList(cargo);
        boolean done = false;
         do {
            List<Point> cargoPositions = shipBoard.getTilesPositions(CargoTile.class, tile -> true); // TODO: to client
            int chosenBlockToTake = 0;      // TODO: from javafx
            int chosenCargoTileToSet = 0;   // TODO: from javafx
            int chosenContainerToSet = 0;   // TODO: from javafx
            done = true;                    // TODO: from javafx

            // TODO: blocks removal
            cargoList.add(shipBoard.loadCargoAt(cargoPositions.get(chosenCargoTileToSet).x, cargoPositions.get(chosenCargoTileToSet).y, chosenContainerToSet, cargoList.get(chosenBlockToTake)));
            cargoList.remove(chosenBlockToTake);
        } while(!done);
    }

    public void removeCrew(int amount) {
        int crewToRemove = amount;
        while(crewToRemove > 0 && getCrewCount() > 0) {
            List<Point> cabinsPositions = shipBoard.getTilesPositions(CabinTile.class, tile -> ((CabinTile)tile).hasCrew()); // TODO: to client

            int chosenCabin = 0; // TODO: from javafx
            if(shipBoard.removeOneCrewMemberAt(cabinsPositions.get(chosenCabin).x, cabinsPositions.get(chosenCabin).y) != null) crewToRemove--;
        }
    }

    private boolean usedBattery() {
        List<Point> batteries = shipBoard.getTilesPositions(BatteriesTile.class, tile -> ((BatteriesTile) tile).getStoredBatteries() > 0);
        int chosenBatteryTile = 0; // TODO: from javafx (-1 is don't use)
        if (chosenBatteryTile >= 0 && chosenBatteryTile < batteries.size()) {
            Point batteryPos = batteries.get(chosenBatteryTile);
            shipBoard.removeBatteryFrom(batteryPos.x, batteryPos.y);
            return true;
        }
        return false;
    }

    public void applyProjectile(Projectile projectile, int diceSum) {
        if(shipBoard.isRowOrColumnEmpty(projectile.getSource(), diceSum)) return;

        if(projectile.getSize() == SMALL) {
            if(projectile instanceof Meteor) {
                if(!shipBoard.hasExposedConnectorInPath(projectile.getSource(), diceSum)) return;
            }
            if(shipBoard.isSideShielded(projectile.getSource().toInt()) && usedBattery()) return;

        } else if(projectile.getSize() == LARGE && projectile instanceof Meteor) {
            ShipBoard.DefendingCannonType cannonType = shipBoard.getDefendingCannonType(projectile.getSource(), diceSum);
            if(cannonType == ShipBoard.DefendingCannonType.SINGLE) return;
            if(cannonType == ShipBoard.DefendingCannonType.DOUBLE && usedBattery()) return;
        }
        shipBoard.removeFirstTileInPath(projectile.getSource(), diceSum);
    }

    public void checkIfAlive() {
        if(shipBoard.getTotalHumanCount() == 0) isEliminated = true;
        // TODO
    }
}
