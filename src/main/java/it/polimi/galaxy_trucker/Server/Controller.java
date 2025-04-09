package it.polimi.galaxy_trucker.Server;

import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.Server.Model.cards.PlanetsCard;

import java.beans.PropertyChangeListener;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Controller {
    GameModel gameModel = null;
    private Map<VirtualView, Player> playerMap = new HashMap<>();

    public Controller(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    public void registerPlayer(VirtualView virtualView, String username) {
        Player player = new Player( username);
        playerMap.put(virtualView, player);
        gameModel.addPlayer(player);
    }

//    void choosePlanet(int chosenPlanet) {
//        if(chosenPlanet < -1 || chosenPlanet >= ((PlanetsCard)gameModel.getCurrentCard()).getPlanetTaken().length) throw new IllegalArgumentException("Planet number out of bounds");
//    }
//




    public void setListener(PropertyChangeListener listener) {
        this.gameModel.setListener(listener);
    }

}
