package it.polimi.galaxy_trucker.tiles;

class CargoTile extends Tile {

    public enum CargoType {
        NORMAL, RED
    }
    private final CargoType type;
    private final int capacity;
    private final BlockType[] containers;

    public CargoTile(Connector top, Connector right, Connector bottom, Connector left, CargoType type, int capacity) {
        super(top, right, bottom, left);
        this.type = type;
        this.capacity = capacity;
        if (type == CargoType.NORMAL && (capacity < 2 || capacity > 3)) throw new IllegalArgumentException("Cargo must have 2 or 3 containers.");
        if (type == CargoType.RED && (capacity < 1 || capacity > 2)) throw new IllegalArgumentException("Red cargo must have 1 or 2 containers.");
        this.containers = new BlockType[capacity];
    }
    public CargoTile(int top, int right, int bottom, int left, CargoType type, int capacity) {
        super(top, right, bottom, left);
        this.type = type;
        this.capacity = capacity;
        if (type == CargoType.NORMAL && (capacity < 2 || capacity > 3)) throw new IllegalArgumentException("Cargo must have 2 or 3 containers.");
        if (type == CargoType.RED && (capacity < 1 || capacity > 2)) throw new IllegalArgumentException("Red cargo must have 1 or 2 containers.");
        this.containers = new BlockType[capacity];
    }

    public int getCapacity() {
        return capacity;
    }

    public BlockType getBlockAt(int index) {
        if (index < 0 || index >= containers.length) {
            throw new IllegalArgumentException("Invalid container index");
        }
        return containers[index];
    }

    public BlockType addOrSwitchBlock(int index, BlockType newBlock) {
        // if the slot is empty, it adds the cube and returns null
        // if there is already a cube, it switches them and returns the cube that was inside
        if (type == CargoType.NORMAL && newBlock == BlockType.RED) {
            throw new IllegalArgumentException("Blue cargo cannot hold RED blocks.");
        }
        if (index < 0 || index >= containers.length) {
            throw new IllegalArgumentException("Invalid container index");
        }
        BlockType previousBlock = containers[index];
        containers[index] = newBlock;
        return previousBlock;
    }

    public BlockType removeBlock(int index) {
        if (index < 0 || index >= containers.length) {
            throw new IllegalArgumentException("Invalid container index");
        }
        BlockType removedBlock = containers[index];
        containers[index] = null;
        return removedBlock;
    }

    @Override
    public String getType() {
        return "CargoHold";
    }
}