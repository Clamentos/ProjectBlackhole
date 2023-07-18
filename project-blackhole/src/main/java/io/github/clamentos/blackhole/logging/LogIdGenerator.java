// OK
package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import java.util.concurrent.atomic.AtomicLong;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Eager-loaded singleton.</b></p>
 * <p>Log id generator.</p>
 * This class generates unique sequential ids for each log event.
*/
public class LogIdGenerator {

    //____________________________________________________________________________________________________________________________________
    
    private static final LogIdGenerator INSTANCE = new LogIdGenerator();
    private AtomicLong id;

    //____________________________________________________________________________________________________________________________________

    private LogIdGenerator() {

        id = new AtomicLong(0);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Get the {@link LogIdGenerator} instance created during class loading.
     * @return The {@link LogIdGenerator} instance.
    */
    public static LogIdGenerator getInstance() {

        return(INSTANCE);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Get the next unique log id.
     * @return The next log id in the sequence. Overflows don't cause any exception
     *         and simply wrap around.
    */
    public long getNext() {

        return(id.getAndIncrement());
    }

    //____________________________________________________________________________________________________________________________________
}