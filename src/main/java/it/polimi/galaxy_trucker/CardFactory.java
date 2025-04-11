package it.polimi.galaxy_trucker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.Server.Model.cards.*;
import it.polimi.galaxy_trucker.Server.Model.tiles.*;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class CardFactory {

    private static final String PATH = "src/main/resources/it/polimi/galaxy_trucker/";
    private static CardFactory instance;
    private final Map<String, List<JsonNode>> cardsByGroup;
    private final ObjectMapper mapper;
    private final Random random;

    private CardFactory(String jsonFilePath) {
        // Private constructor for the singleton. Loads the JSON file.
        this.mapper = new ObjectMapper();
        this.random = new Random();
        this.cardsByGroup = loadJson(jsonFilePath);
    }

    public static CardFactory getInstance() {
        // Retrieves the singleton instance of CardFactory.
        if(instance == null) {
            instance = new CardFactory(PATH + "public/cards.json");
        }
        return instance;
    }

    private Map<String, List<JsonNode>> loadJson(String path) {
        // Loads the JSON file and builds a map of group names to a list of card jsons.
        Map<String, List<JsonNode>> groupMap = new HashMap<>();
        try {
            JsonNode root = mapper.readTree(new File(path));
            for (Iterator<String> iterator = root.fieldNames(); iterator.hasNext(); ) {
                String groupName = iterator.next();
                JsonNode cardsArray = root.get(groupName);
                if (!cardsArray.isArray()) {
                    throw new IllegalArgumentException("Group " + groupName + " must contain an array of cards.");
                }
                List<JsonNode> cardList = new ArrayList<>();
                for (JsonNode cardNode : cardsArray) {
                    if (!cardNode.has("id") || !cardNode.has("cardType")) {
                        throw new IllegalArgumentException("Card is missing required fields: " + cardNode);
                    }
                    cardList.add(cardNode);
                }
                groupMap.put(groupName, cardList);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON file at " + path, e);
        }
        return groupMap;
    }

    public List<Card> getDeck(BoardType deckType) {
        List<Card> result = new ArrayList<>();
        List<JsonNode> cardsNodes = cardsByGroup.get(deckType.toString());

        if(cardsNodes == null || cardsNodes.size() < 8) {
            throw new IllegalArgumentException("Not enough " + deckType + "cards available. Required: 8, available: " + ((cardsNodes == null)? 0 : cardsNodes.size()));
        }
        List<JsonNode> cardsNodesCopy = new ArrayList<>(cardsNodes);
        Collections.shuffle(cardsNodesCopy, random);
        for(int i = 0; i < 8; i++) {
            result.add(createCard(cardsNodesCopy.get(i)));
        }
        if(deckType == BoardType.LEARNING) return result;

        // Combine level 1 cards from "levelOne" and "learningFlight".
        List<JsonNode> levelOneNodes = new ArrayList<>();
        if(cardsByGroup.containsKey("levelOne")) {
            levelOneNodes.addAll(cardsByGroup.get("levelOne"));
        }
        if(cardsByGroup.containsKey("learningFlight")) {
            levelOneNodes.addAll(cardsByGroup.get("learningFlight"));
        }
        if(levelOneNodes.size() < 4) {
            throw new IllegalArgumentException("Not enough level 1 cards available. Required: 4, available: " + levelOneNodes.size());
        }
        Collections.shuffle(levelOneNodes, random);
        for(int i = 0; i < 4; i++) {
            result.add(createCard(levelOneNodes.get(i)));
        }

        return result;
    }

    private static final Map<String, Class<? extends Card>> CARD_MAP = new HashMap<>();
    static {
        CARD_MAP.put("abandonedShip", AbandonedShipCard.class);
        CARD_MAP.put("abandonedStation", AbandonedStationCard.class);
        CARD_MAP.put("combatZone", CombatZoneCard.class);
        CARD_MAP.put("epidemic", EpidemicCard.class);
        CARD_MAP.put("meteorSwarm", MeteorSwarmCard.class);
        CARD_MAP.put("openSpace", OpenSpaceCard.class);
        CARD_MAP.put("pirates", PiratesCard.class);
        CARD_MAP.put("planets", PlanetsCard.class);
        CARD_MAP.put("slavers", SlaversCard.class);
        CARD_MAP.put("smugglers", SmugglersCard.class);
        CARD_MAP.put("starDust", StarDustCard.class);
    }

    private Card createCard(JsonNode cardNode) {
        // Instantiates a Card from its JSON definition.

        String cardType = cardNode.get("cardType").asText();

        Class<? extends Card> cardClass = CARD_MAP.get(cardType);
        if (cardClass == null) {
            throw new IllegalArgumentException("Unknown type in card " + cardNode.get("id") + ": " + cardType);
        }
        try {
            return mapper.readValue(cardNode.toString(), cardClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}