package it.polimi.galaxy_trucker.tiles;

public class StructuralModuleTile extends Tile {

    public StructuralModuleTile(int ID, Connector top, Connector right, Connector bottom, Connector left) {
        super(ID, top, right, bottom, left);
    }
    public StructuralModuleTile(int ID, int top, int right, int bottom, int left) {
        super(ID, top, right, bottom, left);
    }

    @Override
    public String getType() {
        return "Shield";
    }
}
