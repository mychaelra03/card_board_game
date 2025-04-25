package it.polimi.galaxy_trucker.Server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.Common.ChatMessage;
import it.polimi.galaxy_trucker.Common.LobbyJoinResponse;
import it.polimi.galaxy_trucker.Common.PlayerInfo;
import it.polimi.galaxy_trucker.Common.PlayersInfoMessage;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.Server.Event.PlayersUpdateEvent;
import it.polimi.galaxy_trucker.Server.Event.PlayerKickedEvent;

import java.util.HashMap;
import java.util.Map;

public class Controller {
    GameModel gameModel = null;
    private Map<VirtualView, Player> playerMap = new HashMap<>();

    public Controller(GameModel gameModel) {
        this.gameModel = gameModel;

        gameModel.addEventListener(PlayersUpdateEvent.class, (event) -> {
            try {
                this.handlePlayersUpdate(event);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        gameModel.addEventListener(PlayerKickedEvent.class, (event) -> {
            try{
                this.handlePlayerKicked(event);
            } catch (JsonProcessingException e){
                throw new RuntimeException(e);
            }
        });


    }

    public VirtualView getVirtualViewByPlayer(Player targetPlayer) {
        for (Map.Entry<VirtualView, Player> entry : playerMap.entrySet()) {
            if (entry.getValue().getName().equals(targetPlayer.getName())) {
                return entry.getKey();
            }
        }
        System.err.println("No VirtualView found for player: " + targetPlayer.getName());
        return null;
    }


    private void handlePlayerKicked(PlayerKickedEvent event) throws JsonProcessingException {
            PlayerInfo playerInfo = event.getPlayerInfo();
            playerInfo.setType("playerKicked");

        Player kickedPlayer = new Player(playerInfo.getUsername(), playerInfo.getProfilePicIds());

        VirtualView viewToKick = getVirtualViewByPlayer(kickedPlayer);

        if (viewToKick != null) {
            viewToKick.sendPlayerKicked(playerInfo);
            viewToKick.close();
        } else {
            System.err.println("No VirtualView found for: " + playerInfo.getUsername());
        }
    }

    public void handlePlayersUpdate(PlayersUpdateEvent event) throws JsonProcessingException {
        for(VirtualView vw : playerMap.keySet()) {
            PlayersInfoMessage playersInfoMessage = new PlayersInfoMessage(event.getPlayerInfoList(), event.getUsername());

            vw.sendPlayerAdded(playersInfoMessage);
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

                LobbyJoinResponse.JoinStatus result = gameModel.canAddPlayer(player);

                LobbyJoinResponse joinResponse = new LobbyJoinResponse(result);
                String responseJson = mapper.writeValueAsString(joinResponse);
                virtualView.out.println(responseJson);

                if(result == LobbyJoinResponse.JoinStatus.SUCCESS) {
                    playerMap.put(virtualView, player);
                    gameModel.addPlayer(player);
                }
                else virtualView.close();
                break;

            case "chatMessage":

                ChatMessage chatMessage = mapper.convertValue(cmd, ChatMessage.class);
                ChatMessage chatMessageUsername = new ChatMessage(this.playerMap.get(virtualView).getName(), this.playerMap.get(virtualView).getColor().toString(), chatMessage.getMessage());

                String messageInfo = mapper.writeValueAsString(chatMessageUsername);

                virtualView.broadcastMessage(messageInfo);
                break;

            case "kickPlayer":

                PlayerInfo kickedPlayerInfo = mapper.convertValue(cmd, PlayerInfo.class);
                Player kickedPlayer = new Player(kickedPlayerInfo.getUsername(), kickedPlayerInfo.getProfilePicIds());


                VirtualView viewToKick = getVirtualViewByPlayer(kickedPlayer);
                viewToKick.close();

                playerMap.entrySet().removeIf(entry -> entry.getValue().getName().equals(kickedPlayer.getName()));
                this.gameModel.removePlayer(kickedPlayer);
                break;

                case "promotePlayer":

                    PlayerInfo promotedPlayerInfo = mapper.convertValue(cmd, PlayerInfo.class);
                    Player promotedPlayer = new Player(promotedPlayerInfo.getUsername(), promotedPlayerInfo.getProfilePicIds());

                    gameModel.setNewAdmin(promotedPlayer.getName());

        }

    }

    public void clientDisconnect(VirtualView virtualView){
        this.gameModel.removePlayer(playerMap.get(virtualView));
    }

}
