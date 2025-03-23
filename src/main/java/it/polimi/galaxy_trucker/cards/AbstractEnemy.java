package it.polimi.galaxy_trucker.cards;

import it.polimi.galaxy_trucker.GameBoard;
import it.polimi.galaxy_trucker.Player;

public abstract class AbstractEnemy extends Card {

    private final int requiredCannonPower;
    private final int positionCost;

    public AbstractEnemy(int ID, int level, int requiredCannonPower, int positionCost) {
        super(ID, level);
        if (requiredCannonPower < 0) throw new IllegalArgumentException("requiredCannonPower must be a positive number.");
        this.requiredCannonPower = requiredCannonPower;
        if (positionCost < 0) throw new IllegalArgumentException("positionCost must be a positive number.");
        this.positionCost = positionCost;
    }

    public abstract void giveReward(Player player);
    public abstract void applyPunishment(Player player);

    @Override
    public void applyEffects(GameBoard board) {

    }
}
