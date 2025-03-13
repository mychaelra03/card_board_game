package it.polimi.galaxy_trucker.tiles;

class EngineTile extends Tile {

    public enum EngineType {
        SINGLE, DOUBLE
    }
    private final EngineType type;

    public EngineTile(Connector top, Connector right, Connector bottom, Connector left, EngineType type) {
        super(top, right, bottom, left);
        this.type = type;
    }
    public EngineTile(int top, int right, int bottom, int left, EngineType type) {
        super(top, right, bottom, left);
        this.type = type;
    }

    public EngineType getEngineType() {
        return type;
    }

    @Override
    public String getType() {
        return "Cannon";
    }
}
