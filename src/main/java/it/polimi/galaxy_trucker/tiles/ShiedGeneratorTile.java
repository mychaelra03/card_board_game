package it.polimi.galaxy_trucker.tiles;

class ShiedGeneratorTile extends Tile {

    public ShiedGeneratorTile(Connector top, Connector right, Connector bottom, Connector left) {
        super(top, right, bottom, left);
    }
    public ShiedGeneratorTile(int top, int right, int bottom, int left) {
        super(top, right, bottom, left);
    }

    @Override
    public String getType() {
        return "Shield";
    }
}
