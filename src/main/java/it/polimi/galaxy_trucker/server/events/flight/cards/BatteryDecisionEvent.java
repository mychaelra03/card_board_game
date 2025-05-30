package it.polimi.galaxy_trucker.server.events.flight.cards;

import it.polimi.galaxy_trucker.server.events.EventModel;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.cards.projectile.Projectile;

public class BatteryDecisionEvent extends EventModel {

    private final Player player;
    private final Projectile projectile;

    public BatteryDecisionEvent(Player player, Projectile projectile) {
        this.player = player;
        this.projectile = projectile;
    }

    public Player getPlayer() {
        return player;
    }

    public Projectile getProjectile() {
        return projectile;
    }
}
