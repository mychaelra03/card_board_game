package it.polimi.galaxy_trucker.cards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CannonShot {
    private final ProjectileSize size;
    private final ProjectileDirection direction;

    @JsonCreator
    public CannonShot(@JsonProperty("size") ProjectileSize size,@JsonProperty("direction") ProjectileDirection direction) {
        this.size = size;
        this.direction = direction;
    }
    public ProjectileSize getSize() {
        return size;
    }
    public ProjectileDirection getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "CannonShot{" +
                "size=" + size +
                ", direction=" + direction +
                '}';
    }
}
