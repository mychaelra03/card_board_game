package it.polimi.galaxy_trucker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.cards.AbandonedShipCard;
import it.polimi.galaxy_trucker.tiles.*;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class TileFactory {

    private static final String PATH = "src/main/resources/it/polimi/galaxy_trucker/";
    private static TileFactory instance;
    private final List<JsonNode> tilesJson;
    private final ObjectMapper mapper;
    private final Random random;

    private TileFactory(String jsonFilePath) {
        // Private constructor for the singleton. Loads the JSON file.
        this.mapper = new ObjectMapper();
        this.random = new Random();
        this.tilesJson = loadJson(jsonFilePath);
    }

    public static TileFactory getInstance() {
        // Retrieves the singleton instance of TileFactory.
        if (instance == null) {
            instance = new TileFactory(PATH + "public/tiles.json");
        }
        return instance;
    }

    private List<JsonNode> loadJson(String path) {
        // Loads the JSON file and returns a list of tile definitions.
        List<JsonNode> tileList = new ArrayList<>();
        try {
            JsonNode root = mapper.readTree(new File(path));
            if (!root.isArray()) {
                throw new IllegalArgumentException("Root element must be a JSON array.");
            }
            for (JsonNode tileNode : root) {
                if (!tileNode.has("id") || !tileNode.has("tileType") || !tileNode.has("connectors")) {
                    throw new IllegalArgumentException("Tile is missing required fields: " + tileNode);
                }
                tileList.add(tileNode);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON file at " + path, e);
        }
        return tileList;
    }

    public Tile getTile(int id) {
        // Returns the requested tile.
        for(JsonNode tileNode : tilesJson) {
            if(tileNode.get("id").asInt() == id) {
                return createTile(tileNode);
            }
        }
        throw new IllegalArgumentException("Tile with ID " + id + " not found.");
    }

    private Tile createTile(JsonNode tileNode) {
        // Instantiates a Tile from its JSON definition.
        int id = tileNode.get("id").asInt();
        String tileType = tileNode.get("tileType").asText();
        int[] connectors = new int[]{
                tileNode.get("connectors").get(0).asInt(),
                tileNode.get("connectors").get(1).asInt(),
                tileNode.get("connectors").get(2).asInt(),
                tileNode.get("connectors").get(3).asInt()
        };

        switch (tileType) {
            case "alienSupport": {
                if (!tileNode.has("alienType")) {
                    throw new IllegalArgumentException("Missing alienType for alienSupport tile id " + id);
                }
                CrewType alienType = CrewType.valueOf(tileNode.get("alienType").asText());

                return new AlienSupportTile(id, connectors[0], connectors[1], connectors[2], connectors[3], alienType);
            }

            case "cabin": {
                return new CabinTile(id, connectors[0], connectors[1], connectors[2], connectors[3]);
            }

            case "cargo": {
                if (!tileNode.has("cargoType")) {
                    throw new IllegalArgumentException("Missing cargoType for cargo tile id " + id);
                }
                CargoTile.CargoType cargoType = CargoTile.CargoType.valueOf(tileNode.get("cargoType").asText());
                if (!tileNode.has("capacity")) {
                    throw new IllegalArgumentException("Missing capacity for cargo tile id " + id);
                }
                int capacity = tileNode.get("capacity").asInt();

                return new CargoTile(id, connectors[0], connectors[1], connectors[2], connectors[3], cargoType, capacity);
            }

            case "batteries": {
                if (!tileNode.has("capacity")) {
                    throw new IllegalArgumentException("Missing capacity for batteries tile id " + id);
                }
                int capacity = tileNode.get("capacity").asInt();

                return new BatteriesTile(id, connectors[0], connectors[1], connectors[2], connectors[3], capacity);
            }

            case "cannon": {
                if (!tileNode.has("isDouble")) {
                    throw new IllegalArgumentException("Missing isDouble for cannon tile id " + id);
                }
                boolean isDouble = tileNode.get("isDouble").asBoolean();

                return new CannonTile(id, connectors[0], connectors[1], connectors[2], connectors[3], isDouble);
            }

            case "engine": {
                if (!tileNode.has("isDouble")) {
                    throw new IllegalArgumentException("Missing isDouble for engine tile id " + id);
                }
                boolean isDouble = tileNode.get("isDouble").asBoolean();

                return new EngineTile(id, connectors[0], connectors[1], connectors[2], connectors[3], isDouble);
            }

            case "shield": {
                return new ShieldTile(id, connectors[0], connectors[1], connectors[2], connectors[3]);
            }

            case "structural": {
                return new StructuralTile(id, connectors[0], connectors[1], connectors[2], connectors[3]);
            }

            default:
                throw new IllegalArgumentException("Unknown tileType: " + tileType + " for tile id " + id);
        }
    }
}