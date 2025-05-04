package it.polimi.galaxy_trucker.client;

public class ClientSession {
    private static String username = null;
    private static int[] profilePicture = null;

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
}
