package it.polimi.galaxy_trucker.tiles;

public class ShieldTile extends Tile {

    public ShieldTile(int ID, Connector top, Connector right, Connector bottom, Connector left) {
        super(ID, top, right, bottom, left);
    }
    public ShieldTile(int ID, int top, int right, int bottom, int left) {
        super(ID, top, right, bottom, left);
    }

    @Override
    public String getType() {
        return "Shield";
    }
}
