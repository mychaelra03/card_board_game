package it.polimi.galaxy_trucker.tiles;

public class DoubleCannonTile extends CannonTile {

    private boolean powered = false;

    public DoubleCannonTile(int ID, Connector top, Connector right, Connector bottom, Connector left) {
        super(ID, top, right, bottom, left);
    }
    public DoubleCannonTile(int ID, int top, int right, int bottom, int left) {
        super(ID, top, right, bottom, left);
    }

    public boolean isPowered() {
        return powered;
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    @Override
    public double getStrength() {
        if(isPowered()) {
            return (getRotation() == 0)? 2 : 1;
        }
        return 0;
    }

    @Override
    public String getType() {
        return "Double Cannon";
    }
}
