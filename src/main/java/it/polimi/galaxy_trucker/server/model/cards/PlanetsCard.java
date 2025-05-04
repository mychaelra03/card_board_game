package it.polimi.galaxy_trucker.server.model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.server.model.tiles.BlockType;

import java.awt.*;
import java.util.*;
import java.util.List;

import static it.polimi.galaxy_trucker.server.model.cards.Card.InputState.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class PlanetsCard extends Card {
    // Players can land to collect goods.

    private final BlockType[][] planets;
    private final int positionCost;

    private List<Player> playerQueue;

    private boolean[] planetTaken;
    private List<Player> acceptedPlayers;

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
        this.acceptedPlayers = new ArrayList<>();
        setInputState(PLANET_SELECTION);
    }

    public void choosePlanet(Player caller, int index) {
        verifyInputState(PLANET_SELECTION);
        verifyPlayerTurn(caller, playerQueue);
        if(index < 0 || index >= planets.length) throw new IllegalArgumentException("Planet index out of bounds.");
        if(planetTaken[index]) throw new IllegalArgumentException("Planet " + index + " is already taken.");
        planetTaken[index] = true;
        caller.setCargoPool(new ArrayList<>(Arrays.asList(planets[index])));
        acceptedPlayers.add(caller);
        playerQueue.removeFirst();
        if(playerQueue.isEmpty()) {
            setInputState(NONE);
            giveRewards();
        }
    }

    public void refusePlanet(Player caller) {
        verifyInputState(PLANET_SELECTION);
        verifyPlayerTurn(caller, playerQueue);
        playerQueue.removeFirst();
        if(playerQueue.isEmpty()) {
            setInputState(NONE);
            giveRewards();
        }
    }

    private void giveRewards() {
        verifyInputState(NONE);
        if(acceptedPlayers.isEmpty()) {
            setInputState(FINISHED);
            return;
        }
        for(Player player : board.getSortedPlayers().reversed()) {
            if(acceptedPlayers.contains(player)) {
                board.movePlayerBackward(player, positionCost);
            }
        }
        setInputState(CARGO_DECISION);
    }

    public void placeCargo(Player caller, int blockIndex, Point tileCoords, int containerIndex) {
        verifyInputState(CARGO_DECISION);
        verifyPlayerInList(caller, acceptedPlayers, "Player " + caller.getName() + " has no active cargo session.");

        caller.swapCargo(blockIndex, tileCoords, containerIndex);
    }

    public void unloadCargo(Player caller, Point tileCoords, int containerIndex) {
        verifyInputState(CARGO_DECISION);
        verifyPlayerInList(caller, acceptedPlayers, "Player " + caller.getName() + " has no active cargo session.");

        caller.unloadCargo(tileCoords, containerIndex);
    }

    public void confirmDone(Player caller) {
        verifyInputState(CARGO_DECISION);
        verifyPlayerInList(caller, acceptedPlayers, "Player " + caller.getName() + " has no active cargo session.");
        acceptedPlayers.remove(caller);
        caller.clearCargoPool();
        if(acceptedPlayers.isEmpty()) {
            setInputState(FINISHED);
        }
    }

    @Override
    public String getCardType() {
        return "Planets";
    }
}
