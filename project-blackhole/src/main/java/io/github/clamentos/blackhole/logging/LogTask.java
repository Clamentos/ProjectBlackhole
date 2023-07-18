// OK
package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.common.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.common.framework.ContinuousTask;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Stoppable Runnable.</b></p>
 * <p>Log task.</p>
 * This class is responsible for fetching the logs from the log queue and printing them.
*/
public class LogTask extends ContinuousTask {

    private ConfigurationProvider configuration_provider;

    private final HashMap<LogLevel, Boolean> TO_FILE_MAP;
    private LinkedBlockingQueue<Log> queue;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Instantiate a new {@link LogTask} with the given queue.
     * @param queue : The log queue from where to fetch the logs.
     * @throws NullPointerException If {@code queue} is {@code null}.
    */
    public LogTask(LinkedBlockingQueue<Log> queue, long id) throws NullPointerException {

        super(id);

        if(queue == null) throw new NullPointerException();

        configuration_provider = ConfigurationProvider.getInstance();
        this.queue = queue;

        TO_FILE_MAP = new HashMap<>();

        TO_FILE_MAP.put(LogLevel.DEBUG, configuration_provider.DEBUG_LEVEL_TO_FILE);
        TO_FILE_MAP.put(LogLevel.INFO, configuration_provider.INFO_LEVEL_TO_FILE);
        TO_FILE_MAP.put(LogLevel.SUCCESS, configuration_provider.SUCCESS_LEVEL_TO_FILE);
        TO_FILE_MAP.put(LogLevel.NOTE, configuration_provider.NOTE_LEVEL_TO_FILE);
        TO_FILE_MAP.put(LogLevel.WARNING, configuration_provider.WARNING_LEVEL_TO_FILE);
        TO_FILE_MAP.put(LogLevel.ERROR, configuration_provider.ERROR_LEVEL_TO_FILE);

        LogPrinter.printToConsole(new Log("LogTask.new > Log task instantiated successfully", LogLevel.SUCCESS));
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * {@inheritDoc}
    */
    @Override
    public void setup() {

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * {@inheritDoc}
    */
    @Override
    public void work() {

        iteration();
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * {@inheritDoc}
    */
    @Override
    public void terminate() {

        // Loop as long as there are logs in the queue, then exit.
        while(queue.isEmpty() == false) {

            iteration();
        }

        LogPrinter.printToConsole(new Log("LogTask.terminate > Shut down successfull", LogLevel.SUCCESS));
    }

    //____________________________________________________________________________________________________________________________________

    private void iteration() {

        boolean relax;
        int count;
        Log log;

        relax = true;
        count = 0;

        // Poll aggressively with N retries.
        while(count < configuration_provider.MAX_LOG_QUEUE_POLLS) {

            log = queue.poll();

            if(log != null) {

                relax = false;
                logTheLog(log);

                break;
            }

            count++;
        }

        // Block on queue when the retries are exhausted.
        if(relax == true) {

            try {

                // Poll doesn't generate InterrupredException when it times out, it simply returns null.
                log = queue.poll(configuration_provider.LOG_QUEUE_TIMEOUT, TimeUnit.MILLISECONDS);

                if(log != null) {

                    logTheLog(log);
                }
            }

            catch(InterruptedException exc) {

                super.stop();
            }
        }
    }

    private void logTheLog(Log log) {

        if(TO_FILE_MAP.get(log.log_level()) == true) {

            LogPrinter.printToFile(log);
        }

        else {

            LogPrinter.printToConsole(log);
        }
    }

    //____________________________________________________________________________________________________________________________________
}
