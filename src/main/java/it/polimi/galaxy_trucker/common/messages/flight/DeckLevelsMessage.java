package it.polimi.galaxy_trucker.common.messages.flight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.messages.GameMessage;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeckLevelsMessage implements GameMessage {

    private List<Integer> deckLevels;

    public DeckLevelsMessage() {}

    public DeckLevelsMessage(List<Integer> deckLevels) {
        this.deckLevels = deckLevels;
    }

    public List<Integer> getDeckLevels() {
        return deckLevels;
    }

    @Override
    public String getType() {
        return "deckLevels";
    }
}
