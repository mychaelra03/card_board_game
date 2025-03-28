package it.polimi.galaxy_trucker.cards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.tiles.BlockType;

import java.util.List;

import static it.polimi.galaxy_trucker.GameModel.Direction.BACKWARD;

@JsonIgnoreProperties(ignoreUnknown = true)

public class AbandonedStationCard extends Card {
    // Collect goods if you have enough crew.

    private final int requiredCrew;
    private final BlockType[] rewardCargo;
    private final int positionCost;

    public AbandonedStationCard(
            @JsonProperty("id") int id,
            @JsonProperty("level") int level,
            @JsonProperty("requiredCrew") int requiredCrew,
            @JsonProperty("rewardCargo") BlockType[] rewardCargo,
            @JsonProperty("positionCost") int positionCost) {
        super(id, level);
        if (requiredCrew < 0) throw new IllegalArgumentException("positionCost must be a positive number.");
        this.requiredCrew = requiredCrew;
        this.rewardCargo = rewardCargo;
        if (positionCost < 0) throw new IllegalArgumentException("positionCost must be a positive number.");
        this.positionCost = positionCost;
    }

    @Override
    public void applyEffects(GameModel board) {
        List<Player> players = board.getSortedPlayers();
        for(Player player : players) {
            if(player.getCrewCount() < requiredCrew) continue;

            boolean accepted = player.acceptsCardEffect();

            if(accepted) {
                player.giveCargoReward(rewardCargo);
                board.movePlayer(player, positionCost, BACKWARD);
                break;
            }
        }
    }

    @Override
    public String getCardType() {
        return "Abandoned Station";
    }
}
