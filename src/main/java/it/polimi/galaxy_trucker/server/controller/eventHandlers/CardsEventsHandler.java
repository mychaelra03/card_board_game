package it.polimi.galaxy_trucker.server.controller.eventHandlers;

import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.SimpleMessage;
import it.polimi.galaxy_trucker.common.messages.SimplePlayerMessage;
import it.polimi.galaxy_trucker.common.messages.flight.*;
import it.polimi.galaxy_trucker.common.messages.flight.cardActions.CardDecisionMessage;
import it.polimi.galaxy_trucker.common.messages.flight.cardActions.CargoSwapMessage;
import it.polimi.galaxy_trucker.common.messages.flight.cardActions.CargoUnloadMessage;
import it.polimi.galaxy_trucker.server.controller.MessageDispatcher;
import it.polimi.galaxy_trucker.server.events.flight.*;
import it.polimi.galaxy_trucker.server.events.flight.cards.*;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.ShipBoard;
import it.polimi.galaxy_trucker.server.model.cards.Card;
import it.polimi.galaxy_trucker.server.model.cards.projectile.CannonShot;
import it.polimi.galaxy_trucker.server.model.cards.projectile.Meteor;
import it.polimi.galaxy_trucker.server.model.cards.projectile.Projectile;
import it.polimi.galaxy_trucker.server.model.tiles.CannonTile;
import it.polimi.galaxy_trucker.server.model.tiles.CrewType;
import it.polimi.galaxy_trucker.server.model.tiles.EngineTile;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CardsEventsHandler extends EventHandler {

    public CardsEventsHandler(GameModel gameModel, MessageDispatcher dispatcher) {
        super(gameModel, dispatcher);

        gameModel.addEventListener(PlayerPositionUpdateEvent.class, this::handlePlayerPositionUpdate);
        gameModel.addEventListener(PlayerEliminatedEvent.class, this::handlePlayerEliminated);
        gameModel.addEventListener(CreditsUpdateEvent.class, this::handleCreditsUpdate);
        gameModel.addEventListener(CardPickedEvent.class, this::handleCardPicked);
        gameModel.addEventListener(CardFinishedEvent.class, this::handleCardFinished);
        gameModel.addEventListener(PlanetSelectionEvent.class, this::handlePlanetSelection);
        gameModel.addEventListener(CargoDecisionEvent.class, this::handleCargoDecision);
        gameModel.addEventListener(CargoSwappedEvent.class, this::handleCargoSwapped);
        gameModel.addEventListener(CargoUnloadedEvent.class, this::handleCargoUnloaded);
        gameModel.addEventListener(CardDecisionEvent.class, this::handleCardDecision);
        gameModel.addEventListener(CrewDecisionEvent.class, this::handleCrewDecision);
        gameModel.addEventListener(DecisionEndEvent.class, this::handleDecisionEnd);
        gameModel.addEventListener(ProjectileEvent.class, this::handleProjectile);
        gameModel.addEventListener(DiceThrowEvent.class, this::handleDiceThrow);
        gameModel.addEventListener(BatteryDecisionEvent.class, this::handleBatteryDecision);
        gameModel.addEventListener(CannonSelectionEvent.class, this::handleCannonSelection);
        gameModel.addEventListener(EngineSelectionEvent.class, this::handleEngineSelection);
        gameModel.addEventListener(PlayerStatusEvent.class, this::handlePlayerStatus);
        gameModel.addEventListener(CardStatusEvent.class, this::handleCardStatus);
    }

    private void handlePlayerPositionUpdate(PlayerPositionUpdateEvent event) {
        Logger.logDebug(event.getPlayerName() + " moved to " + event.getNewPosition());
        dispatcher.broadcast(new PlayerPositionMessage(event.getPlayerName(), event.getNewPosition()));
    }
    private void handlePlayerEliminated(PlayerEliminatedEvent event) {
        dispatcher.broadcast(new SimplePlayerMessage("player_eliminated", event.getPlayerName()));
    }
    private void handleCreditsUpdate(CreditsUpdateEvent event) {
        dispatcher.broadcast(new CreditsMessage(event.getPlayerName(), event.getCredits()));
    }
    private void handleCardPicked(CardPickedEvent event) {
        Card card = event.getCard();
        Logger.logDebug("Card picked: " + card.getCardType() + " of level " + card.getLevel());
        dispatcher.broadcast(new CardPickedMessage(card.getLevel(), card.getId()));
    }
    private void handleCardFinished(CardFinishedEvent event) {
        if(gameModel.getSortedPlayers().isEmpty() || gameModel.getDeck().isEmpty()) {
            gameModel.handleGameOver();
        } else {
            dispatcher.broadcast(new PickAvailableMessage(gameModel.getFirstPlayer().getName()));
        }
    }
    private void handlePlanetSelection(PlanetSelectionEvent event) {
        List<String> planetChosenByNames = Arrays.stream(event.getPlanetChosenBy())
                .map(p -> p != null ? p.getName() : null).toList();
        dispatcher.broadcast(new PlanetSelectionMessage(event.getPlayerName(), planetChosenByNames));
    }
    private void handleCargoDecision(CargoDecisionEvent event) {
        for(Player player : event.getPlayers()) {
            dispatcher.broadcast(new CargoDecisionMessage(player.getName(), player.getShipContent().cargoData(), player.getCargoPool()));
        }
    }
    private void handleCargoSwapped(CargoSwappedEvent event) {
        String playerName = event.getPlayer().getName();
        dispatcher.sendTo(playerName, new CargoSwapMessage(playerName, event.getBlockIndex(), event.getTileCoords(), event.getContainerIndex()));
        dispatcher.broadcastExcept(playerName, new CargoMessage(playerName, event.getPlayer().getShipContent().cargoData()));
    }
    private void handleCargoUnloaded(CargoUnloadedEvent event) {
        String playerName = event.getPlayer().getName();
        dispatcher.sendTo(playerName, new CargoUnloadMessage(playerName, event.getTileCoords(), event.getContainerIndex()));
        dispatcher.broadcastExcept(playerName, new CargoMessage(playerName, event.getPlayer().getShipContent().cargoData()));
    }
    private void handleCardDecision(CardDecisionEvent event) {
        dispatcher.broadcast(new CardDecisionMessage(event.getPlayerName(), null));
    }
    private void handleCrewDecision(CrewDecisionEvent event) {
        dispatcher.broadcast(new CrewMessage("crew_decision", event.getPlayerName(), event.getCrewTiles()));
    }
    private void handleDecisionEnd(DecisionEndEvent event) {
        dispatcher.sendTo(event.getPlayerName(), new SimpleMessage("decision_end"));
    }
    private void handleProjectile(ProjectileEvent e) {
        Player player = e.getPlayer();
        Projectile projectile = e.getProjectile();
        ProjectileMessage.ProjectileType type = null;
        if(projectile instanceof Meteor) type = ProjectileMessage.ProjectileType.METEOR;
        if(projectile instanceof CannonShot) type = ProjectileMessage.ProjectileType.SHOT;
        if(type == null) throw new IllegalStateException("Projectile type not found.");
        Set<TileInfo> ship = (!e.isDefended() && e.getHitPoint() != null)? player.getSimplifiedShipBoard() : null;

        dispatcher.broadcast(new ProjectileMessage(player.getName(), type, projectile.getSize(), projectile.getSource(), projectile.getPathIndex(), e.getHitPoint(), e.isDefended(), ship));
    }
    private void handleDiceThrow(DiceThrowEvent event) {
        if(event.getDice() == null) return;
        dispatcher.broadcast(new DiceThrowMessage(event.getPlayerName(), event.getDice()));
    }
    private void handleBatteryDecision(BatteryDecisionEvent event) {
        Player player = event.getPlayer();
        Projectile projectile = event.getProjectile();
        Point hitPoint = player.getFirstNonEmptyTilePosition(projectile.getSource(), projectile.getPathIndex());
        dispatcher.broadcast(new BatteryDecisionMessage(player.getName(), player.getShipContent().batteriesData(), hitPoint));
    }
    private void handleCannonSelection(CannonSelectionEvent event) {
        Player player = event.getPlayer();
        Set<Point> verticalCannons = player.getDoubleCannonsPositions(true);
        Set<Point> rotatedCannons = player.getDoubleCannonsPositions(false);
        Set<ShipBoard.BatteriesData> batteries = player.getShipContent().batteriesData();
        double minStrength = player.getMinStrength(CannonTile.class);
        boolean hasAlien = player.hasAlien(CrewType.PURPLE_ALIEN);
        dispatcher.broadcast(new TileSelectionMessage("cannon_selection", player.getName(), minStrength, verticalCannons, rotatedCannons, batteries, false, hasAlien));
    }
    private void handleEngineSelection(EngineSelectionEvent event) {
        Player player = event.getPlayer();
        Set<Point> engines = player.getDoubleEnginesPositions();
        Set<ShipBoard.BatteriesData> batteries = player.getShipContent().batteriesData();
        double minStrength = player.getMinStrength(EngineTile.class);
        boolean hasAlien = player.hasAlien(CrewType.BROWN_ALIEN);
        dispatcher.broadcast(new TileSelectionMessage("engine_selection", player.getName(), minStrength, engines, null, batteries, event.isOpenSpace(), hasAlien));
    }
    private void handlePlayerStatus(PlayerStatusEvent event) {
        dispatcher.broadcast(new StatusUpdateMessage("player_status", event.getStatus().toString().toLowerCase(), event.getPlayerName()));
    }
    private void handleCardStatus(CardStatusEvent event) {
        dispatcher.broadcast(new StatusUpdateMessage("card_status", event.getStatus().toString().toLowerCase(), null));
    }
}
