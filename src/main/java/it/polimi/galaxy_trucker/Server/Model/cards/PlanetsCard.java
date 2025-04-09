package it.polimi.galaxy_trucker.Server.Model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.Server.Model.tiles.BlockType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static it.polimi.galaxy_trucker.GameModel.Direction.BACKWARD;

@JsonIgnoreProperties(ignoreUnknown = true)

public class PlanetsCard extends Card {
    // Players can land to collect goods.

    private final BlockType[][] planets;
    private final boolean[] planetTaken;
    private final int positionCost;
    private final Map<Player,Integer> playerDecision = new HashMap<>();

    public PlanetsCard(
            @JsonProperty("id") int id,
            @JsonProperty("level") int level,
            @JsonProperty("planets") BlockType[][] planets,
            @JsonProperty("positionCost") int positionCost) {
        super(id, level);
        if (planets.length < 1 || planets.length > 4) throw new IllegalArgumentException("There must be between 1 and 4 planets.");
        this.planets = planets;
        this.planetTaken = new boolean[planets.length];
        if (positionCost < 0) throw new IllegalArgumentException("positionCost must be a positive number.");
        this.positionCost = positionCost;
    }

    @Override
    public void applyEffects(GameModel board) {
        List<Player> players = board.getSortedPlayers();
        // boolean[] planetTaken = new boolean[planets.length];

        for(Player player : players) {
            int chosenPlanet = player.choosePlanet(planetTaken);
            if(chosenPlanet != -1) {
                if(planetTaken[chosenPlanet]) throw new IllegalArgumentException("Planet " + chosenPlanet + " is already taken.");
                planetTaken[chosenPlanet] = true;

                player.giveCargoReward(planets[chosenPlanet]);
                board.movePlayer(player, positionCost, BACKWARD);
            }
        }
    }

//    public boolean getPlanetAvailability(int index) {
//        if(index < 0 || index >= planetTaken.length) throw new IllegalArgumentException("Planet number out of bounds");
//        return planetTaken[index];
//    }

    public void choosePlanet(int chosenPlanet, Player play) {
        if(chosenPlanet < -1 || chosenPlanet >= planetTaken.length) throw new IllegalArgumentException("Planet number out of bounds");
        if(chosenPlanet != -1 && planetTaken[chosenPlanet]) throw new IllegalArgumentException("Planet already taken");


    }
    public boolean[] getPlanetTaken() {
        return planetTaken;
    }

    @Override
    public String getCardType() {
        return "Planets";
    }
}
