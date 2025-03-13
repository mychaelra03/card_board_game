package it.polimi.galaxy_trucker.tiles;

public class CabinTile extends Tile {

    public enum CrewType {
        HUMAN, BROWN_ALIEN, PURPLE_ALIEN
    }
    private boolean isBrownAlienHabitable;
    private boolean isPurpleAlienHabitable;
    private final CrewType[] crew;

    public CabinTile(int ID, Connector top, Connector right, Connector bottom, Connector left) {
        super(ID, top, right, bottom, left);
        this.crew = new CrewType[2];
    }
    public CabinTile(int ID, int top, int right, int bottom, int left) {
        super(ID, top, right, bottom, left);
        this.crew = new CrewType[2];
    }

    public void setBrownAlienHabitability(boolean alienHabitable) {
        isBrownAlienHabitable = alienHabitable;
    }
    public void setPurpleAlienHabitability(boolean alienHabitable) {
        isPurpleAlienHabitable = alienHabitable;
    }

    public boolean isBrownAlienHabitable() {
        return isBrownAlienHabitable;
    }
    public boolean isPurpleAlienHabitable() {
        return isPurpleAlienHabitable;
    }

    public CrewType getMemberAt(int index) {
        if (index < 0 || index >= crew.length) throw new IllegalArgumentException("Invalid cabin index");
        return crew[index];
    }

    public void setCrewType(CrewType crewType) {
        if (!isBrownAlienHabitable && crewType == CrewType.BROWN_ALIEN) {
            throw new IllegalArgumentException("Cabin not habitable by brown aliens");
        }
        if (!isPurpleAlienHabitable && crewType == CrewType.PURPLE_ALIEN) {
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
    public int getBrownAlienCount() {
        if(crew[0] == CrewType.BROWN_ALIEN) return 1;
        return 0;
    }
    public int getPurpleAlienCount() {
        if(crew[0] == CrewType.PURPLE_ALIEN) return 1;
        return 0;
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
    public String getType() {
        return "Cabin";
    }
}