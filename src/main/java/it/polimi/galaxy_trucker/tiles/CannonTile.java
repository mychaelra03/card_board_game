package it.polimi.galaxy_trucker.tiles;

class CannonTile extends Tile {

    public enum CannonType {
        SINGLE, DOUBLE;
    }
    private final CannonType type;

    public CannonTile(int ID, Connector top, Connector right, Connector bottom, Connector left, CannonType type) {
        super(ID, top, right, bottom, left);
        this.type = type;
    }
    public CannonTile(int ID, int top, int right, int bottom, int left, CannonType type) {
        super(ID, top, right, bottom, left);
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
