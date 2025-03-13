package it.polimi.galaxy_trucker.tiles;

public class EngineTile extends Tile {

    public enum EngineType {
        SINGLE, DOUBLE
    }
    private final EngineType type;

    public EngineTile(int ID, Connector top, Connector right, Connector bottom, Connector left, EngineType type) {
        super(ID, top, right, bottom, left);
        this.type = type;
    }
    public EngineTile(int ID, int top, int right, int bottom, int left, EngineType type) {
        super(ID, top, right, bottom, left);
        this.type = type;
    }

    public EngineType getEngineType() {
        return type;
    }

    @Override
    public void setRotation(int rotation) {
        if(rotation != 0)  throw new IllegalArgumentException("Engine tiles cannot be rotated");
    }

    @Override
    public String getType() {
        return "Cannon";
    }
}
