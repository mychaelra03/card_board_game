package it.polimi.galaxy_trucker.server.controller.commandHandlers;

import it.polimi.galaxy_trucker.common.messages.SimpleMessage;
import it.polimi.galaxy_trucker.common.messages.SimplePlayerMessage;
import it.polimi.galaxy_trucker.server.controller.MessageDispatcher;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.lobby.BoardSelectMessage;
import it.polimi.galaxy_trucker.common.messages.lobby.ChatMessage;
import it.polimi.galaxy_trucker.server.VirtualView;

import java.util.Map;

/**
 * Handles commands received during the game lobby phase.
 * <p>
 * Supports actions such as board selection, toggling readiness, chatting,
 * kicking or promoting players, and maintains authority checks for admin-only actions.
 */
public class LobbyCmdHandler extends CommandHandler {

    /**
     * Constructs a {@code LobbyCmdHandler} with the provided game model, player map, and dispatcher.
     *
     * @param gameModel  the current game model
     * @param playerMap  a mapping of virtual views to their corresponding players
     * @param dispatcher the dispatcher used for message communication
     */
    public LobbyCmdHandler(GameModel gameModel, Map<VirtualView, Player> playerMap, MessageDispatcher dispatcher) {
        super(gameModel, playerMap, dispatcher);
    }

    /**
     * Registers lobby-specific command handlers for various player actions such as:
     * board selection, readiness toggling, chatting, kicking and promoting players.
     */
    @Override
    protected void registerHandlers() {
        register("board_select", (view, cmd) -> handleBoardSelect(view, mapper.convertValue(cmd, BoardSelectMessage.class)));
        register("ready_toggle", (view, cmd) -> handleReadyToggle(view));
        register("chat_message", (view, cmd) -> handleChatMessage(view, mapper.convertValue(cmd, ChatMessage.class)));
        register("kick_player", (view, cmd) -> handleKickPlayer(view, mapper.convertValue(cmd, SimplePlayerMessage.class)));
        register("promote_player", (view, cmd) -> handlePromotePlayer(view, mapper.convertValue(cmd, SimplePlayerMessage.class)));
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

    private void handleKickPlayer(VirtualView senderView, SimplePlayerMessage message) {
        if (!playerMap.get(senderView).isAdmin()) {
            Logger.logWarning("You need to be an admin to kick another player.");
            return;
        }

        VirtualView viewToKick = getVirtualViewByPlayerName(message.getPlayerName());

        if (viewToKick != null) {
            viewToKick.sendMessage(new SimpleMessage("kicked"));
            viewToKick.close();
        }
    }

    private void handlePromotePlayer(VirtualView senderView, SimplePlayerMessage message) {
        if (!playerMap.get(senderView).isAdmin()) {
            Logger.logWarning("You need to be an admin to promote another player.");
            return;
        }

        gameModel.setNewAdmin(message.getPlayerName());
    }


    /**
     * Retrieves the {@link VirtualView} associated with the specified player name.
     *
     * @param username the name of the player
     * @return the associated virtual view, or {@code null} if not found
     */
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
