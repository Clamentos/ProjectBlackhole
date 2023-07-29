package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.framework.tasks.TaskManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>STEREOTYPE: Eager-loaded singleton.</b></p>
 * <p>Logger service.</p>
 * This class inserts the produced logs into the log queue.
 * Use this class when asynchronous logging is needed for performance reasons.
 * See {@link LogPrinter} for direct synchronous logging.
*/
public class Logger {
    
    private static final Logger INSTANCE = new Logger();

    private ConfigurationProvider configuration_provider;
    private LogPrinter log_printer;

    // Multiple queues allow to spread the lock contention (there could be millions of v-threads).
    private List<LinkedBlockingQueue<Log>> queues;

    //____________________________________________________________________________________________________________________________________

    // Thread safe.
    private Logger() {

        configuration_provider = ConfigurationProvider.getInstance();
        log_printer = LogPrinter.getInstance();

        queues = new ArrayList<>();

        for(int i = 0; i < configuration_provider.NUM_LOG_TASKS; i++) {

            queues.add(new LinkedBlockingQueue<>(configuration_provider.MAX_LOG_QUEUE_SIZE));
            TaskManager.getInstance().launchNewLogTask(queues.get(i));
        }

        log_printer.log("Logger.new > Instantiated successfully", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * @return The {@link ConfigurationProvider} instance created during class loading.
    */
    public static Logger getInstance() {

        return(INSTANCE);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * <p>Inserts the log into the log queue.</p>
     * This method will block the calling thread up to
     * {@link ConfigurationProvider#LOG_QUEUE_INSERT_TIMEOUT} milliseconds.
     * If the insert times out, this method will simply fallback to log synchronously.
     * @param message : The message to log.
     * @param severity : The severity of the log event.
     * @throws IllegalArgumentException If {@code severity} is {@code null}.
    */
    public void log(String message, LogLevel severity) throws IllegalArgumentException {

        Log log;
        int queue_index;

        if(severity == null) {

            throw new IllegalArgumentException();
        }

        if(severity.ordinal() >= configuration_provider.MIN_LOG_LEVEL) {

            log = new Log(message, severity);

            while(true) {

                try {

                    // Choose the queue. Use the MOD operation to guarantee uniformity so that
                    // each queue gets an equal amount of work.
                    queue_index = (int)(log.id() % queues.size());

                    if(queues.get(queue_index).offer(log, configuration_provider.LOG_QUEUE_INSERT_TIMEOUT, TimeUnit.MILLISECONDS) == true) {

                        return;
                    }

                    else {

                        log_printer.log(message, severity);
                    }
                }

                catch(InterruptedException exc) {

                    log_printer.log(

                        "Logger.log > Could not insert into the log queue, InterruptedException: " +
                        exc.getMessage() + " Retrying",
                        LogLevel.WARNING
                    );
                }

            }
        }
    }

    //____________________________________________________________________________________________________________________________________
}
