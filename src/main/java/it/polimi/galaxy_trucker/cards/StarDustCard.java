package it.polimi.galaxy_trucker.cards;

import it.polimi.galaxy_trucker.GameBoard;
import it.polimi.galaxy_trucker.Player;

import java.util.List;

import static it.polimi.galaxy_trucker.GameBoard.Direction.*;

public class StarDustCard extends Card {
    // Players lose flight days based on exposed connectors.

    public StarDustCard(int ID, int level) {
        super(ID, level);
    }

    @Override
    public void applyEffects(GameBoard board) {
        List<Player> playersReversed = board.getSortedPlayers().reversed();
        for(Player player : playersReversed) {
            int exposedConnectors = player.getExposedConnectorsCount();
            board.movePlayer(player, exposedConnectors, BACKWARD);
        }
    }

    @Override
    public String getCardType() {
        return "Star Dust";
    }
}
