package it.polimi.galaxy_trucker.server.controller.commandHandlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.common.messages.PlayersInfoMessage;
import it.polimi.galaxy_trucker.common.messages.SimpleMessage;
import it.polimi.galaxy_trucker.server.controller.MessageDispatcher;
import it.polimi.galaxy_trucker.server.events.lobby.PlayersUpdateEvent;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.PlayerInfo;
import it.polimi.galaxy_trucker.common.messages.lobby.BoardSelectMessage;
import it.polimi.galaxy_trucker.common.messages.lobby.ChatMessage;
import it.polimi.galaxy_trucker.server.VirtualView;

import java.util.Map;

public class LobbyCmdHandler extends CommandHandler {

    public LobbyCmdHandler(GameModel gameModel, Map<VirtualView, Player> playerMap, MessageDispatcher dispatcher) {
        super(gameModel, playerMap, dispatcher);
    }

    @Override
    public void handle(VirtualView senderView, JsonNode cmd) {

        ObjectMapper mapper = new ObjectMapper();
        String type = cmd.get("type").asText();

        switch (cmd.get("type").asText()) {

            case "board_select"   -> handleBoardSelect(senderView, mapper.convertValue(cmd, BoardSelectMessage.class));
            case "ready_toggle"   -> handleReadyToggle(senderView);
            case "chat_message"   -> handleChatMessage(senderView, mapper.convertValue(cmd, ChatMessage.class));
            case "kick_player"    -> handleKickPlayer(senderView, mapper.convertValue(cmd, PlayerInfo.class));
            case "promote_player" -> handlePromotePlayer(senderView, mapper.convertValue(cmd, PlayerInfo.class));

            default               -> Logger.logError("Unrecognized command: " + type);
        }
    }

    private void handleBoardSelect(VirtualView senderView, BoardSelectMessage boardSelectMessage) {
        if (!playerMap.get(senderView).isAdmin()) {
            Logger.logWarning("You do not have the permission to change board type.");
            return;
        }
        gameModel.cycleBoard(boardSelectMessage.getAction());
    }

    private void handleReadyToggle(VirtualView senderView) {
        Player player = playerMap.get(senderView);
        if (player == null) throw new RuntimeException("Player not found");
        player.toggleReady();
        gameModel.updatePlayersReady();
    }

    private void handleChatMessage(VirtualView senderView, ChatMessage chatMessage) {
        Player sender = playerMap.get(senderView);

        ChatMessage completeChatMessage = new ChatMessage(sender.getName(), sender.getColor().toString(), chatMessage.getMessage());

        playerMap.forEach((v, _) -> v.sendMessage(completeChatMessage));
    }

    private void handleKickPlayer(VirtualView senderView, PlayerInfo kickedPlayerInfo) {
        if (!playerMap.get(senderView).isAdmin()) {
            Logger.logWarning("You need to be an admin to kick another player.");
            return;
        }

        VirtualView viewToKick = getVirtualViewByPlayerName(kickedPlayerInfo.getUsername());

        if (viewToKick != null) {
            viewToKick.sendMessage(new SimpleMessage("kicked"));
            viewToKick.close();
        }
    }

    private void handlePromotePlayer(VirtualView senderView, PlayerInfo promotedPlayerInfo) {
        if (!playerMap.get(senderView).isAdmin()) {
            Logger.logWarning("You need to be an admin to promote another player.");
            return;
        }

        gameModel.setNewAdmin(promotedPlayerInfo.getUsername());
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
