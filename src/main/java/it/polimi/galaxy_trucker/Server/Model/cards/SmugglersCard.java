package it.polimi.galaxy_trucker.Server.Model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.Server.Model.tiles.BlockType;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static it.polimi.galaxy_trucker.Server.Model.cards.Card.InputState.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class SmugglersCard extends AbstractEnemy {
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
    }

    public void placeCargo(Player caller, int blockIndex, Point tileCoords, int containerIndex) {
        verifyInputState(CARGO_DECISION);
        if(caller != rewardedPlayer) throw new IllegalArgumentException("Player " + caller.getName() + " has no active cargo session.");

        caller.swapCargo(blockIndex, tileCoords, containerIndex);
    }

    public void confirmDone(Player caller) {
        verifyInputState(CARGO_DECISION);
        if(caller != rewardedPlayer) throw new IllegalArgumentException("Player " + caller.getName() + " has no active cargo session.");
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
