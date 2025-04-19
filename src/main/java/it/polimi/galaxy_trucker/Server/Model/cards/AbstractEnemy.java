package it.polimi.galaxy_trucker.Server.Model.cards;

import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.Server.Model.tiles.CannonTile;

import java.awt.*;
import java.util.List;

import static it.polimi.galaxy_trucker.GameModel.Direction.*;
import static it.polimi.galaxy_trucker.Server.Model.cards.Card.InputState.*;

public abstract class AbstractEnemy extends Card {

    private final int requiredCannonPower;
    private final int positionCost;

    private List<Player> playerQueue;

    public AbstractEnemy(int id, int level, int requiredCannonPower, int positionCost) {
        super(id, level);
        if (requiredCannonPower < 0) throw new IllegalArgumentException("requiredCannonPower must be a positive number.");
        this.requiredCannonPower = requiredCannonPower;
        if (positionCost < 0) throw new IllegalArgumentException("positionCost must be a positive number.");
        this.positionCost = positionCost;
    }

    protected abstract void giveReward(Player player);
    protected abstract void applyPunishment(Player player);

    @Override
    protected void onActivate() {
        playerQueue = board.getSortedPlayers();
        handlePlayer();
    }

    private void handlePlayer() {
        verifyInputState(NONE);
        if(playerQueue.isEmpty()) {
            setInputState(FINISHED);
            return;
        }
        Player currentPlayer = playerQueue.getFirst();
        if(currentPlayer.getMaxStrength(CannonTile.class) < requiredCannonPower) {
            applyPunishment(currentPlayer);
        } else {
            setInputState(CANNON_SELECTION);
        }
    }

    protected void handleNextPlayer() {
        playerQueue.removeFirst();
        handlePlayer();
    }

    public void activateCannons(Player caller, int verticalCannons, int rotatedCannons, List<Point> batteryTiles) {
        verifyInputState(CANNON_SELECTION);
        verifyPlayerTurn(caller, playerQueue);
        if(verticalCannons + rotatedCannons != batteryTiles.size()) throw new IllegalArgumentException("The number of activated cannons does not match the number of batteries used.");
        double strength = caller.getStrength(CannonTile.class, verticalCannons, rotatedCannons);
        caller.removeBatteries(batteryTiles);
        if(strength > requiredCannonPower) {
            setInputState(REWARD_DECISION);
        } else if (strength < requiredCannonPower) {
            setInputState(NONE);
            applyPunishment(caller);
        } else {
            handleNextPlayer();
        }
    }

    public void acceptReward(Player caller) {
        verifyInputState(REWARD_DECISION);
        verifyPlayerTurn(caller, playerQueue);
        board.movePlayer(caller, positionCost, BACKWARD);
        playerQueue.clear();
        setInputState(NONE);
        giveReward(caller);
    }
    public void refuseReward(Player caller) {
        verifyInputState(REWARD_DECISION);
        verifyPlayerTurn(caller, playerQueue);
        playerQueue.clear();
        setInputState(FINISHED);
    }
}
