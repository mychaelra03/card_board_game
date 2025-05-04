package it.polimi.galaxy_trucker.server.events;

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
