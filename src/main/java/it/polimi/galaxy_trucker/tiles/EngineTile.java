package it.polimi.galaxy_trucker.tiles;

public class EngineTile extends Tile {

    public EngineTile(int ID, Connector top, Connector right, Connector bottom, Connector left) {
        super(ID, top, right, bottom, left);
    }
    public EngineTile(int ID, int top, int right, int bottom, int left) {
        super(ID, top, right, bottom, left);
    }

    @Override
    public void setRotation(int rotation) {
        if(rotation != 0)  throw new IllegalArgumentException("Engine tiles cannot be rotated");
    }

    public int getStrength() {
        return 1;
    }

    @Override
    public String getType() {
        return "Engine";
    }
}
