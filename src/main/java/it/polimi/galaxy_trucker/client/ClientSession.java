package it.polimi.galaxy_trucker.client;

import it.polimi.galaxy_trucker.client.controllers.LobbyController;
import it.polimi.galaxy_trucker.common.messages.PlayerInfo;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.*;
import java.util.stream.Collectors;

public class ClientSession {
    private static String username = null;
    private static int[] profilePicture = null;
    private static Map<String, PlayerInfo> players = new HashMap<>();

    public static String getUsername() {
        return username;
    }
    public static void setUsername(String name) {
        username = name;
    }

    public static int[] getProfilePicture() {
        return profilePicture;
    }
    public static void setProfilePicture(int[] profilePicture) {
        ClientSession.profilePicture = profilePicture;
    }

    public static PlayerInfo getPlayerInfo(String username) {
        return players.get(username);
    }
    public static PlayerInfo getPlayerInfo() {
        return players.get(username);
    }
    public static void updatePlayers(List<PlayerInfo> playersInfo) {
        players = playersInfo.stream().collect(Collectors.toMap(PlayerInfo::getUsername, p -> p));
    }

    public static boolean isSelf(String username) {
        return username.equals(ClientSession.getUsername());
    }

    public static Set<String> getPlayerNames() {
        return players.keySet();
    }

    public static void resetData() {
        username = null;
        profilePicture = null;
        players.clear();
    }

    public static int getPlayerCount() {
        return players.size();
    }

    public static StackPane getPlayerProfilePicture(String username, double size) {
        PlayerInfo playerInfo = players.get(username);
        if(playerInfo == null) return null;

        ImageView profilePicBody = new ImageView();
        ImageView profilePicAcc = new ImageView();
        Image bodyImage = new Image(Objects.requireNonNull(LobbyController.class.getResourceAsStream("/textures/characters/character_" + playerInfo.getProfilePicIds()[0] + ".png")));
        Image accessoryImage = new Image(Objects.requireNonNull(LobbyController.class.getResourceAsStream("/textures/characters/hand_" + playerInfo.getProfilePicIds()[1] + ".png")));

        profilePicBody.setImage(bodyImage);
        profilePicBody.setFitHeight(size);
        profilePicBody.setFitWidth(size);

        profilePicAcc.setImage(accessoryImage);
        profilePicAcc.setFitHeight(size);
        profilePicAcc.setFitWidth(size);

        double radius = size / 2;
        Circle profilePicBackground = new Circle(radius);

        profilePicBackground.setFill(Paint.valueOf(playerInfo.getColor()));
        profilePicBackground.setStroke(Color.BLACK);
        profilePicBackground.setStrokeWidth(1);

        // Add a circle clip to the first avatar image to make it circular
        Circle bodyClip = new Circle(radius);
        bodyClip.setCenterX(profilePicBody.getFitWidth() / 2);
        bodyClip.setCenterY(profilePicBody.getFitHeight() / 2);
        profilePicBody.setClip(bodyClip);

        Circle accClip = new Circle(radius);
        accClip.setCenterX(profilePicAcc.getFitWidth() / 2);
        accClip.setCenterY(profilePicAcc.getFitHeight() / 2);
        profilePicAcc.setClip(accClip);

        StackPane profilePic = new StackPane(profilePicBackground, profilePicBody, profilePicAcc);
        profilePic.setMinSize(size, size);
        profilePic.setPrefSize(size, size);
        profilePic.setMaxSize(size, size);
        return profilePic;
    }
}
