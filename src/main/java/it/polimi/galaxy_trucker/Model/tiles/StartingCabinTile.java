package it.polimi.galaxy_trucker.Model.tiles;

public class StartingCabinTile extends CabinTile {

    public StartingCabinTile() {
        super(0, new Connector[]{Connector.UNIVERSAL,Connector.UNIVERSAL,Connector.UNIVERSAL,Connector.UNIVERSAL});
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