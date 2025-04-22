package it.polimi.galaxy_trucker.Client;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {

    private static final String LANG_PATH = "it.polimi.galaxy_trucker.lang";

    private static Locale currentLocale = Locale.ENGLISH;
    private static ResourceBundle bundle = ResourceBundle.getBundle(LANG_PATH + ".messages", currentLocale);

    public static void setLocale(Locale locale) {
        currentLocale = locale;
        bundle = ResourceBundle.getBundle(LANG_PATH + ".messages", locale);
    }

    public static Locale getLocale() {
        return currentLocale;
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static String get(String key) {
        return bundle.getString(key);
    }
}
