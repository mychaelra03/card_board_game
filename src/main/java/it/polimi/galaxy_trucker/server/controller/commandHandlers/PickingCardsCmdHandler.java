package it.polimi.galaxy_trucker.server.controller.commandHandlers;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.flight.cardActions.*;
import it.polimi.galaxy_trucker.server.VirtualView;
import it.polimi.galaxy_trucker.server.controller.MessageDispatcher;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.cards.*;

import java.util.Map;

public class PickingCardsCmdHandler extends CommandHandler {

    public PickingCardsCmdHandler(GameModel gameModel, Map<VirtualView, Player> playerMap, MessageDispatcher dispatcher) {
        super(gameModel, playerMap, dispatcher);
    }

    @Override
    protected void registerHandlers() {
        register("pick_card", (view, cmd) -> handlePickCard(view));
        register("give_up", (view, cmd) -> handleGiveUp(view));
    }


    private void handleGiveUp(VirtualView senderView) {
        Player player = playerMap.get(senderView);
        player.setState(Player.PlayerState.GIVING_UP);
    }

    private void handlePickCard(VirtualView senderView) {
        Player player = playerMap.get(senderView);

        gameModel.pickNextCard(player);
    }


    @Override
    protected void handleUnrecognizedCommand(VirtualView senderView, JsonNode cmd) {
        String type = cmd.get("type").asText();
        Card currentCard = gameModel.getCurrentCard();
        Player sender = playerMap.get(senderView);

        Class<? extends CardActionMessage> messageClass = messageTypeMap.get(type);

        if (messageClass != null) {
            CardActionMessage message = mapper.convertValue(cmd, messageClass);
            message.applyTo(currentCard, sender);
        } else {
            Logger.logWarning("Unrecognized command: " + type);
        }
    }

    private static final Map<String, Class<? extends CardActionMessage>> messageTypeMap = Map.ofEntries(
            Map.entry("activate_cannons", ActivateCannonsMessage.class),
            Map.entry("activate_engines", ActivateEnginesMessage.class),
            Map.entry("refuse_battery", BatteryRefuseMessage.class),
            Map.entry("use_battery", BatteryUseMessage.class),
            Map.entry("card_decision", CardDecisionMessage.class),
            Map.entry("cargo_done", CargoDoneMessage.class),
            Map.entry("swap_cargo", CargoSwapMessage.class),
            Map.entry("unload_cargo", CargoUnloadMessage.class),
            Map.entry("choose_planet", PlanetChosenMessage.class),
            Map.entry("refuse_planet", PlanetRefusedMessage.class),
            Map.entry("remove_member", RemoveMemberMessage.class)
            );
}
