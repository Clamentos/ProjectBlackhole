package io.github.clamentos.blackhole.logging;

///
/**
 * <h3>Log object</h3>
 * This simple record class is used as a template for generating the actual log print.
*/
public final record Log(

    ///
    String message,
    LogLevel log_level,
    long timestamp,
    long id

    ///
) {

    /**
     * Instantiates a new {@link Log} object.
     * @param message : The log message.
     * @param log_level : The log severity.
     * @param id : The id of this log item.
     * @see {@link LogLevel}
    */
    protected Log(String message, LogLevel log_level, long id) {

        this(message, log_level, System.currentTimeMillis(), id);
    }

    ///
}
