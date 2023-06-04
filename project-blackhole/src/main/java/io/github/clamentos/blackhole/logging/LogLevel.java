package io.github.clamentos.blackhole.logging;

/**
 * Log levels.
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

    private String value;
    private String color;

    private LogLevel(String value, String color) {

        this.value = value;
        this.color = color;
    }

    public String getValue() {

        return(value);
    }

    public String getColor() {

        return(color);
    }
}
