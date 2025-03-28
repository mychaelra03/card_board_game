package it.polimi.galaxy_trucker.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.GameBoard;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.tiles.CannonTile;

import java.util.List;

import static it.polimi.galaxy_trucker.GameBoard.Direction.*;

public abstract class AbstractEnemy extends Card {

    private final int requiredCannonPower;
    private final int positionCost;

    public AbstractEnemy(int id, int level, int requiredCannonPower, int positionCost) {
        super(id, level);
        if (requiredCannonPower < 0) throw new IllegalArgumentException("requiredCannonPower must be a positive number.");
        this.requiredCannonPower = requiredCannonPower;
        if (positionCost < 0) throw new IllegalArgumentException("positionCost must be a positive number.");
        this.positionCost = positionCost;
    }

    public abstract void giveReward(Player player);
    public abstract void applyPunishment(Player player);

    @Override
    public void applyEffects(GameBoard board) {
        List<Player> players = board.getSortedPlayers();
        for(Player player : players) {
            if(player.getMaxCannonStrength() < requiredCannonPower) {
                applyPunishment(player);
                continue;
            }
            player.chooseTilesToActivate(CannonTile.class);
            double currentStrength = player.getCurrentStrength(CannonTile.class);
            if(currentStrength > requiredCannonPower) {

                boolean accepted = player.acceptsCardEffect();

                if(accepted) {
                    giveReward(player);
                    board.movePlayer(player, positionCost, BACKWARD);
                }
                break;
            }
            if(currentStrength < requiredCannonPower) {
                applyPunishment(player);
            }
        }
    }
}
