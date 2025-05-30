package it.polimi.galaxy_trucker.common.messages.flight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.messages.GameMessage;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanetSelectionMessage implements GameMessage {

    private String playerName;
    private List<String> planetChosenBy;

    public PlanetSelectionMessage() {}

    public PlanetSelectionMessage(String playerName, List<String> planetChosenBy) {
        this.playerName = playerName;
        this.planetChosenBy = planetChosenBy;
    }

    public String getPlayerName() {
        return playerName;
    }
    public List<String> getPlanetChosenBy() {
        return planetChosenBy;
    }

    @Override
    public String getType() {
        return "planet_selection";
    }
}
