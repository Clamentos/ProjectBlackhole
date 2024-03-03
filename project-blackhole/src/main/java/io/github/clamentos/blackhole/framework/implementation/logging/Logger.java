package io.github.clamentos.blackhole.framework.implementation.logging;

///
import io.github.clamentos.blackhole.framework.implementation.configuration.ConfigurationProvider;

///..
import io.github.clamentos.blackhole.framework.implementation.logging.exportable.LogLevels;

///..
import io.github.clamentos.blackhole.framework.implementation.tasks.TaskManager;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.exportable.ExceptionFormatter;

///.
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

///
/**
 * <h3>Logger</h3>
 * Inserts the produced logs into the log queue.
 * @apiNote Use this class when asynchronous logging is desired for performance reasons.
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
     * Instantiates a new {@code Logger} object and starts the log task.
     * @apiNote Since this class is a singleton, this constructor will only be called once.
    */
    private Logger() {

        log_printer = LogPrinter.getInstance();
        queue = new LinkedBlockingQueue<>(ConfigurationProvider.getInstance().MAX_LOG_QUEUE_SIZE);
        TaskManager.getInstance().launchThread(new LogTask(queue), "LogTask");

        log_printer.logToFile("Logger.new => Instantiated successfully", LogLevels.SUCCESS);
    }

    ///
    /** @return The {@link Logger} instance created during class loading. */
    public static Logger getInstance() {

        return(INSTANCE);
    }

    ///
    /**
     * Logs the message asynchronously, blocking up to {@link ConfigurationProvider#LOG_QUEUE_INSERT_TIMEOUT} milliseconds.
     * @param message : The message to log.
     * @param severity : The severity of the log event.
     * @apiNote If this method couldn't complete in such amount of time, it will log the message synchronously as a fallback.
    */
    public void log(String message, LogLevels severity) {

        Log log = new Log(message, severity, log_printer.getNextId());
        boolean inserted = false;
        int busy_wait_attempts = 0;

        while(busy_wait_attempts < ConfigurationProvider.getInstance().MAX_LOG_QUEUE_INSERT_ATTEMPTS) {

            inserted = queue.offer(log);

            if(inserted == true) {

                return;
            }

            busy_wait_attempts++;
        }

        try {

            inserted = queue.offer(log, ConfigurationProvider.getInstance().LOG_QUEUE_INSERT_TIMEOUT, TimeUnit.MILLISECONDS);
        }

        catch(InterruptedException exc) {

            log_printer.logToFile(

                ExceptionFormatter.format("Logger.log => Could not insert the log into the queue", exc, "Logging synchronously..."),
                LogLevels.NOTE
            );
        }

        if(inserted == false) {

            try {

                log_printer.printToFile(log);
            }

            catch(NullPointerException exc) {

                log_printer.logToFile(

                    ExceptionFormatter.format("Logger.log => Could not synchronously log", exc, "Skipping this one..."),
                    LogLevels.ERROR
                );
            }
        }
    }

    ///
}
