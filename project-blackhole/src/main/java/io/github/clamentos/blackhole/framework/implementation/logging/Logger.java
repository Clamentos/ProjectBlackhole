package io.github.clamentos.blackhole.framework.implementation.logging;

///
import io.github.clamentos.blackhole.framework.implementation.configuration.ConfigurationProvider;

///..
import io.github.clamentos.blackhole.framework.implementation.tasks.TaskManager;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.ExceptionFormatter;

///.
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

///
/**
 * <h3>Logger</h3>
 * <p>Inserts the produced logs into the log queue.</p>
 * Use this class when asynchronous logging is desired for performance reasons.
 * @see LogTask
*/
public final class Logger {

    ///
    /** Singleton instance of {@code this} instantiated during class loading. */
    private static final Logger INSTANCE = new Logger();

    ///.
    /** The service used to actually write the log. */
    private final LogPrinter log_printer;

    ///..
    /** The queue used to insert the log objects into. */
    private final BlockingQueue<Log> queue;

    ///
    /**
     * <p>Instantiates a new {@code Logger} object and starts the log task.</p>
     * Since this class is a singleton, this constructor will only be called once.
     * @see LogTask
    */
    private Logger() {

        log_printer = LogPrinter.getInstance();
        queue = new LinkedBlockingQueue<>(ConfigurationProvider.getInstance().MAX_LOG_QUEUE_SIZE);

        TaskManager.getInstance().launchThread(new LogTask(queue), "LogTask");

        log_printer.logToFile("Logger.new >> Instantiated successfully", LogLevels.SUCCESS);
    }

    ///
    /** @return The {@link Logger} instance created during class loading. */
    public static Logger getInstance() {

        return(INSTANCE);
    }

    ///
    /**
     * <p>Logs the message asynchronously, blocking up to {@link ConfigurationProvider#LOG_QUEUE_INSERT_TIMEOUT} milliseconds.</p>
     * If this method couldn't complete in such amount of time, it will log the message synchronously as a fallback.
     * @param message : The message to log.
     * @param severity : The severity of the log event.
     * @throws IllegalArgumentException If {@code severity} is {@code null}.
     * @see LogLevels
    */
    public void log(String message, LogLevels severity) throws IllegalArgumentException {

        if(severity == null) {

            throw new IllegalArgumentException("(Logger.log) -> The argument \"severity\" cannot be null");
        }

        Log log = new Log(message, severity, log_printer.getNextId());
        boolean inserted = false;
        int busy_wait_attempts = 0;

        // Attempt to insert aggressively.
        while(busy_wait_attempts < ConfigurationProvider.getInstance().MAX_LOG_QUEUE_INSERT_ATTEMPTS) {

            inserted = queue.offer(log);

            if(inserted == true) {

                return;
            }

            busy_wait_attempts++;
        }

        // The busy wait expired the attempts. Insert with blocking behaviour.
        try {

            inserted = queue.offer(log, ConfigurationProvider.getInstance().LOG_QUEUE_INSERT_TIMEOUT, TimeUnit.MILLISECONDS);
        }

        catch(InterruptedException exc) {

            log_printer.logToFile(ExceptionFormatter.format("Logger.log >> ", exc, " >> Ignoring..."), LogLevels.NOTE);
        }

        // Log synchronously as a fallback.
        if(inserted == false) {

            log_printer.printToFile(log);
        }
    }

    ///
}
