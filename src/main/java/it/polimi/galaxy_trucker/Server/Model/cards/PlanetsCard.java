package it.polimi.galaxy_trucker.Server.Model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.Server.Model.tiles.BlockType;

import java.util.List;

import static it.polimi.galaxy_trucker.GameModel.Direction.BACKWARD;

@JsonIgnoreProperties(ignoreUnknown = true)

public class PlanetsCard extends Card {
    // Players can land to collect goods.

    private final BlockType[][] planets;
    private final int positionCost;

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
    public void applyEffects(GameModel board) {
        List<Player> players = board.getSortedPlayers();
        boolean[] planetTaken = new boolean[planets.length];

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

    @Override
    public String getCardType() {
        return "Planets";
    }
}
