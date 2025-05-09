package it.polimi.galaxy_trucker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.server.model.tiles.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class TileFactory {

    private static TileFactory instance;
    private final List<JsonNode> tilesJson;
    private final ObjectMapper mapper;

    private TileFactory(String jsonFilePath) {
        // Private constructor for the singleton. Loads the JSON file.
        this.mapper = new ObjectMapper();
        this.tilesJson = loadJson(jsonFilePath);
    }

    public static TileFactory getInstance() {
        // Retrieves the singleton instance of TileFactory.
        if(instance == null) {
            instance = new TileFactory("public/tiles.json");
        }
        return instance;
    }

    private List<JsonNode> loadJson(String path) {
        // Loads the JSON file and returns a list of tile definitions.
        List<JsonNode> tileList = new ArrayList<>();
        try{
            InputStream is = TileFactory.class.getClassLoader().getResourceAsStream(path);
            if(is == null) {
                throw new IOException("Could not find " + path);
            }
            JsonNode root = mapper.readTree(is);
            if(!root.isArray()) {
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

    private static final Map<String, Class<? extends Tile>> TILE_MAP = new HashMap<>();
    static {
        TILE_MAP.put("alienSupport", AlienSupportTile.class);
        TILE_MAP.put("batteries", BatteriesTile.class);
        TILE_MAP.put("cabin", CabinTile.class);
        TILE_MAP.put("cannon", CannonTile.class);
        TILE_MAP.put("cargo", CargoTile.class);
        TILE_MAP.put("engine", EngineTile.class);
        TILE_MAP.put("shield", ShieldTile.class);
        TILE_MAP.put("structural", StructuralTile.class);
    }

    private Tile createTile(JsonNode tileNode) {
        // Instantiates a Tile from its JSON definition.

        String tileType = tileNode.get("tileType").asText();

        Class<? extends Tile> tileClass = TILE_MAP.get(tileType);
        if (tileClass == null) {
            throw new IllegalArgumentException("Unknown type in tile " + tileNode.get("id") + ": " + tileType);
        }
        try {
            return mapper.readValue(tileNode.toString(), tileClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}