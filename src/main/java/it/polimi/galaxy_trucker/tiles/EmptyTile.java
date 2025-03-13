package it.polimi.galaxy_trucker.tiles;

public class EmptyTile extends Tile {

    public EmptyTile() {
        super(0, null, null, null, null);
    }

    @Override
    public String getType() {
        return "Empty";
    }
}
