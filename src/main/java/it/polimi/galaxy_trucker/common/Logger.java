package it.polimi.galaxy_trucker.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static boolean debugMode = false;

    public enum LogType {
        INFO (GREEN),
        DEBUG (BLUE),
        WARNING (YELLOW),
        ERROR (RED);

        private final String color;
        LogType(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }
    }

    private static final String RESET = "\u001B[0m";
    private static final String GRAY = "\u001B[37m";
    private static final String GREEN = "\u001B[32m";   // INFO
    private static final String BLUE = "\u001B[34m";    // DEBUG
    private static final String YELLOW = "\u001B[33m";  // WARNING
    private static final String RED = "\u001B[31m";     // ERROR


    private static void printLog(String message, LogType logType) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String timestamp = LocalDateTime.now().format(formatter);

        String typeColor = switch (logType) {
            case INFO -> GREEN;
            case DEBUG -> BLUE;
            case WARNING -> YELLOW;
            case ERROR -> RED;
        };
        String type = logType.toString();

        String className = Thread.currentThread().getStackTrace()[3].getClassName().substring(Thread.currentThread().getStackTrace()[3].getClassName().lastIndexOf('.') + 1);

        String thread = Thread.currentThread().getName();

        String logMessage = String.format("%s %s[%s] %s[%s] [%s]%s %s", timestamp, typeColor, type, GRAY, className, thread, RESET, message);

        if (logType == LogType.ERROR || logType == LogType.WARNING) {
            System.err.println(logMessage);
        } else {
            System.out.println(logMessage);
        }
    }

    // Wrapper methods for different log levels
    public static void logInfo(String message) {
        printLog(message, LogType.INFO);
    }

    public static void logDebug(String message) {
        if(debugMode) printLog(message, LogType.DEBUG);
    }

    public static void logWarning(String message) {
        printLog(message, LogType.WARNING);
    }

    public static void logError(String message) {
        printLog(message, LogType.ERROR);
    }


    public static void setDebugMode(boolean debugMode) {
        Logger.logDebug("Debug mode disabled.");
        Logger.debugMode = debugMode;
        Logger.logDebug("Debug mode enabled.");
    }
    public static boolean isDebugEnabled() {
        return Logger.debugMode;
    }
}
