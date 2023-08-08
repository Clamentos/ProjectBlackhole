package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.scaffolding.tasks.TaskManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

//________________________________________________________________________________________________________________________________________

/**
 * <h3>Logger service</h3>
 * 
 * This class inserts the produced logs into the log queue.
 * Use this class when asynchronous logging is desired for performance reasons.
 * 
 * @see {@link LogPrinter}
 * @apiNote This class is an <b>eager-loaded singleton</b>.
*/
public class Logger {
    
    private static final Logger INSTANCE = new Logger();

    private final int NUM_LOG_TASKS;
    private final int MAX_LOG_QUEUE_SIZE;
    private final int MIN_LOG_LEVEL;
    private final int INSERT_TIMEOUT;

    private LogPrinter log_printer;

    // Multiple queues allow to spread the lock contention (there could be millions of v-threads).
    private List<LinkedBlockingQueue<Log>> queues;

    //____________________________________________________________________________________________________________________________________

    // Instantiates all the queues and launches the log tasks.
    private Logger() {

        log_printer = LogPrinter.getInstance();

        NUM_LOG_TASKS = ConfigurationProvider.getInstance().NUM_LOG_TASKS;
        MAX_LOG_QUEUE_SIZE = ConfigurationProvider.getInstance().MAX_LOG_QUEUE_SIZE;
        MIN_LOG_LEVEL = ConfigurationProvider.getInstance().MIN_LOG_LEVEL;
        INSERT_TIMEOUT = ConfigurationProvider.getInstance().LOG_QUEUE_INSERT_TIMEOUT;

        queues = new ArrayList<>();

        for(int i = 0; i < NUM_LOG_TASKS; i++) {

            queues.add(new LinkedBlockingQueue<>(MAX_LOG_QUEUE_SIZE));
            TaskManager.getInstance().launchNewLogTask(queues.get(i));
        }

        log_printer.log("Logger.new > Instantiated successfully", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    /** @return The {@link ConfigurationProvider} instance created during class loading. */
    public static Logger getInstance() {

        return(INSTANCE);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Inserts the log into the log queue.
     * <p>This method will block the calling thread up to
     * {@link ConfigurationProvider#LOG_QUEUE_INSERT_TIMEOUT} milliseconds. If the insert times out,
     * this method will simply fallback to log synchronously.</p>
     * @param message : The message to log.
     * @param severity : The severity of the log event.
     * @throws IllegalArgumentException If {@code severity} is {@code null}.
    */
    public void log(String message, LogLevel severity) throws IllegalArgumentException {

        Log log;
        int idx;

        if(severity == null) {

            throw new IllegalArgumentException("Log level cannot be null");
        }

        if(severity.ordinal() >= MIN_LOG_LEVEL) {

            log = new Log(message, severity, log_printer.getNextId());

            while(true) {

                try {

                    /*
                     * Choose the queue. Use the MOD operation to guarantee uniformity so that
                     * each queue gets an equal amount of work.
                    */

                    idx = (int)(log.id() % queues.size());

                    if(queues.get(idx).offer(log, INSERT_TIMEOUT, TimeUnit.MILLISECONDS) == true) {

                        return;
                    }

                    log_printer.log(message, severity);
                }

                catch(InterruptedException exc) {

                    log_printer.log(

                        "Logger.log > Interrupted while trying to insert a log into the queue, retrying",
                        LogLevel.WARNING
                    );
                }
            }
        }
    }

    //____________________________________________________________________________________________________________________________________
}
