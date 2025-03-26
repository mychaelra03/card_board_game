package it.polimi.galaxy_trucker.cards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.GameBoard;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MeteorSwarmCard extends Card {
    // Rolling dice determines if meteors hit your ship.

    // TODO: check if Meteor should be an inner class or not (it's utilized only in here)

    public static class Meteor {
        private final ProjectileSize size;
        private final ProjectileDirection direction;

        @JsonCreator
        public Meteor(@JsonProperty("size") ProjectileSize size,@JsonProperty("direction") ProjectileDirection direction) {
            this.size = size;
            this.direction = direction;
        }
        public ProjectileSize getSize() {
            return size;
        }
        public ProjectileDirection getDirection() {
            return direction;
        }

    }
    private final Meteor[] meteors;

    @JsonCreator
    public MeteorSwarmCard(@JsonProperty("id") int id,@JsonProperty("level") int level,@JsonProperty("meteors") Meteor[] meteors) {
        super(id, level);
        this.meteors = meteors;
    }

    @Override
    public void applyEffects(GameBoard board) {

    }

    @Override
    public String getCardType() {
        return "Meteor Swarm";
    }


}
