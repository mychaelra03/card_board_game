package it.polimi.galaxy_trucker.Server.Model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.Server.Model.tiles.BlockType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.polimi.galaxy_trucker.GameModel.Direction.BACKWARD;

@JsonIgnoreProperties(ignoreUnknown = true)

public class PlanetsCard extends Card {
    // Players can land to collect goods.

    private final BlockType[][] planets;
    private final int positionCost;

    private List<Player> playerQueue;
    private boolean[] planetTaken;
    private Map<Player, BlockType[]> decisions;

    public PlanetsCard(
            @JsonProperty("id") int id,
            @JsonProperty("level") int level,
            @JsonProperty("planets") BlockType[][] planets,
            @JsonProperty("positionCost") int positionCost) {
        super(id, level);
        if (planets.length < 1 || planets.length > 4) throw new IllegalArgumentException("There must be between 1 and 4 planets.");
        this.planets = planets;
        if (positionCost < 0) throw new IllegalArgumentException("positionCost must be a positive number.");
        this.positionCost = positionCost;
    }

    @Override
    protected void onActivate() {
        this.playerQueue = board.getSortedPlayers();
        this.planetTaken = new boolean[planets.length];
        this.decisions = new HashMap<>();
        askFirstPlayer();
    }

    private void askFirstPlayer() {
        if(!playerQueue.isEmpty()) {
            Player player = playerQueue.getFirst();
            // TODO
        } else giveRewards();
    }

    private void askNextPlayer() {
        playerQueue.removeFirst();
        askFirstPlayer();
    }

    public void choosePlanet(int index) {
        if(index < 0 || index >= planets.length) throw new IllegalArgumentException("Planet index out of bounds.");
        if(planetTaken[index]) throw new IllegalArgumentException("Planet " + index + " is already taken.");
        if(playerQueue.isEmpty()) throw new IllegalStateException("No player available to make a choice.");
        planetTaken[index] = true;
        decisions.put(playerQueue.getFirst(), planets[index]);
        askNextPlayer();
    }

    public void refuse() {
        askNextPlayer();
    }

    private void giveRewards() {
        for(Player player : board.getSortedPlayers().reversed()) {
            if(decisions.containsKey(player)) {
                BlockType[] chosenPlanets = decisions.get(player);
                if(chosenPlanets == null) throw new IllegalArgumentException("A planet is null.");
                player.giveCargoReward(chosenPlanets);
                board.movePlayer(player, positionCost, BACKWARD);
            }
        }
    }
//    @Override
//    public void applyEffects() {
//        List<Player> players = board.getSortedPlayers();
//        boolean[] planetTaken = new boolean[planets.length];
//
//        for(Player player : players) {
//            int chosenPlanet = player.choosePlanet(planetTaken);
//            if(chosenPlanet != -1) {
//                if(planetTaken[chosenPlanet]) throw new IllegalArgumentException("Planet " + chosenPlanet + " is already taken.");
//                planetTaken[chosenPlanet] = true;
//
//                player.giveCargoReward(planets[chosenPlanet]);
//                board.movePlayer(player, positionCost, BACKWARD);
//            }
//        }
//    }

    @Override
    public String getCardType() {
        return "Planets";
    }
}
