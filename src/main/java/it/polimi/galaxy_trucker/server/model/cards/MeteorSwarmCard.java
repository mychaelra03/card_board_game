package it.polimi.galaxy_trucker.server.model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.server.model.cards.projectile.Meteor;

import java.awt.*;
import java.util.*;
import java.util.List;

import static it.polimi.galaxy_trucker.server.model.cards.Card.InputState.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class MeteorSwarmCard extends Card {
    // Rolling dice determines if meteors hit your ship.

    private final Meteor[] meteors;

    private List<Meteor> meteorQueue;
    private Meteor currentMeteor;
    private Set<Player> playersAtRisk;

    public MeteorSwarmCard(
            @JsonProperty("id") int id,
            @JsonProperty("level") int level,
            @JsonProperty("meteors") Meteor[] meteors) {
        super(id, level);
        this.meteors = meteors;
    }

    @Override
    protected void onActivate() {
        this.meteorQueue = new LinkedList<>(Arrays.asList(meteors));
        applyMeteor();
    }

    private void applyMeteor() {
        verifyInputState(NONE);
        if(meteorQueue.isEmpty()) {
            setInputState(FINISHED);
            return;
        }
        List<Player> players = board.getSortedPlayers();
        this.currentMeteor = meteorQueue.getFirst();
        currentMeteor.randomizePathIndex();

        this.playersAtRisk = new HashSet<>();
        for(Player player : players) {
            if(currentMeteor.evaluateDefense(player)) {
                playersAtRisk.add(player);
                setInputState(BATTERY_DECISION);
            }
        }

        handleNextMeteorIfReady();
    }

    public void useBattery(Player caller, Point batteryCoords) {
        verifyInputState(BATTERY_DECISION);
        verifyPlayerInList(caller, playersAtRisk, "Player " + caller.getName() + " is not currently at risk.");

        caller.ensureBatteryRemovedFrom(batteryCoords);
        playersAtRisk.remove(caller);

        handleNextMeteorIfReady();
    }
    public void refuseBattery(Player caller) {
        verifyInputState(BATTERY_DECISION);
        verifyPlayerInList(caller, playersAtRisk, "Player " + caller.getName() + " is not currently at risk.");

        caller.removeFirstTileInPath(currentMeteor.getSource(), currentMeteor.getPathIndex());
        playersAtRisk.remove(caller);

        handleNextMeteorIfReady();
    }

    private void handleNextMeteorIfReady() {
        if(playersAtRisk.isEmpty()) {
            meteorQueue.removeFirst();
            setInputState(NONE);
            applyMeteor();
        }
    }

    @Override
    public String getCardType() {
        return "Meteor Swarm";
    }
}
