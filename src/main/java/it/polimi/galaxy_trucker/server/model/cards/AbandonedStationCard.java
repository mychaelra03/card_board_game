package it.polimi.galaxy_trucker.server.model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.tiles.BlockType;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.polimi.galaxy_trucker.server.model.cards.Card.InputState.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class AbandonedStationCard extends Card {
    // Collect goods if you have enough crew.

    private final int requiredCrew;
    private final BlockType[] rewardCargo;
    private final int positionCost;

    private List<Player> playerQueue;

    private Player rewardedPlayer;

    public AbandonedStationCard(
            @JsonProperty("id") int id,
            @JsonProperty("level") int level,
            @JsonProperty("requiredCrew") int requiredCrew,
            @JsonProperty("rewardCargo") BlockType[] rewardCargo,
            @JsonProperty("positionCost") int positionCost) {
        super(id, level);
        if (requiredCrew < 0) throw new IllegalArgumentException("positionCost must be a positive number.");
        this.requiredCrew = requiredCrew;
        this.rewardCargo = rewardCargo;
        if (positionCost < 0) throw new IllegalArgumentException("positionCost must be a positive number.");
        this.positionCost = positionCost;
    }

    @Override
    protected void onActivate() {
        this.playerQueue = board.getSortedPlayers();
        setInputState(REWARD_DECISION);
        skipIneligiblePlayers();
    }

    public void accept(Player caller) {
        verifyInputState(REWARD_DECISION);
        verifyPlayerTurn(caller, playerQueue);
        board.movePlayerBackward(caller, positionCost);
        this.rewardedPlayer = caller;
        caller.setCargoPool(new ArrayList<>(Arrays.asList(rewardCargo)));
        playerQueue.clear();
        setInputState(CARGO_DECISION);
    }

    public void refuse(Player caller) {
        verifyInputState(REWARD_DECISION);
        verifyPlayerTurn(caller, playerQueue);
        playerQueue.removeFirst();
        skipIneligiblePlayers();
    }

    private void skipIneligiblePlayers() {
        verifyInputState(REWARD_DECISION);
        while(!playerQueue.isEmpty() && playerQueue.getFirst().getCrewCount() < requiredCrew) {
            playerQueue.removeFirst();
        }
        if(playerQueue.isEmpty()) {
            setInputState(FINISHED);
        }
    }

    public void loadCargo(Player caller, int blockIndex, Point tileCoords, int containerIndex) {
        verifyInputState(CARGO_DECISION);
        verifyCaller(caller, rewardedPlayer);

        caller.swapCargo(blockIndex, tileCoords, containerIndex);
    }

    public void unloadCargo(Player caller, Point tileCoords, int containerIndex) {
        verifyInputState(CARGO_DECISION);
        verifyCaller(caller, rewardedPlayer);

        caller.unloadCargo(tileCoords, containerIndex);
    }

    public void confirmDone(Player caller) {
        verifyInputState(CARGO_DECISION);
        verifyCaller(caller, rewardedPlayer);
        caller.clearCargoPool();
        setInputState(FINISHED);
    }

    @Override
    public String getCardType() {
        return "Abandoned Station";
    }
}
