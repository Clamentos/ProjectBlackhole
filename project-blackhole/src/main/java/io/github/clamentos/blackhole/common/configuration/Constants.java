package io.github.clamentos.blackhole.common.configuration;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.logging.LogLevel;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Enumeration class that lists all the possible configuration
 * parameters, along with their default values and types.</p>
 * 
 * <ul>
 *     <li>{@code MAX_QUEUE_POLLS}: maximum number of polls on {@link LinkedBlockingQueue}
 *         before blocking. Used by {@link LogTask}</li>
 *     <li>{@code MIN_LOG_LEVEL}: minimum log level below which logs are discarded.
 *         This parameter has no effect if the {@link LogPrinter} object is used directly.</li>
 *     <li>{@code MAX_LOG_FILE_SIZE}: maximum log file size in bytes. Above this,
 *         a new log file will be created.</li>
 *     <li>{@code DEBUG_LEVEL_TO_FILE}: specifies if logs with level of {@link LogLevel#DEBUG}
 *         should be printed to file or console.</li>
 *     <li>{@code INFO_LEVEL_TO_FILE}: specifies if logs with level of {@link LogLevel#INFO}
 *         should be printed to file or console.</li>
 *     <li>{@code SUCCESS_LEVEL_TO_FILE}: specifies if logs with level of {@link LogLevel#SUCCESS}
 *         should be printed to file or console.</li>
 *     <li>{@code NOTE_LEVEL_TO_FILE}: specifies if logs with level of {@link LogLevel#NOTE}
 *         should be printed to file or console.</li>
 *     <li>{@code WARNING_LEVEL_TO_FILE}: specifies if logs with level of {@link LogLevel#WARNING}
 *         should be printed to file or console.</li>
 *     <li>{@code ERROR_LEVEL_TO_FILE}: specifies if logs with level of {@link LogLevel#ERROR}
 *         should be printed to file or console.</li>
 *     <li>{@code SERVER_PORT}: specifies the TCP port of the server.</li>
 *     <li>{@code SOCKET_TIMEOUT}: specifies the amount of milliseconds until a socket times out.</li>
 *     <li>{@code MAX_START_RETRIES}: specifies how many retries to do before giving up
 *         when attempting to establish any kind of connection.</li>
 *     <li>{@code DB_ADDRESS}: the address of the database.</li>
 *     <li>{@code DB_USERNAME}: the database username.</li>
 *     <li>{@code DB_PASSWORD}: the database password.</li>
 * </ul>
*/
public enum Constants {

    MAX_QUEUE_POLLS(100, Integer.class),

    MIN_LOG_LEVEL(LogLevel.INFO, LogLevel.class),
    MAX_LOG_FILE_SIZE(10_000_000, Integer.class),
    DEBUG_LEVEL_TO_FILE(false, Boolean.class),
    INFO_LEVEL_TO_FILE(false, Boolean.class),
    SUCCESS_LEVEL_TO_FILE(false, Boolean.class),
    NOTE_LEVEL_TO_FILE(false, Boolean.class),
    WARNING_LEVEL_TO_FILE(false, Boolean.class),
    ERROR_LEVEL_TO_FILE(false, Boolean.class),

    SERVER_PORT(8080, Integer.class),
    SOCKET_TIMEOUT(10_000, Integer.class),
    MAX_START_RETRIES(5, Integer.class),

    DB_ADDRESS(null, String.class),
    DB_USERNAME(null, String.class),
    DB_PASSWORD(null, String.class);
    
    // ...

    //____________________________________________________________________________________________________________________________________

    private Object default_value;
    private Class<?> type;

    //____________________________________________________________________________________________________________________________________

    private Constants(Object default_value, Class<?> type) {

        this.default_value = default_value;
        this.type = type;
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Simple getter for the value.
     * @return The raw, untyped value associated with {@code this} entry.
    */
    public Object getValue() {

        return(default_value);
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * Simple getter for the type.
     * @return The type associated with {@code this} entry.
    */
    public Class<?> getType() {

        return(type);
    }

    //____________________________________________________________________________________________________________________________________
}
