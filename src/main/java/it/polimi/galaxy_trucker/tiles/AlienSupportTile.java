package it.polimi.galaxy_trucker.tiles;

class AlienSupportTile extends Tile {

    public enum AlienColor {
        BROWN, PURPLE
    }
    private final AlienColor color;

    public AlienSupportTile(int ID, Connector top, Connector right, Connector bottom, Connector left, AlienColor color) {
        super(ID, top, right, bottom, left);
        this.color = color;
    }
    public AlienSupportTile(int ID, int top, int right, int bottom, int left, AlienColor color) {
        super(ID, top, right, bottom, left);
        this.color = color;
    }

    public AlienColor getAlienColor() {
        return color;
    }

    @Override
    public String getType() {
        return "Alien Support";
    }

}