package io.github.clamentos.blackhole.logging;

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.common.configuration.Constants;

public enum LogLevel {
    
    DEBUG("DEBUG  ", "\u001B[30m", ConfigurationProvider.getInstance().getConstant(Constants.DEBUG_LEVEL_TO_FILE, Boolean.class)),
    INFO("INFO   ", "\u001B[34m", ConfigurationProvider.getInstance().getConstant(Constants.INFO_LEVEL_TO_FILE, Boolean.class)),
    SUCCESS("SUCCESS", "\u001B[32m", ConfigurationProvider.getInstance().getConstant(Constants.SUCCESS_LEVEL_TO_FILE, Boolean.class)),
    NOTE("NOTE   ", "\u001B[35m", ConfigurationProvider.getInstance().getConstant(Constants.NOTE_LEVEL_TO_FILE, Boolean.class)),
    WARNING("WARNING", "\u001B[33m", ConfigurationProvider.getInstance().getConstant(Constants.WARNING_LEVEL_TO_FILE, Boolean.class)),
    ERROR("ERROR  ", "\u001B[31m", ConfigurationProvider.getInstance().getConstant(Constants.ERROR_LEVEL_TO_FILE, Boolean.class));

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
