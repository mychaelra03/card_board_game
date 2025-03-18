package it.polimi.galaxy_trucker.tiles;

public class CabinTile extends Tile {

    private boolean canHostBrownAlien = false;
    private boolean canHostPurpleAlien = false;
    private final CrewType[] crew;

    public CabinTile(int ID, Connector top, Connector right, Connector bottom, Connector left) {
        super(ID, top, right, bottom, left);
        this.crew = new CrewType[2];
        setCrewType(CrewType.HUMAN); // Not sure if i should set this now or while deciding crew
    }
    public CabinTile(int ID, int top, int right, int bottom, int left) {
        super(ID, top, right, bottom, left);
        this.crew = new CrewType[2];
        setCrewType(CrewType.HUMAN);
    }

    public void allowAlienType(CrewType alienType, boolean alienHabitable) {
        if(alienType == CrewType.HUMAN) throw new IllegalArgumentException("Alien type can't be HUMAN.");
        if(alienType == CrewType.BROWN_ALIEN) canHostBrownAlien = alienHabitable;
        else if(alienType == CrewType.PURPLE_ALIEN) canHostPurpleAlien = alienHabitable;
        if(!alienHabitable && hasAlien(alienType)) crew[0] = null;
    }
    public boolean hasAlien(CrewType alienType) {
        if(alienType == CrewType.HUMAN) throw new IllegalArgumentException("Alien type can't be HUMAN.");
        return crew[0] == alienType;
    }
    public boolean canHostAlienType(CrewType alienType) {
        if(alienType == CrewType.HUMAN) throw new IllegalArgumentException("Alien type can't be HUMAN.");
        return (alienType == CrewType.BROWN_ALIEN)? canHostBrownAlien : canHostPurpleAlien;
    }

    public CrewType getMemberAt(int index) {
        if (index < 0 || index >= crew.length) throw new IllegalArgumentException("Invalid cabin index");
        return crew[index];
    }

    public void setCrewType(CrewType crewType) {
        if (!canHostBrownAlien && crewType == CrewType.BROWN_ALIEN) {
            throw new IllegalArgumentException("Cabin not habitable by brown aliens");
        }
        if (!canHostPurpleAlien && crewType == CrewType.PURPLE_ALIEN) {
            throw new IllegalArgumentException("Cabin not habitable by purple aliens");
        }
        this.crew[0] = crewType;
        this.crew[1] = (crewType == CrewType.HUMAN) ? crewType : null;
    }

    public int getHumanCount() {
        int count = 0;
        if(crew[0] == CrewType.HUMAN) count++;
        if(crew[1] == CrewType.HUMAN) count++;
        return count;
    }

    public CrewType removeMember(int index) {
        if (index < 0 || index >= crew.length) {
            throw new IllegalArgumentException("Invalid cabin index");
        }
        CrewType removedMember = crew[index];
        crew[index] = null;
        return removedMember;
    }

    @Override
    public String getTileType() {
        return "Cabin";
    }
}