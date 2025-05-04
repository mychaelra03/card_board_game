package it.polimi.galaxy_trucker.server.model.cards.projectile;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.ShipBoard;

import static it.polimi.galaxy_trucker.server.model.cards.projectile.Projectile.Size.LARGE;
import static it.polimi.galaxy_trucker.server.model.cards.projectile.Projectile.Size.SMALL;
import static it.polimi.galaxy_trucker.ShipBoard.DefendingCannonType.DOUBLE;
import static it.polimi.galaxy_trucker.ShipBoard.DefendingCannonType.SINGLE;

public class Meteor extends Projectile {

    public Meteor(@JsonProperty("size") Projectile.Size size, @JsonProperty("source") Projectile.Source source) {
        super(size, source);
    }
    public Meteor(Size size, Source source, int pathIndex) {
        super(size, source, pathIndex);
    }

    @Override
    public boolean evaluateDefense(Player player) {
        // Returns true if the player have to use a battery to block the projectile.
        // It damages ship if not blocked.

        if(player.isPathEmpty(getSource(), getPathIndex())) return false;

        if(getSize() == SMALL) {
            if(!player.hasExposedConnectorInPath(getSource(), getPathIndex())) return false;

            if(player.isSideShielded(getSource().toInt())) {
                return true;
            }
        } else if(getSize() == LARGE) {
            ShipBoard.DefendingCannonType cannonType = player.getDefendingCannonType(getSource(), getPathIndex());
            if(cannonType == SINGLE) return false;
            if(cannonType == DOUBLE) return true;
        }

        player.removeFirstTileInPath(getSource(), getPathIndex());
        return false;
    }
}
