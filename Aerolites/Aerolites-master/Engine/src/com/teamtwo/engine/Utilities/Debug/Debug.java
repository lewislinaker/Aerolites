package com.teamtwo.engine.Utilities.Debug;

import java.io.PrintStream;

public final class Debug {

    /**
     * Whether the Engine is being run in debug mode<br>
     * Add the DEBUG environment variable to run in debug mode
     */
    public static final boolean DEBUG = System.getenv("DEBUG") != null;

    /**
     * An enumeration for Logging level
     */
    public enum LogLevel {
        /** Debug logging level */
        DEBUG,
        /** Information logging level */
        INFORMATION,
        /** Warning logging level, uses System.err */
        WARNING,
        /** Error logging level, uses System.err and exits */
        ERROR
    }

    // Cannot instantiate
    private Debug() {}

    /**
     * Logs the given message at the specified level in the given layout "Level : Message"
     * @param level The level to log at
     * @param message The message to print out
     */
    public static void log(LogLevel level, String message) {
        PrintStream s;
        String name;
        switch (level) {
            case DEBUG:
                if(!DEBUG) return;
                s = System.out;
                name = "Debug";
                break;
            case INFORMATION:
                s = System.out;
                name = "Information";
                break;
            case WARNING:
                s = System.err;
                name = "Warning";
                break;
            case ERROR:
                s = System.err;
                name = "Error";
                break;
            default:
                throw new IllegalArgumentException("Error: Unknown log level: " + level.toString());
        }

        s.println(name + ": " + message);

        if(level == LogLevel.ERROR) System.exit(-1);
    }

    /**
     * Logs a simple message to the terminal
     * @param message The message to print out
     */
    public static void log(String message) {
        System.out.println(message);
    }
}
