package it.polimi.galaxy_trucker.tiles;

public class StructuralTile extends Tile {

    public StructuralTile(int ID, Connector top, Connector right, Connector bottom, Connector left) {
        super(ID, top, right, bottom, left);
    }
    public StructuralTile(int ID, int top, int right, int bottom, int left) {
        super(ID, top, right, bottom, left);
    }

    @Override
    public String getTileType() {
        return "Shield";
    }
}
