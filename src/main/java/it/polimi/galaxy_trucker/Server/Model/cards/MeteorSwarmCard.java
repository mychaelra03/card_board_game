package it.polimi.galaxy_trucker.Server.Model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.Server.Model.cards.projectile.Meteor;
import it.polimi.galaxy_trucker.Server.Model.cards.projectile.Projectile;

import java.awt.*;
import java.util.*;
import java.util.List;

import static it.polimi.galaxy_trucker.Server.Model.cards.Card.InputState.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class MeteorSwarmCard extends Card {
    // Rolling dice determines if meteors hit your ship.

    private final Meteor[] meteors;

    private List<Meteor> meteorQueue;
    private Meteor currentMeteor;
    private int currentPathIndex;
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
        this.currentMeteor = meteorQueue.getFirst();
        List<Player> players = board.getSortedPlayers();
        int[] dice = GameModel.rollTwoDice();
        this.currentPathIndex = Projectile.diceSumToIndex(currentMeteor.getSource(), dice[0] + dice[1]);

        this.playersAtRisk = new HashSet<>(players);
        for(Player player : players) {
            if(currentMeteor.evaluateDefense(player, currentPathIndex)) {
                playersAtRisk.add(player);
                setInputState(BATTERY_DECISION);
            }
        }
        handleNextMeteorIfReady();
    }

    public void useBattery(Player caller, Point batteryCoords) {
        verifyInputState(BATTERY_DECISION);
        if(!playersAtRisk.contains(caller)) throw new IllegalArgumentException(caller.getName() + " is not currently at risk.");

        if(!caller.removeBatteryFrom(batteryCoords.x, batteryCoords.y)) throw new IllegalArgumentException("No battery found at " + batteryCoords + ".");
        playersAtRisk.remove(caller);

        handleNextMeteorIfReady();
    }
    public void refuseBattery(Player caller) {
        verifyInputState(BATTERY_DECISION);
        if(!playersAtRisk.contains(caller)) throw new IllegalArgumentException(caller.getName() + " is not currently at risk.");

        caller.removeFirstTileInPath(currentMeteor.getSource(), currentPathIndex);
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
