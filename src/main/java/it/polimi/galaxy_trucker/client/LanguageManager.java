package it.polimi.galaxy_trucker.client;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {

    private static Locale currentLocale = Locale.ENGLISH;
    private static Locale pendingLocale = currentLocale;
    private static ResourceBundle bundle = ResourceBundle.getBundle("lang.messages", currentLocale);

    public static void setPendingLocale(Locale locale) {
        pendingLocale = locale;
    }
    public static void cycleLanguages() {
        if (pendingLocale.equals(Locale.ENGLISH)) {
            setPendingLocale(Locale.ITALIAN);
        } else {
            setPendingLocale(Locale.ENGLISH);
        }
    }

    public static void applyPendingLocale() {
        if (pendingLocale != currentLocale) {
            currentLocale = pendingLocale;
            bundle = ResourceBundle.getBundle("lang.messages", currentLocale);
        }
    }

    public static Locale getLocale() {
        return currentLocale;
    }

    public static Locale getPendingLocale() {
        return pendingLocale;
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static String get(String key) {
        return bundle.getString(key);
    }
}
