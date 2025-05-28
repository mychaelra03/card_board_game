package it.polimi.galaxy_trucker.server.model.cards.projectile;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.ShipBoard;

import static it.polimi.galaxy_trucker.server.model.cards.projectile.Projectile.Size.LARGE;
import static it.polimi.galaxy_trucker.server.model.cards.projectile.Projectile.Size.SMALL;
import static it.polimi.galaxy_trucker.server.model.ShipBoard.DefendingCannonType.DOUBLE;
import static it.polimi.galaxy_trucker.server.model.ShipBoard.DefendingCannonType.SINGLE;

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

        if(player.isPathEmpty(this)) return false;

        if(getSize() == SMALL) {
            if(!player.hasExposedConnectorInPath(this)) return false;

            if(player.isSideShielded(getSource().toInt())) return true;

        } else if(getSize() == LARGE) {
            ShipBoard.DefendingCannonType cannonType = player.getDefendingCannonType(this);
            if(cannonType == SINGLE) return false;
            if(cannonType == DOUBLE) return true;
        }

        player.applyProjectile(this);
        return false;
    }
}
