package it.polimi.galaxy_trucker.tiles;

import it.polimi.galaxy_trucker.Player;

public class StartingCabinTile extends CabinTile {

    private static final int ID = 0;
    private final Player.Color color;

    public StartingCabinTile(Player.Color color) {
        super(ID, Connector.UNIVERSAL, Connector.UNIVERSAL,  Connector.UNIVERSAL,  Connector.UNIVERSAL);
        this.color = color;
    }

    public Player.Color getColor() {
        return color;
    }

    @Override
    public void allowAlienType(CrewType alienType, boolean alienHabitable) {
        if(alienHabitable) throw new IllegalArgumentException("Starting cabin cannot host aliens.");
    }

    @Override
    public void setCrewType(CrewType crewType) {
        if(crewType != CrewType.HUMAN) throw new IllegalArgumentException("Starting cabin cannot host aliens.");
        super.setCrewType(crewType);
    }

    @Override
    public String getTileType() {
        return "Starting Cabin";
    }
}