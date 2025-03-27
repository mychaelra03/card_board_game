package it.polimi.galaxy_trucker.tiles;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class CargoTile extends Tile {

    public enum CargoType {
        NORMAL, RED
    }
    private final CargoType type;
    private final int capacity;
    private final BlockType[] containers;

    public CargoTile(
            @JsonProperty("id") int id,
            @JsonProperty("connectors") Connector[] connectors,
            @JsonProperty("cargoType") CargoType type,
            @JsonProperty("capacity") int capacity) {
        super(id, connectors);
        this.type = type;
        this.capacity = capacity;
        if (type == CargoType.NORMAL && (capacity < 2 || capacity > 3)) throw new IllegalArgumentException("Cargo must have 2 or 3 containers.");
        if (type == CargoType.RED && (capacity < 1 || capacity > 2)) throw new IllegalArgumentException("Red cargo must have 1 or 2 containers.");
        this.containers = new BlockType[capacity];
    }


    public int getCapacity() {
        return capacity;
    }

    public CargoType getType() {
        return type;
    }

    public BlockType getBlockAt(int index) {
        if (index < 0 || index >= containers.length) {
            throw new IllegalArgumentException("Invalid container index");
        }
        return containers[index];
    }

    public BlockType loadBlock(int index, BlockType newBlock) {
        // if the slot is empty, it adds the block and returns null
        // if there is already a block, it switches them and returns the block that was inside
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
        return loadBlock(index, null);
    }

    public int getHoldsValue() {
        int total = 0;
        for (BlockType container : containers) {
            if(container != null) total += container.getValue();
        }
        return total;
    }

    @Override
    public String getTileType() {
        return "Cargo Hold";
    }
}