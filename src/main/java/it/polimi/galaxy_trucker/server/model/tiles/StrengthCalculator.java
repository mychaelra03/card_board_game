package it.polimi.galaxy_trucker.server.model.tiles;

import it.polimi.galaxy_trucker.ShipBoard;

import static it.polimi.galaxy_trucker.server.model.tiles.CrewType.*;

public class StrengthCalculator {

    public static double calculateStrength(ShipBoard board, Class<? extends ActivatableTile> tileClass, int doubleVertical, int doubleRotated) {

        validateActivation(board, tileClass, doubleVertical, doubleRotated);

        double strength = 0;

        int singleVertical = board.getTileCount(tileClass, tile -> !((ActivatableTile)tile).isDouble() && tile.getRotation() == 0);
        int singleRotated = board.getTileCount(tileClass, tile -> !((ActivatableTile)tile).isDouble() && tile.getRotation() != 0);

        strength += singleVertical * getStrength(tileClass, false, 0);
        strength += singleRotated * getStrength(tileClass, false, 1);
        strength += doubleVertical * getStrength(tileClass, true, 0);
        strength += doubleRotated * getStrength(tileClass, true, 1);

        if((tileClass == EngineTile.class && board.hasAlien(BROWN_ALIEN) || tileClass == CannonTile.class && board.hasAlien(PURPLE_ALIEN)) && strength > 0) strength += 2;
        return strength;
    }

    private static void validateActivation(ShipBoard board, Class<? extends ActivatableTile> tileClass, int vertical, int rotated) {
        int verticalAvailable = board.getTileCount(tileClass, tile -> ((ActivatableTile) tile).isDouble() && tile.getRotation() == 0);
        int rotatedAvailable = board.getTileCount(tileClass, tile -> ((ActivatableTile) tile).isDouble() && tile.getRotation() != 0);

        if(vertical < 0 || rotated < 0) throw new IllegalArgumentException("Cannot activate a negative number of tiles.");
        if(vertical > verticalAvailable || rotated > rotatedAvailable) throw new IllegalArgumentException("Not enough available tiles.");
        if(vertical + rotated > board.getTotalBatteriesCount()) throw new IllegalArgumentException("Not enough batteries to activate all the tiles.");
    }

    private static double getStrength(Class<? extends ActivatableTile> tileClass, boolean isDouble, int rotation) {
        if(tileClass == CannonTile.class) {
            return CannonTile.getStrength(rotation, isDouble);
        } else if(tileClass == EngineTile.class) {
            return (rotation == 0)? EngineTile.getStrength(0, isDouble) : 0;
        }
        throw new IllegalArgumentException("Unsupported tile type: " + tileClass);
    }
}