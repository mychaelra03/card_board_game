package it.polimi.galaxy_trucker.Model.cards.projectile;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Meteor extends Projectile {

    public Meteor(
            @JsonProperty("size") Projectile.Size size,
            @JsonProperty("source") Projectile.Source source) {
        super(size, source);
    }
}
