package it.polimi.galaxy_trucker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.tiles.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TileFactory {
    private static final Map<Integer, Tile> tileTemplates = new HashMap<>();
    private static final Set<Integer> usedTileIDs = new HashSet<>();

    static {
        loadTilesFromJson();
    }

    private static void loadTilesFromJson() {
        try {
            // Read JSON from resources
            InputStream inputStream = TileFactory.class.getResourceAsStream("public/tiles.json");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(inputStream);

            // Loop through each tile entry in the JSON
            for (JsonNode node : rootNode) {
                int id = node.get("id").asInt();
                String tileType = node.get("tileType").asText();
                int[] connectors = new int[]{
                        node.get("connectors").get(0).asInt(),
                        node.get("connectors").get(1).asInt(),
                        node.get("connectors").get(2).asInt(),
                        node.get("connectors").get(3).asInt()
                };

                Tile tile = createTile(id, tileType, connectors, node);
                tileTemplates.put(id, tile);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load tile data from JSON", e);
        }
    }

    private static Tile createTile(int id, String tileType, int[] connectors, JsonNode node) {
        return switch (tileType) {
            case "alienSupport" ->
                    new AlienSupportTile(id, connectors[0], connectors[1], connectors[2], connectors[3], CrewType.valueOf(node.get("alienType").asText()));
            case "cabin" -> new CabinTile(id, connectors[0], connectors[1], connectors[2], connectors[3]);
            case "cargo" -> new CargoTile(id, connectors[0], connectors[1], connectors[2], connectors[3],
                    CargoTile.CargoType.valueOf(node.get("cargoType").asText()), node.get("capacity").asInt());
            case "batteries" ->
                    new BatteriesTile(id, connectors[0], connectors[1], connectors[2], connectors[3], node.get("capacity").asInt());
            case "cannon" -> new CannonTile(id, connectors[0], connectors[1], connectors[2], connectors[3], node.get("isDouble").asBoolean());
            case "engine" -> new EngineTile(id, connectors[0], connectors[1], connectors[2], connectors[3], node.get("isDouble").asBoolean());
            case "shield" -> new ShieldTile(id, connectors[0], connectors[1], connectors[2], connectors[3]);
            case "structural" -> new StructuralTile(id, connectors[0], connectors[1], connectors[2], connectors[3]);

            default -> throw new IllegalArgumentException("Unknown tile type: " + tileType);
        };
    }


    public static Tile getTile(int id) {
        if (!tileTemplates.containsKey(id)) {
            throw new IllegalArgumentException("Tile ID not found: " + id);
        }
        if (usedTileIDs.contains(id)) {
            throw new IllegalStateException("Tile ID " + id + " has already been used.");
        }

        usedTileIDs.add(id); // Mark the tile as used
        //Tile prototype = tileTemplates.get(id);
        //return createNewInstance(prototype);
        return tileTemplates.get(id);
    }

    public static void resetTiles() {
        tileTemplates.clear();
        usedTileIDs.clear();
        loadTilesFromJson();
    }

//    private static Tile createNewInstance(Tile prototype) {
//        if (prototype instanceof AlienSupportTile alienSupport) {
//            return new AlienSupportTile(alienSupport.getID(),
//                    alienSupport.getConnector(0),
//                    alienSupport.getConnector(1),
//                    alienSupport.getConnector(2),
//                    alienSupport.getConnector(3),
//                    alienSupport.getAlienColor());
//        }
//        if (prototype instanceof CabinTile) {
//            return new CabinTile(prototype.getID(),
//                    prototype.getConnector(0),
//                    prototype.getConnector(1),
//                    prototype.getConnector(2),
//                    prototype.getConnector(3));
//        }
//        if (prototype instanceof CargoTile cargo) {
//            return new CargoTile(prototype.getID(),
//                    prototype.getConnector(0),
//                    prototype.getConnector(1),
//                    prototype.getConnector(2),
//                    prototype.getConnector(3),
//                    cargo.getType(),
//                    cargo.getCapacity());
//        }
//        if (prototype instanceof BatteriesTile batteries) {
//            return new BatteriesTile(batteries.getID(),
//                    batteries.getConnector(0),
//                    batteries.getConnector(1),
//                    batteries.getConnector(2),
//                    batteries.getConnector(3),
//                    batteries.getCapacity());
//        }
//        if (prototype instanceof DoubleCannonTile) {
//            return new DoubleCannonTile(prototype.getID(),
//                    prototype.getConnector(0),
//                    prototype.getConnector(1),
//                    prototype.getConnector(2),
//                    prototype.getConnector(3));
//        }
//        if (prototype instanceof CannonTile) {
//            return new CannonTile(prototype.getID(),
//                    prototype.getConnector(0),
//                    prototype.getConnector(1),
//                    prototype.getConnector(2),
//                    prototype.getConnector(3));
//        }
//        if (prototype instanceof DoubleEngineTile) {
//            return new DoubleEngineTile(prototype.getID(),
//                    prototype.getConnector(0),
//                    prototype.getConnector(1),
//                    prototype.getConnector(2),
//                    prototype.getConnector(3));
//        }
//        if (prototype instanceof EngineTile) {
//            return new EngineTile(prototype.getID(),
//                    prototype.getConnector(0),
//                    prototype.getConnector(1),
//                    prototype.getConnector(2),
//                    prototype.getConnector(3));
//        }
//        if (prototype instanceof ShieldTile) {
//            return new ShieldTile(prototype.getID(),
//                    prototype.getConnector(0),
//                    prototype.getConnector(1),
//                    prototype.getConnector(2),
//                    prototype.getConnector(3));
//        }
//        if (prototype instanceof StructuralTile) {
//            return new StructuralTile(prototype.getID(),
//                    prototype.getConnector(0),
//                    prototype.getConnector(1),
//                    prototype.getConnector(2),
//                    prototype.getConnector(3));
//        }
//        throw new IllegalArgumentException("Unsupported tile type: " + prototype.getClass().getSimpleName());
//    }
}
