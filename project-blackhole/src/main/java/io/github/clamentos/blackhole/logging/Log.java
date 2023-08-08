package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

/**
 * <h3>Log object</h3>
 * This simple record class is used as a template for generating the actual print.
 * @apiNote This class is <b>immutable data</b>.
*/
public final record Log(
    
    String message,
    LogLevel log_level,
    long timestamp,
    long id

    //____________________________________________________________________________________________________________________________________
) {

    /**
     * Instantiates a new {@link Log} object.
     * @param message : The log message.
     * @param log_level : The log severity.
     * @param id : The id of this log item.
    */
    protected Log(String message, LogLevel log_level, long id) {

        this(message, log_level, System.currentTimeMillis(), id);
    }

    //____________________________________________________________________________________________________________________________________
}