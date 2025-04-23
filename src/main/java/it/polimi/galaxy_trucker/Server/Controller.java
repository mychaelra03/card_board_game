package it.polimi.galaxy_trucker.Server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.Common.ChatMessage;
import it.polimi.galaxy_trucker.Common.PlayerInfo;
import it.polimi.galaxy_trucker.Common.PlayersInfoMessage;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.Server.Event.PlayerJoinLeaveEvent;

import java.util.HashMap;
import java.util.Map;

public class Controller {
    GameModel gameModel = null;
    private Map<VirtualView, Player> playerMap = new HashMap<>();

    public Controller(GameModel gameModel) {
        this.gameModel = gameModel;

        gameModel.addEventListener(PlayerJoinLeaveEvent.class, (event) -> {
            try {
                this.handlePlayerJoinLeave(event);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void handlePlayerJoinLeave(PlayerJoinLeaveEvent event) throws JsonProcessingException {
        for( VirtualView vw : playerMap.keySet() ) {
            vw.sendPlayerAdded(new PlayersInfoMessage(event.getPlayerInfoList(), event.getUsername()));
        }
    }

    public void processCmd(VirtualView virtualView, String json) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode cmd = mapper.readTree(json);
        String type = cmd.get("type").asText();

        switch (type) {
            case "playerInfo":

                PlayerInfo playerInfo = mapper.convertValue(cmd, PlayerInfo.class);
                Player player = new Player(playerInfo.getUsername(), playerInfo.getProfilePicIds());

                playerMap.put(virtualView, player);
                gameModel.addPlayer(player);
                break;

            case "chatMessage":

                ChatMessage chatMessage = mapper.convertValue(cmd, ChatMessage.class);
                ChatMessage chatMessageUsername = new ChatMessage(this.playerMap.get(virtualView).getName(), chatMessage.getMessage());
                String messageInfo = mapper.writeValueAsString(chatMessageUsername);

                virtualView.broadcastMessage(messageInfo);
        }




    }

//    void choosePlanet(int chosenPlanet) {
//        if(chosenPlanet < -1 || chosenPlanet >= ((PlanetsCard)gameModel.getCurrentCard()).getPlanetTaken().length) throw new IllegalArgumentException("Planet number out of bounds");
//    }
//


    public void clientDisconnect(VirtualView virtualView){
        this.gameModel.removePlayer(playerMap.get(virtualView));
    }




}
