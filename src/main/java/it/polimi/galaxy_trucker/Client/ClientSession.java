package it.polimi.galaxy_trucker.Client;

public class ClientSession {
    private static String username;
    private static int[] profilePicIds;
    private static boolean admin;
    private static boolean ready;

    public static String getUsername() {
        return username;
    }
    public static void setUsername(String name) {
        username = name;
    }

    public static boolean isAdmin() {
        return admin;
    }
    public static void setAdmin(boolean admin) {
        ClientSession.admin = admin;
    }

    public static int[] getProfilePicIds() {
        return profilePicIds;
    }
    public static void setProfilePicIds(int[] profilePicIds) {
        ClientSession.profilePicIds = profilePicIds;
    }

    public static boolean isReady() {
        return ready;
    }
    public static void setReady(boolean ready) {
        ClientSession.ready = ready;
    }
}
