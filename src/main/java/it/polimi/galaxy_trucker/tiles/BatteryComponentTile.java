package it.polimi.galaxy_trucker.tiles;

class BatteryComponentTile extends Tile {

    private final int capacity;
    private int storedBatteries;

    public BatteryComponentTile(Connector top, Connector right, Connector bottom, Connector left, int capacity) {
        super(top, right, bottom, left);
        this.capacity = capacity;
        if (capacity < 2 || capacity > 3) throw new IllegalArgumentException("Battery Component must have capacity of 2 or 3.");
        this.storedBatteries = 0;
    }
    public BatteryComponentTile(int top, int right, int bottom, int left, int capacity) {
        super(top, right, bottom, left);
        this.capacity = capacity;
        if (capacity < 2 || capacity > 3) throw new IllegalArgumentException("Battery Component must have capacity of 2 or 3.");
        this.storedBatteries = 0;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getStoredBatteries() {
        return storedBatteries;
    }

    public boolean addBattery() {
        if (storedBatteries < capacity) {
            storedBatteries++;
            return true;
        }
        return false;
    }

    public boolean removeBattery() {
        if (storedBatteries > 0) {
            storedBatteries--;
            return true;
        }
        return false;
    }

    @Override
    public String getType() {
        return "Battery Container";
    }
}
