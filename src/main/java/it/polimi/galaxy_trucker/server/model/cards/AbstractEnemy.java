package it.polimi.galaxy_trucker.server.model.cards;

import it.polimi.galaxy_trucker.server.events.flight.cards.CannonSelectionEvent;
import it.polimi.galaxy_trucker.server.events.flight.cards.CardDecisionEvent;
import it.polimi.galaxy_trucker.server.events.flight.cards.DecisionEndEvent;
import it.polimi.galaxy_trucker.server.events.flight.cards.PlayerStatusEvent;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.tiles.CannonTile;

import java.awt.*;
import java.util.List;

import static it.polimi.galaxy_trucker.server.model.cards.Card.InputState.*;

public abstract class AbstractEnemy extends Card implements CannonsHandlerCard, DecisionHandlerCard {

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
            dispatcher.fireEvent(new PlayerStatusEvent(currentPlayer.getName(), PlayerStatusEvent.Status.PUNISHED));
            applyPunishment(currentPlayer);
        }
        else if(currentPlayer.getMinStrength(CannonTile.class) > requiredCannonPower) {
            setInputState(REWARD_DECISION);
            dispatcher.fireEvent(new PlayerStatusEvent(currentPlayer.getName(), PlayerStatusEvent.Status.REWARDED));
            dispatcher.fireEvent(new CardDecisionEvent(currentPlayer.getName()));
        }
        else {
            setInputState(CANNON_SELECTION);
            dispatcher.fireEvent(new CannonSelectionEvent(currentPlayer));
        }
    }

    protected void handleNextPlayer() {
        playerQueue.removeFirst();
        setInputState(NONE);
        handlePlayer();
    }

    @Override
    public void activateCannons(Player caller, int verticalCannons, int rotatedCannons, List<Point> batteryTiles) {
        verifyInputState(CANNON_SELECTION);
        verifyPlayerTurn(caller, playerQueue);
        dispatcher.fireEvent(new DecisionEndEvent(caller.getName()));
        if(verticalCannons + rotatedCannons != batteryTiles.size()) throw new IllegalArgumentException("The number of activated cannons does not match the number of batteries used.");
        double strength = caller.getStrength(CannonTile.class, verticalCannons, rotatedCannons);
        caller.removeBatteries(batteryTiles);
        if(strength > requiredCannonPower) {
            setInputState(REWARD_DECISION);

            dispatcher.fireEvent(new PlayerStatusEvent(caller.getName(), PlayerStatusEvent.Status.REWARDED));
            dispatcher.fireEvent(new CardDecisionEvent(caller.getName()));
        } else if (strength < requiredCannonPower) {
            setInputState(NONE);
            dispatcher.fireEvent(new PlayerStatusEvent(caller.getName(), PlayerStatusEvent.Status.PUNISHED));
            applyPunishment(caller);
        } else {
            dispatcher.fireEvent(new PlayerStatusEvent(caller.getName(), PlayerStatusEvent.Status.SKIPPED_ENEMY));
            handleNextPlayer();
        }
    }

    @Override
    public void accept(Player caller) {
        verifyInputState(REWARD_DECISION);
        verifyPlayerTurn(caller, playerQueue);
        board.movePlayerBackward(caller, positionCost);
        playerQueue.clear();
        setInputState(NONE);
        giveReward(caller);
    }
    @Override
    public void refuse(Player caller) {
        verifyInputState(REWARD_DECISION);
        verifyPlayerTurn(caller, playerQueue);
        playerQueue.clear();
        setInputState(FINISHED);
    }
}
