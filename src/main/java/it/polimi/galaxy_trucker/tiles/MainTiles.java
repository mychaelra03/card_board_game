package it.polimi.galaxy_trucker.tiles;

import it.polimi.galaxy_trucker.ShipBoard;

import static it.polimi.galaxy_trucker.tiles.Connector.*;
import static it.polimi.galaxy_trucker.tiles.AlienSupportTile.AlienColor.*;
import static it.polimi.galaxy_trucker.tiles.BlockType.*;
import static it.polimi.galaxy_trucker.tiles.CargoTile.*;
import static it.polimi.galaxy_trucker.tiles.CabinTile.CrewType.*;


public class MainTiles {
    public static void main(String[] args) {

        AlienSupportTile alienB = new AlienSupportTile(0, 0, 1, 2, 3, PURPLE);
        AlienSupportTile alienB2 = new AlienSupportTile(1, SINGLE, SINGLE, SINGLE, SINGLE, BROWN);
        AlienSupportTile alienP = new AlienSupportTile(2, SINGLE, SINGLE, SINGLE, SINGLE, PURPLE);

        CargoTile blueCargo = new CargoTile(3, UNIVERSAL, DOUBLE, NONE, SINGLE, CargoType.NORMAL, 2);
        CargoTile redCargo = new CargoTile(4, SINGLE, DOUBLE, NONE, SINGLE, CargoType.RED, 2);
        EngineTile engine = new EngineTile(5, DOUBLE, NONE, SINGLE, SINGLE);
        EngineTile doubleEngine = new DoubleEngineTile(12, DOUBLE, NONE, SINGLE, SINGLE);
        ShieldTile shield = new ShieldTile(6, NONE, NONE, SINGLE, NONE);
        BatteriesTile battery = new BatteriesTile(7, NONE, NONE, NONE, NONE, 2);
        CabinTile cabin = new CabinTile(8, NONE, NONE, NONE, NONE);

        System.out.println(alienB + "\n" + alienP + "\n" + engine + "\n" + shield);

        alienB.printConnectors();

        battery.addBattery();
        battery.addBattery();
        System.out.println(battery.getStoredBatteries());

        blueCargo.addOrSwitchBlock(0, YELLOW);
        blueCargo.addOrSwitchBlock(1, GREEN);
        redCargo.addOrSwitchBlock(1, RED);

        System.out.println(blueCargo.getBlockAt(0).toString() + blueCargo.getBlockAt(1));
        cabin.allowBrownAlien(true);
        cabin.setCrewType(BROWN_ALIEN);
        System.out.println(cabin.getHumanCount());
        System.out.println(cabin.hasBrownAlien());
        cabin.setCrewType(HUMAN);
        System.out.println(cabin.getHumanCount());
        cabin.removeMember(1);
        System.out.println(cabin.getHumanCount());
        System.out.println(cabin.getID());
        System.out.println("blue cargo value: " + blueCargo.getHoldsValue());


        StructuralTile structuralModule = new StructuralTile(7, 0, 1, 2, 3);
        structuralModule.printConnectors();
        structuralModule.setRotation(1);
        structuralModule.printConnectors();
        structuralModule.setRotation(2);
        structuralModule.printConnectors();
        structuralModule.setRotation(3);
        structuralModule.printConnectors();
        engine.setRotation(0);

        ShipBoard ship = new ShipBoard(2);
        System.out.println(ship.isInsideShip(0, 0));
        System.out.println(ship.getTile(2, 2));

        ship.setTile(2, 2, cabin, 0);
        System.out.println(ship.getTile(2, 2).getType());


    }
}
