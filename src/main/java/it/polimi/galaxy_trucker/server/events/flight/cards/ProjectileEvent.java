package it.polimi.galaxy_trucker.server.events.flight.cards;

import it.polimi.galaxy_trucker.server.events.EventModel;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.cards.projectile.Projectile;

import java.awt.*;

public class ProjectileEvent extends EventModel {

    private final Player player;
    private final Projectile projectile;
    private final Point hitPoint;
    private final boolean defended;

    public ProjectileEvent(Player player, Projectile projectile, Point hitPoint, boolean defended) {
        this.player = player;
        this.projectile = projectile;
        this.hitPoint = hitPoint;
        this.defended = defended;
    }

    public Player getPlayer() {
        return player;
    }

    public Projectile getProjectile() {
        return projectile;
    }

    public Point getHitPoint() {
        return hitPoint;
    }

    public boolean isDefended() {
        return defended;
    }
}
