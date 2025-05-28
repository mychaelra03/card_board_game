package it.polimi.galaxy_trucker.server.events.gameOver;

import it.polimi.galaxy_trucker.common.messages.endGame.PlayerScores;
import it.polimi.galaxy_trucker.server.events.EventModel;

import java.util.List;

public class GameOverEvent extends EventModel {

    private final List<PlayerScores> playersScores;

    public GameOverEvent(List<PlayerScores> playersScores) {
        this.playersScores = playersScores;
    }

    public List<PlayerScores> getPlayersScores() {
        return playersScores;
    }
}

