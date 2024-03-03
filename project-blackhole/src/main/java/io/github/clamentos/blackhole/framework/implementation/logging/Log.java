package io.github.clamentos.blackhole.framework.implementation.logging;

///
import io.github.clamentos.blackhole.framework.implementation.logging.exportable.LogLevels;

///
/**
 * <h3>Log</h3>
 * Internal log object used to generate the final print.
*/
public final record Log(

    ///
    /** The log message. */
    String message,

    /** The log severity. */
    LogLevels log_level,

    /** The log instantiation timestamp in milliseconds. */
    long timestamp,

    /** The runtime unique log identifier. */
    long id

    ///
) {

    ///
    /**
     * Instantiates a new {@link Log} object.
     * @param message : The log message.
     * @param log_level : The log severity.
     * @param id : The runtime unique log identifier.
    */
    protected Log(String message, LogLevels log_level, long id) {

        this(message, log_level, System.currentTimeMillis(), id);
    }

    ///
}
