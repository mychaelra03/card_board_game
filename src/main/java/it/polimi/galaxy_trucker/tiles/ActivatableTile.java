package it.polimi.galaxy_trucker.tiles;

public abstract class ActivatableTile extends Tile {

    private final boolean isDouble;
    private boolean isPowered;

    public ActivatableTile(int id,Connector[] connectors, boolean isDouble) {
        super(id, connectors);
        this.isDouble = isDouble;
        this.isPowered = false;
    }

    public boolean isDouble() {
        return isDouble;
    }
    public void setPowered(boolean powered) {
        this.isPowered = powered;
    }
    public boolean isPowered() {
        return isPowered;
    }

    public abstract double getStrength();
}