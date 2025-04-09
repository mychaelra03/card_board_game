package it.polimi.galaxy_trucker.Server;

import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Player;

import java.beans.PropertyChangeListener;

public class Controller {
    GameModel gameModel = null;

    public Controller(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    String processCmd(String cmd, String cmd2) {
        Player player = new Player(cmd, new int[]{0, 0});

        gameModel.addPlayer(player);

        return player.getName();
    }

    public void setListener(PropertyChangeListener listener) {
        this.gameModel.setListener(listener);
    }

}
