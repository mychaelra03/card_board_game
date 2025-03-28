package it.polimi.galaxy_trucker.cards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.GameModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenSpaceCard extends Card {
    // Move forward based on engine strength.

    @JsonCreator
    public OpenSpaceCard(@JsonProperty("id") int id,@JsonProperty("level") int level) {
        super(id, level);
    }

    @Override
    public void applyEffects(GameModel board) {

    }

    @Override
    public String getCardType() {
        return "Open Space";
    }
}
