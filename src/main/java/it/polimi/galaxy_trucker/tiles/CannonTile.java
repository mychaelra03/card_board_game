package it.polimi.galaxy_trucker.tiles;

public class CannonTile extends Tile {

    private final boolean isDouble;
    private boolean isPowered;

    public CannonTile(int ID, Connector top, Connector right, Connector bottom, Connector left, boolean isDouble) {
        super(ID, top, right, bottom, left);
        this.isDouble = isDouble;
        this.isPowered = false;
    }
    public CannonTile(int ID, int top, int right, int bottom, int left, boolean isDouble) {
        super(ID, top, right, bottom, left);
        this.isDouble = isDouble;
        this.isPowered = false;
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
            if(!isPowered) return 0;
            return (getRotation() == 0) ? 2 : 1;
        }
        return (getRotation() == 0) ? 1 : 0.5;
    }

    @Override
    public String getTileType() {
        return (isDouble)? "Double Cannon" : "Cannon";
    }
}
