package it.polimi.galaxy_trucker.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.server.VirtualView;

import java.util.Map;

public interface CommandHandler {
    void handle(VirtualView sender, JsonNode cmd, Map<VirtualView, Player> playerMap) throws JsonProcessingException;
}
