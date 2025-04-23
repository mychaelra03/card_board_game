package it.polimi.galaxy_trucker.Common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.Player;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerInfo implements GameMessage{
    private String username;
    private int[] profilePicIds;
    private String color;

    public PlayerInfo() {}

    public PlayerInfo(String username, int[] profilePicIds, String color) {
        this.username = username;
        this.profilePicIds = profilePicIds;
        this.color = color;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int[] getProfilePicIds() {
        return profilePicIds;
    }

    public void setProfilePicIds(int[] profilePicIds) {
        this.profilePicIds = profilePicIds;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String getType() {
        return "playerInfo";
    }
}
