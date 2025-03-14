package it.polimi.galaxy_trucker.tiles;

public class DoubleEngineTile extends EngineTile {

    private boolean powered = false;

    public DoubleEngineTile(int ID, Connector top, Connector right, Connector bottom, Connector left) {
        super(ID, top, right, bottom, left);
    }
    public DoubleEngineTile(int ID, int top, int right, int bottom, int left) {
        super(ID, top, right, bottom, left);
    }

    public boolean isPowered() {
        return powered;
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    @Override
    public int getStrength() {
        return isPowered()? 2 : 0;
    }

    @Override
    public String getType() {
        return "Double Engine";
    }
}
