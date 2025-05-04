package it.polimi.galaxy_trucker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.server.model.cards.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class CardFactory {

    private static final String PATH = "it/polimi/galaxy_trucker/";
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
            InputStream is = CardFactory.class.getClassLoader().getResourceAsStream(path);
            if(is == null) {
                throw new IOException("Could not find " + path);
            }
            JsonNode root = mapper.readTree(is);
            for(Iterator<String> iterator = root.fieldNames(); iterator.hasNext(); ) {
                String groupName = iterator.next();
                JsonNode cardsArray = root.get(groupName);
                if(!cardsArray.isArray()) {
                    throw new IllegalArgumentException("Group " + groupName + " must contain an array of cards.");
                }
                List<JsonNode> cardList = new ArrayList<>();
                for(JsonNode cardNode : cardsArray) {
                    if (!cardNode.has("id") || !cardNode.has("cardType")) {
                        throw new IllegalArgumentException("Card is missing required fields: " + cardNode);
                    }
                    cardList.add(cardNode);
                }
                groupMap.put(groupName, cardList);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error reading JSON file at " + path, e);
        }
        if(groupMap.containsKey("learningFlight")) {
            if(!groupMap.containsKey("levelOne")) groupMap.put("levelOne", new ArrayList<>());
            groupMap.get("levelOne").addAll(groupMap.get("learningFlight"));
        }
        return groupMap;
    }

    public List<Card> generateDeck(BoardType deckType) {
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
        if(deckType == BoardType.LEARNING || deckType == BoardType.LEVEL_ONE) return result;

        // If deck is level 2, adds 4 level 1 cards.
        List<JsonNode> level1Nodes = cardsByGroup.get("levelOne");
        if(level1Nodes == null || level1Nodes.size() < 4) {
            throw new IllegalArgumentException("Not enough level 1 cards available. Required: 4, available: " + ((level1Nodes == null)? 0 : level1Nodes.size()));
        }
        List<JsonNode> level1NodesCopy = new ArrayList<>(level1Nodes);
        Collections.shuffle(level1NodesCopy, random);
        for(int i = 0; i < 4; i++) {
            result.add(createCard(level1NodesCopy.get(i)));
        }

        return result;
    }

    public Card generateCard(BoardType deckType, int cardId) {
        // Returns a specific card. Used for testing.
        for(JsonNode cardNode : cardsByGroup.get(deckType.toString())) {
            if(cardNode.has("id") && cardNode.get("id").asInt() == cardId) {
                return createCard(cardNode);
            }
        }
        throw new IllegalArgumentException("Card with ID " + cardId + " not found.");
    }

    private static final Map<String, Class<? extends Card>> CARD_MAP = Map.ofEntries(
            Map.entry("abandonedShip", AbandonedShipCard.class),
            Map.entry("abandonedStation", AbandonedStationCard.class),
            Map.entry("combatZone", CombatZoneCard.class),
            Map.entry("epidemic", EpidemicCard.class),
            Map.entry("meteorSwarm", MeteorSwarmCard.class),
            Map.entry("openSpace", OpenSpaceCard.class),
            Map.entry("pirates", PiratesCard.class),
            Map.entry("planets", PlanetsCard.class),
            Map.entry("slavers", SlaversCard.class),
            Map.entry("smugglers", SmugglersCard.class),
            Map.entry("starDust", StarDustCard.class)
    );

    private Card createCard(JsonNode cardNode) {
        // Instantiates a Card from its JSON definition.

        String cardType = cardNode.get("cardType").asText();

        Class<? extends Card> cardClass = CARD_MAP.get(cardType);

        if(cardClass == null) throw new IllegalArgumentException("Unknown type in card " + cardNode.get("id") + ": " + cardType);

        try {
            Card card = mapper.readValue(cardNode.toString(), cardClass);
            return card;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}