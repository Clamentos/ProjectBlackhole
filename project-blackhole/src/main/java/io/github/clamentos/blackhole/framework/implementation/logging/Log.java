package io.github.clamentos.blackhole.framework.implementation.logging;

///
/**
 * <h3>Log</h3>
 * Template for generating the final log print.
*/
public final record Log(

    ///
    /** The log message string. */
    String message,

    /** The log severity. */
    LogLevels log_level,

    /** The log instantiation timestamp in milliseconds. */
    long timestamp,

    /** The runtime-unique log identifier. */
    long id

    ///
) {

    ///
    /**
     * Instantiates a new {@link Log} object.
     * @param message : The log message.
     * @param log_level : The log severity.
     * @param id : The id of the log item.
     * @see LogLevels
    */
    protected Log(String message, LogLevels log_level, long id) {

        this(message, log_level, System.currentTimeMillis(), id);
    }

    ///
}
