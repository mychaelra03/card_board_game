package it.polimi.galaxy_trucker.server.model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.server.events.flight.cards.CargoDecisionEvent;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.tiles.BlockType;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import static it.polimi.galaxy_trucker.server.model.cards.Card.InputState.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class SmugglersCard extends AbstractEnemy implements CargoHandlerCard {
    // Combat encounter, win to gain goods, lose to lose goods.

    private final BlockType[] rewardCargo;
    private final int cargoLossOnDefeat;

    private Player rewardedPlayer;

    public SmugglersCard(
            @JsonProperty("id") int id,
            @JsonProperty("level") int level,
            @JsonProperty("requiredCannonPower") int requiredCannonPower,
            @JsonProperty("rewardCargo")BlockType[] rewardCargo,
            @JsonProperty("positionCost")int positionCost,
            @JsonProperty("cargoLossOnDefeat")int cargoLossOnDefeat) {
        super(id, level, requiredCannonPower, positionCost);
        this.rewardCargo = rewardCargo;
        if (cargoLossOnDefeat < 0) throw new IllegalArgumentException("cargoLossOnDefeat must be a positive number.");
        this.cargoLossOnDefeat = cargoLossOnDefeat;
    }

    @Override
    protected void giveReward(Player player) {
        verifyInputState(NONE);
        this.rewardedPlayer = player;
        player.setCargoPool(new ArrayList<>(Arrays.asList(rewardCargo)));
        setInputState(CARGO_DECISION);

        dispatcher.fireEvent(new CargoDecisionEvent(Set.of(player)));
    }

    @Override
    public void loadCargo(Player caller, int blockIndex, Point tileCoords, int containerIndex) {
        verifyInputState(CARGO_DECISION);
        verifyCaller(caller, rewardedPlayer);

        caller.swapCargo(blockIndex, tileCoords, containerIndex);
    }
    @Override
    public void unloadCargo(Player caller, Point tileCoords, int containerIndex) {
        verifyInputState(CARGO_DECISION);
        verifyCaller(caller, rewardedPlayer);

        caller.unloadCargo(tileCoords, containerIndex);
    }
    @Override
    public void confirmDone(Player caller) {
        verifyInputState(CARGO_DECISION);
        verifyCaller(caller, rewardedPlayer);
        caller.clearCargoPool();
        setInputState(FINISHED);
    }

    @Override
    protected void applyPunishment(Player player) {
        verifyInputState(NONE);
        player.removeMostValuableCargo(cargoLossOnDefeat);
        handleNextPlayer();
    }

    @Override
    public String getCardType() {
        return "Smugglers";
    }
}
