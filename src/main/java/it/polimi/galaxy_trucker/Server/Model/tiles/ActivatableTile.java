package it.polimi.galaxy_trucker.Server.Model.tiles;

public abstract class ActivatableTile extends Tile {

    private final boolean isDouble;

    public ActivatableTile(int id,Connector[] connectors, boolean isDouble) {
        super(id, connectors);
        this.isDouble = isDouble;
    }

    public boolean isDouble() {
        return isDouble;
    }
}