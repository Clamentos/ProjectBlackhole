package io.github.clamentos.blackhole.logging;

///
import io.github.clamentos.blackhole.configuration.ConfigurationProvider;

///
/**
 * <h3>Enumeration of all the possible log levels</h3>
 * 
 * <p>Each entry is composed of the log level name + color + destination.</p>
 * 
 * The log levels in increasing relevance are:
 * <ol>
 *     <li>{@code DEBUG}: Used for debugging.</li>
 *     <li>{@code INFO}: Used to give harmless but useful information.</li>
 *     <li>{@code SUCCESS}: Used to indicate the positive outcome of a critical process.</li>
 *     <li>{@code NOTE}: Used to indicate failures on non critical processes.</li>
 * 
 *     <li>{@code WARNING}: Used to indicate that a critical process encountered an unusual situation but
 *         was able to recover without service interruptions.</li>
 * 
 *     <li>{@code ERROR}: Used to indicate that a critical process encountered an unusual or unexpected
 *         situation that was not able to recover without service interruptions.</li>
 * </ol>
*/
public enum LogLevel {

    ///
    DEBUG("DEBUG  ", "\u001B[30m", ConfigurationProvider.getInstance().DEBUG_LEVEL_TO_FILE),
    INFO("INFO   ", "\u001B[34m", ConfigurationProvider.getInstance().INFO_LEVEL_TO_FILE),
    SUCCESS("SUCCESS", "\u001B[32m", ConfigurationProvider.getInstance().SUCCESS_LEVEL_TO_FILE),
    NOTE("NOTE   ", "\u001B[35m", ConfigurationProvider.getInstance().NOTE_LEVEL_TO_FILE),
    WARNING("WARNING", "\u001B[33m", ConfigurationProvider.getInstance().WARNING_LEVEL_TO_FILE),
    ERROR("ERROR  ", "\u001B[31m", ConfigurationProvider.getInstance().ERROR_LEVEL_TO_FILE);

    ///
    private String value;
    private String color;
    private boolean to_file;

    ///
    private LogLevel(String value, String color, boolean to_file) {

        this.value = value;
        this.color = color;
        this.to_file = to_file;
    }

    ///
    /** @return the associated space-padded log level name string. */
    public String getValue() {

        return(value);
    }

    /** @return the associated log level color escape string. */
    public String getColor() {

        return(color);
    }

    /** @return {@code true} if the destination is the log file, {@code false} if console. */
    public boolean toFile() {

        return(to_file);
    }

    ///
}
