package it.polimi.galaxy_trucker.tiles;

public class ShiedGeneratorTile extends Tile {

    public ShiedGeneratorTile(int ID, Connector top, Connector right, Connector bottom, Connector left) {
        super(ID, top, right, bottom, left);
    }
    public ShiedGeneratorTile(int ID, int top, int right, int bottom, int left) {
        super(ID, top, right, bottom, left);
    }

    @Override
    public String getTileType() {
        return "Shield";
    }
}
