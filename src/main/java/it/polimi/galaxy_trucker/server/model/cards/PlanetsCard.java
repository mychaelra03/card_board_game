package it.polimi.galaxy_trucker.server.model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.server.events.flight.cards.CargoDecisionEvent;
import it.polimi.galaxy_trucker.server.events.flight.cards.PlanetSelectionEvent;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.tiles.BlockType;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static it.polimi.galaxy_trucker.server.model.cards.Card.InputState.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class PlanetsCard extends Card implements CargoHandlerCard {
    // Players can land to collect goods.

    private final BlockType[][] planets;
    private final int positionCost;

    private List<Player> playerQueue;

    private Player[] planetChosenBy;

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
        this.planetChosenBy = new Player[planets.length];
        setInputState(PLANET_SELECTION);

        dispatcher.fireEvent(new PlanetSelectionEvent(playerQueue.getFirst().getName(), planetChosenBy));
    }

    public void choosePlanet(Player caller, int index) {
        verifyInputState(PLANET_SELECTION);
        verifyPlayerTurn(caller, playerQueue);
        if(index < 0 || index >= planets.length) throw new IllegalArgumentException("Planet index out of bounds.");
        if (planetChosenBy[index] != null) {
            Logger.logWarning("Planet " + index + " is already taken.");
            return;
        }
        planetChosenBy[index] = caller;
        caller.setCargoPool(new ArrayList<>(Arrays.asList(planets[index])));
        playerQueue.removeFirst();

        if(!playerQueue.isEmpty()) {
            dispatcher.fireEvent(new PlanetSelectionEvent(playerQueue.getFirst().getName(), planetChosenBy));
        } else {
            setInputState(NONE);
            giveRewards();
        }
    }

    public void refusePlanet(Player caller) {
        verifyInputState(PLANET_SELECTION);
        verifyPlayerTurn(caller, playerQueue);
        playerQueue.removeFirst();

        if(!playerQueue.isEmpty()) {
            dispatcher.fireEvent(new PlanetSelectionEvent(playerQueue.getFirst().getName(), planetChosenBy));
        } else {
            setInputState(NONE);
            giveRewards();
        }
    }

    private void giveRewards() {
        verifyInputState(NONE);

        boolean anyAccepted = Arrays.stream(planetChosenBy).anyMatch(Objects::nonNull);
        if (!anyAccepted) {
            setInputState(FINISHED);
            return;
        }
        for (Player player : board.getSortedPlayers().reversed()) {
            if (Arrays.asList(planetChosenBy).contains(player)) {
                board.movePlayerBackward(player, positionCost);
            }
        }
        setInputState(CARGO_DECISION);
        Set<Player> accepted = Arrays.stream(planetChosenBy).filter(Objects::nonNull).collect(Collectors.toSet());
        dispatcher.fireEvent(new CargoDecisionEvent(accepted));
    }

    @Override
    public void loadCargo(Player caller, int blockIndex, Point tileCoords, int containerIndex) {
        verifyInputState(CARGO_DECISION);
        verifyPlayerInList(caller, Arrays.asList(planetChosenBy), "Player " + caller.getName() + " has no active cargo session.");

        caller.swapCargo(blockIndex, tileCoords, containerIndex);
    }

    @Override
    public void unloadCargo(Player caller, Point tileCoords, int containerIndex) {
        verifyInputState(CARGO_DECISION);
        verifyPlayerInList(caller, Arrays.asList(planetChosenBy), "Player " + caller.getName() + " has no active cargo session.");

        caller.unloadCargo(tileCoords, containerIndex);
    }

    @Override
    public void confirmDone(Player caller) {
        verifyInputState(CARGO_DECISION);
        verifyPlayerInList(caller, Arrays.asList(planetChosenBy), "Player " + caller.getName() + " has no active cargo session.");

        for (int i = 0; i < planetChosenBy.length; i++) {
            if (planetChosenBy[i] == caller) {
                planetChosenBy[i] = null;
            }
        }
        caller.clearCargoPool();

        boolean anyRemaining = Arrays.stream(planetChosenBy).anyMatch(Objects::nonNull);
        if (!anyRemaining) {
            setInputState(FINISHED);
        }
    }

    @Override
    public String getCardType() {
        return "Planets";
    }
}
