package io.github.clamentos.blackhole.framework.implementation.logging;

///
/**
 * <h3>Log Levels</h3>
 * <p>Enumeration of all the possible log levels.</p>
 * Each entry is composed of the log level name + ANSI color escape code.
 * <ol>
 *     <li>{@code DEBUG}: Used for debugging.</li>
 *     <li>{@code INFO}: Used to give harmless but useful information.</li>
 *     <li>{@code SUCCESS}: Used to indicate positive outcomes.</li>
 *     <li>{@code NOTE}: Used to indicate minor issues.</li>
 *     <li>{@code WARNING}: Used to indicate recoverable errors.</li>
 *     <li>{@code ERROR}: Used to indicate unrecoverable errors.</li>
 *     <li>{@code FATAL}: Used to indicate catastrophic errors.</li>
 * </ol>
*/
public enum LogLevels {

    ///
    DEBUG("DEBUG  ", "\u001B[30m"),
    INFO("INFO   ", "\u001B[34m"),
    SUCCESS("SUCCESS", "\u001B[32m"),
    NOTE("NOTE   ", "\u001B[35m"),
    WARNING("WARNING", "\u001B[33m"),
    ERROR("ERROR  ", "\u001B[31m"),
    FATAL("FATAL  ", "\u001B[31m");

    ///
    /** The space-padded log level name. */
    private String value;

    /** The associated log level ANSI color escape. */
    private String color;

    ///
    /**
     * Instantiates a new {@code LogLevels} constant during class loading.
     * @param value : The space-padded log level name.
     * @param color : The associated log level ANSI color escape.
    */
    private LogLevels(String value, String color) {

        this.value = value;
        this.color = color;
    }

    ///
    /** @return The associated space-padded log level name string. */
    public String getValue() {

        return(value);
    }

    ///..
    /** @return The associated log level ANSI color escape string. */
    public String getColor() {

        return(color);
    }

    ///
}
