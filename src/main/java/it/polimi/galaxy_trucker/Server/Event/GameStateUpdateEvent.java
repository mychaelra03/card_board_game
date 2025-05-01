package it.polimi.galaxy_trucker.Server.Event;

import it.polimi.galaxy_trucker.BoardType;
import it.polimi.galaxy_trucker.GameModel;

public class GameStateUpdateEvent extends EventModel {
    public final GameModel.GameState gameState;

    public GameStateUpdateEvent(GameModel.GameState gameState) {
        this.gameState = gameState;
    }

    public GameModel.GameState getGameState() {
        return gameState;
    }
}
