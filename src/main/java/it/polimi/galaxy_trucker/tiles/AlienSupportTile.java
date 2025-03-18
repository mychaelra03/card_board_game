package it.polimi.galaxy_trucker.tiles;

public class AlienSupportTile extends Tile {

    private final CrewType alienType;

    public AlienSupportTile(int ID, Connector top, Connector right, Connector bottom, Connector left, CrewType alienType) {
        super(ID, top, right, bottom, left);
        if(alienType == CrewType.HUMAN) throw new IllegalArgumentException(alienType + " is a not a valid alien type.");
        this.alienType = alienType;
    }
    public AlienSupportTile(int ID, int top, int right, int bottom, int left, CrewType alienType) {
        super(ID, top, right, bottom, left);
        if(alienType == CrewType.HUMAN) throw new IllegalArgumentException(alienType + " is a not a valid alien type.");
        this.alienType = alienType;
    }

    public CrewType getAlienColor() {
        return alienType;
    }

    @Override
    public String getTileType() {
        return "Alien Support";
    }

}