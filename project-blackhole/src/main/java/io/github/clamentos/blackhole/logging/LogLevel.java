package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

/**
 * Enumeration listing al the log levels.
 * Each successive entry is more important than the previous.
 * Each entry is composed of the log level name + color.
*/
public enum LogLevel {
    
    DEBUG("  DEBUG", "\u001B[30m"),
    INFO("   INFO", "\u001B[34m"),
    SUCCESS("SUCCESS", "\u001B[32m"),
    NOTE("   NOTE", "\u001B[35m"),
    WARNING("WARNING", "\u001B[33m"),
    ERROR("  ERROR", "\u001B[31m");

    //____________________________________________________________________________________________________________________________________

    private String value;
    private String color;

    //____________________________________________________________________________________________________________________________________

    private LogLevel(String value, String color) {

        this.value = value;
        this.color = color;
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Get the associated log level string.
     * @return the log level string (always well-defined).
     */
    public String getValue() {

        return(value);
    }

    /**
     * Get the associated log level color.
     * @return the log level color (always well-defined).
     */
    public String getColor() {

        return(color);
    }

    //____________________________________________________________________________________________________________________________________
}
