package it.polimi.galaxy_trucker.tiles;

class StructuralModuleTile extends Tile {

    public StructuralModuleTile(Connector top, Connector right, Connector bottom, Connector left) {
        super(top, right, bottom, left);
    }
    public StructuralModuleTile(int top, int right, int bottom, int left) {
        super(top, right, bottom, left);
    }

    @Override
    public String getType() {
        return "Shield";
    }
}
