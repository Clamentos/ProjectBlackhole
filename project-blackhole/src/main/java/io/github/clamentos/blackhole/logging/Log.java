package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>STEREOTYPE: Immutable data.</b></p>
 * <p>Log object.</p>
 * This class is used as a template for generating the actual print.
 * The getters are all standard and thread safe.
*/
public final record Log(
    
    String message,
    LogLevel log_level,
    long timestamp,
    long id

    //____________________________________________________________________________________________________________________________________
) {

    /**
     * <p><b>This method is thread safe.</p></b>
     * Instantiates a new {@link Log} object.
     * @param message : The log message.
     * @param log_level : The log severity.
    */
    public Log(String message, LogLevel log_level) {

        this(
            
            message,
            log_level,
            System.currentTimeMillis(),
            LogPrinter.getInstance().getNextId()
        );
    }

    //____________________________________________________________________________________________________________________________________

    // Thread safe obviously.
    // Just for the LogPrinter to log things during initialization (otherwise cyclic dependency).
    protected Log(String message, LogLevel log_level, long id) {

        this(message, log_level, System.currentTimeMillis(), id);
    }

    //____________________________________________________________________________________________________________________________________
}