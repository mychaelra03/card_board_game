package it.polimi.galaxy_trucker.tiles;

public class EngineTile extends Tile {

    private final boolean isDouble;
    private boolean isPowered;

    public EngineTile(int ID, Connector top, Connector right, Connector bottom, Connector left, boolean isDouble) {
        super(ID, top, right, bottom, left);
        this.isDouble = isDouble;
        this.isPowered = false;

    }
    public EngineTile(int ID, int top, int right, int bottom, int left, boolean isDouble) {
        super(ID, top, right, bottom, left);
        this.isDouble = isDouble;
        this.isPowered = false;
    }

    @Override
    public void setRotation(int rotation) {
        // TODO: Remove this if the checks are done after the ship is built.
        if(rotation != 0)  throw new IllegalArgumentException("Engine tiles cannot be rotated");
    }

    public boolean isDouble() {
        return isDouble;
    }
    public void setPowered(boolean isPowered) {
        this.isPowered = isPowered;
    }
    public boolean isPowered() {
        return isPowered;
    }

    public double getStrength() {
        if(isDouble) {
            return(isPowered)? 2 : 0;
        }
        return 1;
    }

    @Override
    public String getTileType() {
        return (isDouble)? "Double Engine" : "Engine";
    }
}
