package it.polimi.galaxy_trucker.Server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.Common.*;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.Server.Event.BoardTypeUpdateEvent;
import it.polimi.galaxy_trucker.Server.Event.GameStateUpdateEvent;
import it.polimi.galaxy_trucker.Server.Event.PlayersUpdateEvent;
import it.polimi.galaxy_trucker.Server.Event.PlayerKickedEvent;

import java.util.HashMap;
import java.util.Map;

public class Controller {
    GameModel gameModel = null;
    private Map<VirtualView, Player> playerMap = new HashMap<>();

    public Controller(GameModel gameModel) {
        this.gameModel = gameModel;

        gameModel.addEventListener(PlayersUpdateEvent.class, this::handlePlayersUpdate);
        gameModel.addEventListener(BoardTypeUpdateEvent.class, this::handleBoardTypeUpdate);
        gameModel.addEventListener(PlayerKickedEvent.class, this::handlePlayerKicked);
        gameModel.addEventListener(GameStateUpdateEvent.class, this::handleGameStateUpdate);
    }

    public VirtualView getVirtualViewByPlayerName(String username) {
        for (Map.Entry<VirtualView, Player> entry : playerMap.entrySet()) {
            if (entry.getValue().getName().equals(username)) {
                return entry.getKey();
            }
        }
        System.err.println("No VirtualView found for player: " + username);
        return null;
    }


    private void handlePlayerKicked(PlayerKickedEvent event) {
            PlayerInfo playerInfo = event.getPlayerInfo();
            playerInfo.setType("playerKicked");

        VirtualView viewToKick = getVirtualViewByPlayerName(playerInfo.getUsername());

        if (viewToKick != null) {
            viewToKick.sendMessage(playerInfo);
            playerMap.remove(viewToKick);
            viewToKick.close();
        }
    }

    public void handlePlayersUpdate(PlayersUpdateEvent event) {
        for(VirtualView vw : playerMap.keySet()) {
            vw.sendMessage(new PlayersInfoMessage(event.getPlayerInfoList(), event.getUsername()));
        }
    }
    public void handleBoardTypeUpdate(BoardTypeUpdateEvent event) {
        for(VirtualView vw : playerMap.keySet()) {
            vw.sendMessage(new BoardTypeMessage(event.getBoardType()));
        }
    }
    public void handleGameStateUpdate(GameStateUpdateEvent event) {
        for(VirtualView vw : playerMap.keySet()) {
            vw.sendMessage(new GameStateMessage(event.getGameState()));
        }
    }

    public void processCmd(VirtualView senderVirtualView, String json) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode cmd = mapper.readTree(json);
        String type = cmd.get("type").asText();

        switch (type) {
            case "playerInfo" -> {

                PlayerInfo playerInfo = mapper.convertValue(cmd, PlayerInfo.class);

                Player player = new Player(playerInfo.getUsername(), playerInfo.getProfilePicIds());

                LobbyJoinResponse.JoinStatus result = gameModel.canAddPlayer(player);

                LobbyJoinResponse joinResponse = new LobbyJoinResponse(result);
                senderVirtualView.sendMessage(joinResponse);

                if (result == LobbyJoinResponse.JoinStatus.SUCCESS) {
                    playerMap.put(senderVirtualView, player);
                    gameModel.addPlayer(player);
                    senderVirtualView.sendMessage(new BoardTypeMessage(gameModel.getBoardType()));
                } else senderVirtualView.close();
            }

            case "boardSelect" -> {
                if(!playerMap.get(senderVirtualView).isAdmin()) {
                    System.err.println("You do not have the permission to change board type.");
                    return;
                }
                BoardSelectMessage boardSelectMessage = mapper.convertValue(cmd, BoardSelectMessage.class);
                gameModel.cycleBoard(boardSelectMessage.getAction());
            }

            case "readyToggle" -> {
                Player player = playerMap.get(senderVirtualView);
                if (player == null) throw new RuntimeException("Player not found");
                player.toggleReady();
                gameModel.updatePlayersReady();
            }

            case "chatMessage" -> {

                ChatMessage chatMessage = mapper.convertValue(cmd, ChatMessage.class);

                ChatMessage chatMessageUsername = new ChatMessage(playerMap.get(senderVirtualView).getName(), playerMap.get(senderVirtualView).getColor().toString(), chatMessage.getMessage());

                playerMap.forEach((v, _) -> v.sendMessage(chatMessageUsername));
            }

            case "kickPlayer" -> {

                PlayerInfo kickedPlayerInfo = mapper.convertValue(cmd, PlayerInfo.class);

                this.gameModel.removePlayer(kickedPlayerInfo.getUsername());
            }

            case "promotePlayer" -> {

                PlayerInfo promotedPlayerInfo = mapper.convertValue(cmd, PlayerInfo.class);

                Player promotedPlayer = new Player(promotedPlayerInfo.getUsername(), promotedPlayerInfo.getProfilePicIds());

                gameModel.setNewAdmin(promotedPlayer.getName());
            }
        }

    }

    public void clientDisconnect(VirtualView virtualView) {
        if(playerMap.containsKey(virtualView)) {
            this.gameModel.removePlayer(playerMap.get(virtualView).getName());
        }
    }

}
