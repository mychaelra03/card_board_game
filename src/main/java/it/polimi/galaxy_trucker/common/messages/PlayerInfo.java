package it.polimi.galaxy_trucker.common.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerInfo implements GameMessage {

    private String username;
    private int[] profilePicIds;
    private String color;
    private boolean admin = false;
    private boolean ready = false;
    private String type = "playerInfo";

    public PlayerInfo() {}

    public PlayerInfo(String username, int[] profilePicIds, String color, boolean admin, boolean ready) {
        this.username = username;
        this.profilePicIds = profilePicIds;
        this.color = color;
        this.admin = admin;
        this.ready = ready;
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

    public void setType(String type) {
        this.type = type;
    }
    @Override
    public String getType() {
        return type;
    }

    public boolean isAdmin() {
        return admin;
    }
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isReady() {
        return ready;
    }
}
