package it.polimi.galaxy_trucker.server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.PlayerInfo;
import it.polimi.galaxy_trucker.common.messages.lobby.BoardSelectMessage;
import it.polimi.galaxy_trucker.common.messages.lobby.ChatMessage;
import it.polimi.galaxy_trucker.common.messages.lobby.KickedMessage;
import it.polimi.galaxy_trucker.server.VirtualView;

import java.util.Map;

public class LobbyHandler extends CommandHandler {

    public LobbyHandler(GameModel gameModel, Map<VirtualView, Player> playerMap) {
        super(gameModel, playerMap);
    }

    @Override
    public void handle(VirtualView senderView, JsonNode cmd) {

        ObjectMapper mapper = new ObjectMapper();
        String type = cmd.get("type").asText();

        switch (cmd.get("type").asText()) {

            case "boardSelect" -> {
                if (!playerMap.get(senderView).isAdmin()) {
                    Logger.logWarning("You do not have the permission to change board type.");
                    return;
                }
                BoardSelectMessage boardSelectMessage = mapper.convertValue(cmd, BoardSelectMessage.class);
                gameModel.cycleBoard(boardSelectMessage.getAction());
            }

            case "readyToggle" -> {
                Player player = playerMap.get(senderView);
                if (player == null) throw new RuntimeException("Player not found");
                player.toggleReady();
                gameModel.updatePlayersReady();
            }

            case "chatMessage" -> {

                ChatMessage chatMessage = mapper.convertValue(cmd, ChatMessage.class);

                Player sender = playerMap.get(senderView);

                ChatMessage completeChatMessage = new ChatMessage(sender.getName(), sender.getColor().toString(), chatMessage.getMessage());

                playerMap.forEach((v, _) -> v.sendMessage(completeChatMessage));
            }

            case "kickPlayer" -> {
                if (!playerMap.get(senderView).isAdmin()) {
                    Logger.logWarning("You need to be an admin to kick another player.");
                    return;
                }
                PlayerInfo kickedPlayerInfo = mapper.convertValue(cmd, PlayerInfo.class);

                VirtualView viewToKick = getVirtualViewByPlayerName(kickedPlayerInfo.getUsername());

                if (viewToKick != null) {
                    viewToKick.sendMessage(new KickedMessage());
                    viewToKick.close();
                }
            }

            case "promotePlayer" -> {
                if (!playerMap.get(senderView).isAdmin()) {
                    Logger.logWarning("You need to be an admin to promote another player.");
                    return;
                }
                PlayerInfo promotedPlayerInfo = mapper.convertValue(cmd, PlayerInfo.class);

                gameModel.setNewAdmin(promotedPlayerInfo.getUsername());
            }

            default -> Logger.logError("Unrecognized command: " + type);
        }
    }


    public VirtualView getVirtualViewByPlayerName(String username) {
        for (Map.Entry<VirtualView, Player> entry : playerMap.entrySet()) {
            if (entry.getValue().getName().equals(username)) {
                return entry.getKey();
            }
        }
        Logger.logError("No VirtualView found for player: " + username);
        return null;
    }
}
