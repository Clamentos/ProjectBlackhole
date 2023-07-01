package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.config.ConfigurationProvider;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Enumeration listing al the log levels.</p>
 * Each entry is composed of the log level name + color + destination.
 * The destination must be configured in the {@link ConfigurationProvider}.
 * <p>The log levels in increasing order are:</p>
 * <ol>
 *     <li>DEBUG</li>
 *     <li>INFO</li>
 *     <li>SUCCESS</li>
 *     <li>NOTE</li>
 *     <li>WARNING</li>
 *     <li>ERROR</li>
 * </ol>
*/
public enum LogLevel {

    DEBUG("DEBUG  ", "\u001B[30m", ConfigurationProvider.DEBUG_LEVEL_TO_FILE),
    INFO("INFO   ", "\u001B[34m", ConfigurationProvider.INFO_LEVEL_TO_FILE),
    SUCCESS("SUCCESS", "\u001B[32m", ConfigurationProvider.SUCCESS_LEVEL_TO_FILE),
    NOTE("NOTE   ", "\u001B[35m", ConfigurationProvider.NOTE_LEVEL_TO_FILE),
    WARNING("WARNING", "\u001B[33m", ConfigurationProvider.WARNING_LEVEL_TO_FILE),
    ERROR("ERROR  ", "\u001B[31m", ConfigurationProvider.ERROR_LEVEL_TO_FILE);

    //____________________________________________________________________________________________________________________________________

    private String value;
    private String color;
    private boolean to_file;

    //____________________________________________________________________________________________________________________________________

    private LogLevel(String value, String color, boolean to_file) {

        this.value = value;
        this.color = color;
        this.to_file = to_file;
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

    /**
     * Get the destination of the associated log level.
     * @return the log level destination, either to console or to file.
    */
    public boolean getToFile() {

        return(to_file);
    }

    //____________________________________________________________________________________________________________________________________
}
