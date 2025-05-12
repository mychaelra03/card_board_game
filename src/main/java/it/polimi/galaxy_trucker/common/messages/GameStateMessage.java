package it.polimi.galaxy_trucker.common.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.GameModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameStateMessage implements GameMessage {

    private GameModel.GameState gameState;

    public GameStateMessage() {}

    public GameStateMessage(GameModel.GameState gameState) {
       this.gameState = gameState;
    }

    public GameModel.GameState getGameState() {
       return gameState;
    }

    @Override
    public String getType() {
        return "gameState";
    }
}
