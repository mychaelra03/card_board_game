package it.polimi.galaxy_trucker.Client;

public class ClientSession {
    private static String username;
    private static int[] profilePicIds;

    public static void setUsername(String name) {
        username = name;
    }

    public static String getUsername() {
        return username;
    }

    public static int[] getProfilePicIds() {
        return profilePicIds;
    }

    public static void setProfilePicIds(int[] profilePicIds) {
        ClientSession.profilePicIds = profilePicIds;
    }
}
