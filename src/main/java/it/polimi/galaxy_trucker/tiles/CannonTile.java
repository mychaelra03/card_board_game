package it.polimi.galaxy_trucker.tiles;

class CannonTile extends Tile {

    public enum CannonType {
        SINGLE, DOUBLE;
    }
    private final CannonType type;

    public CannonTile(Connector top, Connector right, Connector bottom, Connector left, CannonType type) {
        super(top, right, bottom, left);
        this.type = type;
    }    public CannonTile(int top, int right, int bottom, int left, CannonType type) {
        super(top, right, bottom, left);
        this.type = type;
    }

    public CannonType getCannonType() {
        return type;
    }

    @Override
    public String getType() {
        return "Cannon";
    }
}
