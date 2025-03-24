package it.polimi.galaxy_trucker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.cards.*;
import it.polimi.galaxy_trucker.tiles.BlockType;
import it.polimi.galaxy_trucker.cards.CombatZoneCard.CombatRule.*;

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

    public List<Card> getLearningDeck() {
        // Returns a list of all cards from the "learningFlight" group.
        List<Card> result = new ArrayList<>();
        List<JsonNode> learningFlightNodes = cardsByGroup.get("learningFlight");
        if(learningFlightNodes == null || learningFlightNodes.size() < 8) {
            throw new IllegalArgumentException("Not enough learning cards available. Required: 8, available: " + ((learningFlightNodes == null)? 0 : learningFlightNodes.size()));
        }
        Collections.shuffle(learningFlightNodes, random);
        for(int i = 0; i < 8; i++) {
            result.add(createCard(learningFlightNodes.get(i), 1));
        }
        return result;
    }

    public List<Card> getLevelTwoDeck() {
        // Returns a list of cards for level 2 play:
        // 8 random level 2 cards and 4 random level 1 cards (from both "level1" and "learningFlight").
        List<Card> result = new ArrayList<>();

        // Select 8 random levelTwo cards.
        List<JsonNode> levelTwoNodes = cardsByGroup.get("level2");
        if(levelTwoNodes == null || levelTwoNodes.size() < 8) {
            throw new IllegalArgumentException("Not enough level 2 cards available. Required: 8, available: " + ((levelTwoNodes == null)? 0 : levelTwoNodes.size()));
        }
        List<JsonNode> levelTwoCopy = new ArrayList<>(levelTwoNodes);
        Collections.shuffle(levelTwoCopy, random);
        for(int i = 0; i < 8; i++) {
            result.add(createCard(levelTwoCopy.get(i), 2));
        }

        // Combine level1 cards from "level1" and "learningFlight".
        List<JsonNode> levelOneNodes = new ArrayList<>();
        if(cardsByGroup.containsKey("level1")) {
            levelOneNodes.addAll(cardsByGroup.get("level1"));
        }
        if(cardsByGroup.containsKey("learningFlight")) {
            levelOneNodes.addAll(cardsByGroup.get("learningFlight"));
        }
        if(levelOneNodes.size() < 4) {
            throw new IllegalArgumentException("Not enough level 1 cards available. Required: 4, available: " + levelOneNodes.size());
        }
        Collections.shuffle(levelOneNodes, random);
        for(int i = 0; i < 4; i++) {
            result.add(createCard(levelOneNodes.get(i), 1));
        }

        return result;
    }

    private Card createCard(JsonNode cardNode, int level) {
        // Instantiates a Card from its JSON definition.
        int id = cardNode.get("id").asInt();
        String cardType = cardNode.get("cardType").asText();
        //int level = cardNode.get("level").asInt();

        switch (cardType) {
            case "abandonedShip": {
                if(!cardNode.has("crewToGiveUp")) {
                    throw new IllegalArgumentException("Missing crewToGiveUp for AbandonedShip card id " + id);
                }
                int crewToGiveUp = cardNode.get("crewToGiveUp").asInt();
                if(!cardNode.has("rewardCredits")) {
                    throw new IllegalArgumentException("Missing rewardCredits for AbandonedShip card id " + id);
                }
                int rewardCredits = cardNode.get("rewardCredits").asInt();
                if(!cardNode.has("positionCost")) {
                    throw new IllegalArgumentException("Missing positionCost for AbandonedShip card id " + id);
                }
                int positionCost = cardNode.get("positionCost").asInt();

                return new AbandonedShipCard(id, level, crewToGiveUp, rewardCredits, positionCost);
            }

            case "abandonedStation": {
                if(!cardNode.has("requiredCrew")) {
                    throw new IllegalArgumentException("Missing requiredCrew for AbandonedStatuib card id " + id);
                }
                int requiredCrew = cardNode.get("requiredCrew").asInt();
                JsonNode rewardCargoNode = cardNode.get("rewardCargo");
                if(rewardCargoNode == null || !rewardCargoNode.isArray()) {
                    throw new IllegalArgumentException("rewardCargo is not an array for card id " + id);
                }
                List<BlockType> rewardCargoList = new ArrayList<>();
                for(JsonNode cargoNode : rewardCargoNode) {
                    BlockType block = BlockType.valueOf(cargoNode.asText());
                    rewardCargoList.add(block);
                }
                BlockType[] rewardCargo = rewardCargoList.toArray(new BlockType[0]);
                if(!cardNode.has("positionCost")) {
                    throw new IllegalArgumentException("Missing positionCost for AbandonedShip card id " + id);
                }
                int positionCost = cardNode.get("positionCost").asInt();

                return new AbandonedStationCard(id, level, requiredCrew, rewardCargo, positionCost);
            }

            case "combatZone": {
                // Expect a combatRules array.
                JsonNode combatRulesNode = cardNode.get("combatRules");
                if(combatRulesNode == null || !combatRulesNode.isArray()) {
                    throw new IllegalArgumentException("Missing or invalid combatRules for Combat Zone card id " + id);
                }
                List<CombatZoneCard.CombatRule> combatRulesList = new ArrayList<>();
                for(JsonNode ruleNode : combatRulesNode) {
                    Condition condition = Condition.valueOf(ruleNode.get("condition").asText());
                    Punishment punishment = Punishment.valueOf(ruleNode.get("punishment").asText());
                    if(punishment == Punishment.CANNON_SHOTS) {
                        // For CANNON_SHOTS, expect a cannonShot array.
                        JsonNode cannonShotsNode = ruleNode.get("cannonShots");
                        if(cannonShotsNode == null || !cannonShotsNode.isArray()) {
                            throw new IllegalArgumentException("Missing cannonShots for CANNON_SHOTS rule in card id " + id);
                        }
                        List<CannonShot> cannonShotsList = new ArrayList<>();
                        for(JsonNode shotNode : cannonShotsNode) {
                            ProjectileSize size = ProjectileSize.valueOf(shotNode.get("size").asText());
                            ProjectileDirection direction = ProjectileDirection.valueOf(shotNode.get("direction").asText());
                            cannonShotsList.add(new CannonShot(size, direction));
                        }
                        CannonShot[] cannonShots = cannonShotsList.toArray(new CannonShot[0]);
                        CombatZoneCard.CombatRule rule = new CombatZoneCard.CombatRule(condition, punishment, cannonShots);
                        combatRulesList.add(rule);
                    } else {
                        if(!ruleNode.has("punishmentValue")) {
                            throw new IllegalArgumentException("Missing punishmentValue for rule in card id " + id);
                        }
                        int punishmentValue = ruleNode.get("punishmentValue").asInt();
                        CombatZoneCard.CombatRule rule = new CombatZoneCard.CombatRule(
                                condition, punishment, punishmentValue
                        );
                        combatRulesList.add(rule);
                    }
                }
                CombatZoneCard.CombatRule[] combatRules = combatRulesList.toArray(new CombatZoneCard.CombatRule[0]);

                return new CombatZoneCard(id, level, combatRules);
            }

            case "epidemic": {
                return new EpidemicCard(id, level);
            }

            case "meteorSwarm": {
                JsonNode meteorsNode = cardNode.get("meteors");
                if(meteorsNode == null || !meteorsNode.isArray()) {
                    throw new IllegalArgumentException("meteors is not an array for card id " + id);
                }
                List<MeteorSwarmCard.Meteor> meteorsList = new ArrayList<>();
                for(JsonNode meteorNode : meteorsNode) {
                    ProjectileSize size = ProjectileSize.valueOf(meteorNode.get("size").asText());
                    ProjectileDirection direction = ProjectileDirection.valueOf(meteorNode.get("direction").asText());
                    meteorsList.add(new MeteorSwarmCard.Meteor(size, direction));
                }
                MeteorSwarmCard.Meteor[] meteors = meteorsList.toArray(new MeteorSwarmCard.Meteor[0]);

                return new MeteorSwarmCard(id, level, meteors);
            }

            case "openSpace": {
                return new OpenSpaceCard(id, level);
            }

            case "pirates": {
                if(!cardNode.has("requiredCannonPower")) {
                    throw new IllegalArgumentException("Missing requiredCannonPower for Pirates card id " + id);
                }
                int requiredCannonPower = cardNode.get("requiredCannonPower").asInt();
                if(!cardNode.has("rewardCredits")) {
                    throw new IllegalArgumentException("Missing rewardCredits for Pirates card id " + id);
                }
                int rewardCredits = cardNode.get("rewardCredits").asInt();
                JsonNode cannonShotsNode = cardNode.get("cannonShots");
                if(cannonShotsNode == null || !cannonShotsNode.isArray()) {
                    throw new IllegalArgumentException("cannonShots is not an array for card id " + id);
                }
                if(!cardNode.has("positionCost")) {
                    throw new IllegalArgumentException("Missing positionCost for Pirates card id " + id);
                }
                int positionCost = cardNode.get("positionCost").asInt();
                List<CannonShot> cannonShotsList = new ArrayList<>();
                for(JsonNode shotNode : cannonShotsNode) {
                    ProjectileSize size = ProjectileSize.valueOf(shotNode.get("size").asText());
                    ProjectileDirection direction = ProjectileDirection.valueOf(shotNode.get("direction").asText());
                    cannonShotsList.add(new CannonShot(size, direction));
                }
                CannonShot[] cannonShots = cannonShotsList.toArray(new CannonShot[0]);

                return new PiratesCard(id, level, requiredCannonPower, rewardCredits, positionCost, cannonShots);
            }

            case "planets": {
                JsonNode planetsNode = cardNode.get("planets");
                if(planetsNode == null || !planetsNode.isArray()) {
                    throw new IllegalArgumentException("Missing or invalid planets for Planets card id " + id);
                }
                List<BlockType[]> planetsList = new ArrayList<>();
                for(JsonNode rowNode : planetsNode) {
                    if(!rowNode.isArray()) {
                        throw new IllegalArgumentException("Planets row is not an array for card id " + id);
                    }
                    List<BlockType> row = new ArrayList<>();
                    for(JsonNode planetNode : rowNode) {
                        BlockType block = BlockType.valueOf(planetNode.asText());
                        row.add(block);
                    }
                    planetsList.add(row.toArray(new BlockType[0]));
                }
                BlockType[][] planets = planetsList.toArray(new BlockType[0][]);
                if(!cardNode.has("positionCost")) {
                    throw new IllegalArgumentException("Missing positionCost for Planets card id " + id);
                }
                int positionCost = cardNode.get("positionCost").asInt();

                return new PlanetsCard(id, level, planets, positionCost);
            }

            case "slavers": {
                if(!cardNode.has("requiredCannonPower")) {
                    throw new IllegalArgumentException("Missing requiredCannonPower for Slavers card id " + id);
                }
                int requiredCannonPower = cardNode.get("requiredCannonPower").asInt();
                if(!cardNode.has("rewardCredits")) {
                    throw new IllegalArgumentException("Missing rewardCredits for Slavers card id " + id);
                }
                int rewardCredits = cardNode.get("rewardCredits").asInt();
                if(!cardNode.has("positionCost")) {
                    throw new IllegalArgumentException("Missing positionCost for Slavers card id " + id);
                }
                int positionCost = cardNode.get("positionCost").asInt();
                if(!cardNode.has("crewLossOnDefeat")) {
                    throw new IllegalArgumentException("Missing crewLossOnDefeat for Slavers card id " + id);
                }
                int crewLossOnDefeat = cardNode.get("crewLossOnDefeat").asInt();

                return new SlaversCard(id, level, requiredCannonPower, rewardCredits, positionCost, crewLossOnDefeat);
            }

            case "smugglers": {
                if(!cardNode.has("requiredCannonPower")) {
                    throw new IllegalArgumentException("Missing requiredCannonPower for Slavers card id " + id);
                }
                int requiredCannonPower = cardNode.get("requiredCannonPower").asInt();
                JsonNode rewardCargoNode = cardNode.get("rewardCargo");
                if(rewardCargoNode == null || !rewardCargoNode.isArray()) {
                    throw new IllegalArgumentException("rewardCargo is not an array for card id " + id);
                }
                List<BlockType> rewardCargoList = new ArrayList<>();
                for(JsonNode cargoNode : rewardCargoNode) {
                    BlockType block = BlockType.valueOf(cargoNode.asText());
                    rewardCargoList.add(block);
                }
                BlockType[] rewardCargo = rewardCargoList.toArray(new BlockType[0]);
                if(!cardNode.has("positionCost")) {
                    throw new IllegalArgumentException("Missing positionCost for Slavers card id " + id);
                }
                int positionCost = cardNode.get("positionCost").asInt();
                if(!cardNode.has("cargoLossOnDefeat")) {
                    throw new IllegalArgumentException("Missing cargoLossOnDefeat for Slavers card id " + id);
                }
                int cargoLossOnDefeat = cardNode.get("cargoLossOnDefeat").asInt();

                return new SmugglersCard(id, level, requiredCannonPower, rewardCargo, positionCost, cargoLossOnDefeat);
            }

            case "starDust": {
                return new StarDustCard(id, level);
            }

            default:
                throw new IllegalArgumentException("Unknown cardType: " + cardType + " for card id " + id);
        }
    }
}