package it.polimi.galaxy_trucker.server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.flight.cardActions.*;
import it.polimi.galaxy_trucker.server.VirtualView;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.cards.*;

import java.util.Map;

public class PickingCardsHandler extends CommandHandler {

    public PickingCardsHandler(GameModel gameModel, Map<VirtualView, Player> playerMap) {
        super(gameModel, playerMap);
    }

    private static final Map<String, Class<? extends CardActionMessage>> messageTypeMap = Map.ofEntries(
            Map.entry("activate_cannons", ActivateCannonsMessage.class),
            Map.entry("activate_engines", ActivateEnginesMessage.class),
            Map.entry("refuse_battery", BatteryRefuseMessage.class),
            Map.entry("use_battery", BatteryUseMessage.class),
            Map.entry("card_decision", CardDecisionMessage.class),
            Map.entry("done", CargoDoneMessage.class),
            Map.entry("swap_cargo", CargoSwapMessage.class),
            Map.entry("unload_cargo", CargoUnloadMessage.class),
            Map.entry("choose_planet", PlanetChosenMessage.class),
            Map.entry("refuse_planet", PlanetRefusedMessage.class),
            Map.entry("remove_member", RemoveMemberMessage.class)
            );

    @Override
    public void handle(VirtualView senderView, JsonNode cmd) {

        ObjectMapper mapper = new ObjectMapper();
        String type = cmd.get("type").asText();
        switch (type) {

            case "pick_card" -> {

                Player player = playerMap.get(senderView);

                gameModel.pickNextCard(player);
            }

            default -> {
                Card currentCard = gameModel.getCurrentCard();
                Player sender = playerMap.get(senderView);

                Class<? extends CardActionMessage> messageClass = messageTypeMap.get(type);

                if (messageClass != null) {
                    CardActionMessage message = mapper.convertValue(cmd, messageClass);
                    message.applyTo(currentCard, sender);
                } else {
                    Logger.logError("Unknown command type: " + type);
                }
            }
        }

    }
}
