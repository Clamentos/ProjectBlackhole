// OK
package io.github.clamentos.blackhole.framework.logging;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Enumeration of all the possible log levels.</p>
 * <p>Each entry is composed of the log level name + color + destination.</p>
 * 
 * The log levels in increasing relevance are:
 * <ol>
 *     <li>{@code DEBUG}: used for debugging.</li>
 *     <li>{@code INFO}: used to give harmless but useful information.</li>
 *     <li>{@code SUCCESS}: used to indicate the positive outcome of a critical process.</li>
 *     <li>{@code NOTE}: used to indicate failures on non critical processes.</li>
 *     <li>{@code WARNING}: used to indicate that a critical process encountered an unusual
 *         situation but was able to recover without service interruptions.</li>
 *     <li>{@code ERROR}: used to indicate that a critical process encountered an unusual or unexpected
 *         situation that was not able to recover without service interruptions.</li>
 * </ol>
*/
public enum LogLevel {
    
    DEBUG("DEBUG  ", "\u001B[30m", ConfigurationProvider.getInstance().DEBUG_LEVEL_TO_FILE),
    INFO("INFO   ", "\u001B[34m", ConfigurationProvider.getInstance().INFO_LEVEL_TO_FILE),
    SUCCESS("SUCCESS", "\u001B[32m", ConfigurationProvider.getInstance().SUCCESS_LEVEL_TO_FILE),
    NOTE("NOTE   ", "\u001B[35m", ConfigurationProvider.getInstance().NOTE_LEVEL_TO_FILE),
    WARNING("WARNING", "\u001B[33m", ConfigurationProvider.getInstance().WARNING_LEVEL_TO_FILE),
    ERROR("ERROR  ", "\u001B[31m", ConfigurationProvider.getInstance().ERROR_LEVEL_TO_FILE);

    //____________________________________________________________________________________________________________________________________

    private String value;
    private String color;
    private boolean to_file;

    //____________________________________________________________________________________________________________________________________

    // Thread safe
    private LogLevel(String value, String color, boolean to_file) {

        this.value = value;
        this.color = color;
        this.to_file = to_file;
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Get the associated log level string.
     * @return the log level string (always well-defined).
    */
    public String getValue() {

        return(value);
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * Get the associated log level color.
     * @return the log level color (always well-defined).
    */
    public String getColor() {

        return(color);
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * Get the associated destination for {@code this} log level.
     * @return {@code true} if the destination is the log file, {@code false} if console.
    */
    public boolean toFile() {

        return(to_file);
    }

    //____________________________________________________________________________________________________________________________________
}
