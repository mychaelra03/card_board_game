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

public class Meteor extends Projectile {

    public Meteor(@JsonProperty("size") Projectile.Size size, @JsonProperty("source") Projectile.Source source) {
        super(size, source);
    }

    @Override
    public boolean evaluateDefense(Player player, int diceSum) {
        // Returns true if the player have to use a battery to block the projectile.
        // It damages ship if not blocked.

        int index = diceSumToIndex(getSource(), diceSum);

        if(player.isPathEmpty(getSource(), index)) return false;

        if(getSize() == SMALL) {
            if(!player.hasExposedConnectorInPath(getSource(), index)) return false;

            if(player.isSideShielded(getSource().toInt())) {
                return true;
            }
        } else if(getSize() == LARGE) {
            ShipBoard.DefendingCannonType cannonType = player.getDefendingCannonType(getSource(), index);
            if(cannonType == SINGLE) return false;
            if(cannonType == DOUBLE) return true;
        }

        player.removeFirstTileInPath(getSource(), index);
        return false;
    }
}
