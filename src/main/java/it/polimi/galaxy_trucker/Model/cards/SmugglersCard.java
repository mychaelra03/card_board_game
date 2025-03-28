package it.polimi.galaxy_trucker.Model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.Model.tiles.BlockType;

@JsonIgnoreProperties(ignoreUnknown = true)

public class SmugglersCard extends AbstractEnemy {
    // Combat encounter, win to gain goods, lose to lose goods.

    private final BlockType[] rewardCargo;
    private final int cargoLossOnDefeat;

    public SmugglersCard(
            @JsonProperty("id") int id,
            @JsonProperty("level") int level,
            @JsonProperty("requiredCannonPower") int requiredCannonPower,
            @JsonProperty("rewardCargo")BlockType[] rewardCargo,
            @JsonProperty("positionCost")int positionCost,
            @JsonProperty("cargoLossOnDefeat")int cargoLossOnDefeat) {
        super(id, level, requiredCannonPower, positionCost);
        this.rewardCargo = rewardCargo;
        if (cargoLossOnDefeat < 0) throw new IllegalArgumentException("LossOnDefeat must be a positive number.");
        this.cargoLossOnDefeat = cargoLossOnDefeat;
    }

    @Override
    public void giveReward(Player player) {
        player.giveCargoReward(rewardCargo);
    }

    @Override
    public void applyPunishment(Player player) {
        player.removeMostValuableCargo(cargoLossOnDefeat);
    }

    @Override
    public String getCardType() {
        return "Smugglers";
    }
}
