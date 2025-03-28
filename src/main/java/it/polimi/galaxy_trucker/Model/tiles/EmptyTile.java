package it.polimi.galaxy_trucker.Model.tiles;

public class EmptyTile extends Tile {

    public EmptyTile() {
        super(0, null);
    }

    @Override
    public String getTileType() {
        return "Empty";
    }
}
