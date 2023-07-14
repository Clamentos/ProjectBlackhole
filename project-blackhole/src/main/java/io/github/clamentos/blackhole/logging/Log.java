package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import java.util.Date;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Log object.</p>
 * This class is used as a template for generating the actual print.
*/
public record Log(
    
    String message,
    LogLevel log_level,
    Date creation_date,
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
            new Date(System.currentTimeMillis()),
            LogIdGenerator.getInstance().getNext()
        );
    }

    //____________________________________________________________________________________________________________________________________
}