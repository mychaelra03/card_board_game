package it.polimi.galaxy_trucker.tiles;

import static it.polimi.galaxy_trucker.tiles.Connector.*;
import static it.polimi.galaxy_trucker.tiles.AlienSupportTile.AlienColor.*;
import static it.polimi.galaxy_trucker.tiles.BlockType.*;
import static it.polimi.galaxy_trucker.tiles.EngineTile.*;
import static it.polimi.galaxy_trucker.tiles.CargoTile.*;


public class MainTiles {
    public static void main(String[] args) {

        AlienSupportTile alienB = new AlienSupportTile(0, 1, 2, 3, PURPLE);
        AlienSupportTile alienB2 = new AlienSupportTile(SINGLE, SINGLE, SINGLE, SINGLE, BROWN);
        AlienSupportTile alienP = new AlienSupportTile(SINGLE, SINGLE, SINGLE, SINGLE, PURPLE);

        CargoTile blueCargo = new CargoTile(UNIVERSAL, DOUBLE, NONE, SINGLE, CargoType.NORMAL, 2);
        CargoTile redCargo = new CargoTile(SINGLE, DOUBLE, NONE, SINGLE, CargoType.RED, 2);
        EngineTile engine = new EngineTile(DOUBLE, NONE, SINGLE, SINGLE, EngineType.SINGLE);
        ShiedGeneratorTile shield = new ShiedGeneratorTile(NONE, NONE, SINGLE, NONE);
        BatteryComponentTile battery = new BatteryComponentTile(NONE, NONE, NONE, NONE, 2);

        System.out.println(alienB + "\n" + alienP + "\n" + engine + "\n" + shield);

        alienB.printConnectors();

        battery.addBattery();
        battery.addBattery();
        System.out.println(battery.getStoredBatteries());

        blueCargo.addOrSwitchBlock(0, YELLOW);
        blueCargo.addOrSwitchBlock(1, GREEN);
        blueCargo.addOrSwitchBlock(1, RED);

        System.out.println(blueCargo.getBlockAt(0).toString() + blueCargo.getBlockAt(1));
    }
}
