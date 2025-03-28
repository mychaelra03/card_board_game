package it.polimi.galaxy_trucker.Model.cards.projectile;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CannonShot extends Projectile {

    public CannonShot(
            @JsonProperty("size")Size size,
            @JsonProperty("direction") Source source) {
        super(size, source);
    }
}
