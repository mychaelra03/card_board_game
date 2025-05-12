package it.polimi.galaxy_trucker.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.common.messages.BoardTypeMessage;
import it.polimi.galaxy_trucker.common.messages.PlayerInfo;
import it.polimi.galaxy_trucker.common.messages.login.LobbyJoinResponse;
import it.polimi.galaxy_trucker.server.VirtualView;

import java.util.Map;

public class LobbyHandler implements CommandHandler {

    @Override
    public void handle(VirtualView sender, JsonNode cmd, Map<VirtualView, Player> playerMap) throws JsonProcessingException {

        String type = cmd.get("type").asText();
//        switch (type) {
//            case "playerInfo" -> {
//
//                PlayerInfo playerInfo = mapper.convertValue(cmd, PlayerInfo.class);
//
//                Player player = new Player(playerInfo.getUsername(), playerInfo.getProfilePicIds());
//
//                LobbyJoinResponse.JoinStatus result = gameModel.canAddPlayer(player);
//
//                senderVirtualView.sendMessage(new LobbyJoinResponse(result));
//
//                if (result == LobbyJoinResponse.JoinStatus.SUCCESS) {
//                    playerMap.put(senderVirtualView, player);
//                    gameModel.addPlayer(player);
//                    senderVirtualView.sendMessage(new BoardTypeMessage(gameModel.getBoardType()));
//                } else senderVirtualView.close();
//            }
    }
}
