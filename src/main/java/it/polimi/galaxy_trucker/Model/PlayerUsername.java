package it.polimi.galaxy_trucker.Model;

public class PlayerUsername {

    public static PlayerUsername instance;
    private  String username;
    private  int playerID;
    private  String colorP;
    private  int playerPic;

    public PlayerUsername(String username, int playerID, String colorP, int playerPic) {
        this.username = username;
        this.playerID = playerID;
        this.colorP = colorP;
        this.playerPic = playerPic;
    }
    public static PlayerUsername getInstance() {
        if (instance == null) {
            instance = new PlayerUsername();
        }
        return instance;
    }


     public PlayerUsername() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public String getColorP() {
        return colorP;
    }

    public void setColorP(String colorP) {
        this.colorP = colorP;
    }

    public int getPlayerPic() {
        return playerPic;
    }

    public void setPlayerPic(int playerPic) {
        this.playerPic = playerPic;
    }


    @Override
    public String toString() {
        return "PlayerUsername{" +
                "username='" + username + '\'' +
                ", playerID=" + playerID +
                ", colorP='" + colorP + '\'' +
                ", playerPic=" + playerPic +
                '}';
    }
}
