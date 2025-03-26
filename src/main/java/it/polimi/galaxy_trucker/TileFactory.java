package it.polimi.galaxy_trucker;

import com.fasterxml.jackson.core.JsonProcessingException;
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

        switch (tileType) {

            case "alienSupport": {
                try {
                    return mapper.readValue(tileNode.toString(), AlienSupportTile.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            case "cabin": {
                try {
                    return mapper.readValue(tileNode.toString(), CabinTile.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            case "cargo": {
                try {
                    return mapper.readValue(tileNode.toString(), CargoTile.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            case "batteries": {
                try {
                    return mapper.readValue(tileNode.toString(), BatteriesTile.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            case "cannon": {
                try {
                    return mapper.readValue(tileNode.toString(), CannonTile.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            case "engine": {
                try {
                    return mapper.readValue(tileNode.toString(), EngineTile.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            case "shield": {
                try {
                    return mapper.readValue(tileNode.toString(), ShieldTile.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            case "structural": {
                try {
                    return mapper.readValue(tileNode.toString(), StructuralTile.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            default:
                throw new IllegalArgumentException("Unknown tileType: " + tileType + " for tile id " + id);
        }
    }
}