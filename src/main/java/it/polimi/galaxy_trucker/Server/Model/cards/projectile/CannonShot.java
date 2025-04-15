package it.polimi.galaxy_trucker.Server.Model.cards.projectile;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.ShipBoard;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static it.polimi.galaxy_trucker.Server.Model.cards.projectile.Projectile.Size.LARGE;
import static it.polimi.galaxy_trucker.Server.Model.cards.projectile.Projectile.Size.SMALL;
import static it.polimi.galaxy_trucker.ShipBoard.DefendingCannonType.DOUBLE;
import static it.polimi.galaxy_trucker.ShipBoard.DefendingCannonType.SINGLE;

public class CannonShot extends Projectile {

    public CannonShot(@JsonProperty("size") Size size, @JsonProperty("direction") Source source) {
        super(size, source);
    }

    @Override
    public boolean evaluateDefense(Player player, int pathIndex) {
        // Returns true if the player have to use a battery to block the projectile.
        // It damages ship if not blocked.

        if(player.isPathEmpty(getSource(), pathIndex)) return false;

        if(getSize() == SMALL && player.isSideShielded(getSource().toInt())) return true;

        player.removeFirstTileInPath(getSource(), pathIndex);
        return false;
    }
}
