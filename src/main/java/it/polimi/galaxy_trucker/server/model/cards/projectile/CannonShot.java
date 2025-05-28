package it.polimi.galaxy_trucker.server.model.cards.projectile;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.server.model.Player;

import static it.polimi.galaxy_trucker.server.model.cards.projectile.Projectile.Size.SMALL;

public class CannonShot extends Projectile {

    public CannonShot(@JsonProperty("size") Size size, @JsonProperty("direction") Source source) {
        super(size, source);
    }
    public CannonShot(Size size, Source source, int pathIndex) {
        super(size, source, pathIndex);
    }

    @Override
    public boolean evaluateDefense(Player player) {
        // Returns true if the player have to use a battery to block the projectile.
        // It damages ship if not blocked.

        if(player.isPathEmpty(this)) return false;

        if(getSize() == SMALL && player.isSideShielded(getSource().toInt())) return true;

        player.applyProjectile(this);
        return false;
    }
}
