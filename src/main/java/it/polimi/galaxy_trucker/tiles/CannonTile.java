package it.polimi.galaxy_trucker.tiles;

public class CannonTile extends Tile {

    public CannonTile(int ID, Connector top, Connector right, Connector bottom, Connector left) {
        super(ID, top, right, bottom, left);
    }
    public CannonTile(int ID, int top, int right, int bottom, int left) {
        super(ID, top, right, bottom, left);
    }

    public double getStrength() {
        return (getRotation() == 0)? 1 : 0.5;
    }

    @Override
    public String getType() {
        return "Cannon";
    }
}
