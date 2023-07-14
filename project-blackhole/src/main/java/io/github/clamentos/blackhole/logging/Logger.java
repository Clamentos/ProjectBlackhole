package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.common.configuration.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Eager-loaded singleton.</b></p>
 * <p>Logger.</p>
 * This class inserts the produced logs into the log queue.
*/
public class Logger {
    
    private static final Logger INSTANCE = new Logger();
    private final ConfigurationProvider CONFIGS;

    private final LogLevel MIN_LOG_LEVEL;
    private final int MAX_QUEUE_POLLS;
    private final int QUEUE_TIMEOUT;

    private List<LinkedBlockingQueue<Log>> queues;
    private List<LogTask> log_tasks;

    //____________________________________________________________________________________________________________________________________

    private Logger() {

        CONFIGS = ConfigurationProvider.getInstance();
        MIN_LOG_LEVEL = CONFIGS.getConstant(Constants.MIN_LOG_LEVEL, LogLevel.class);
        MAX_QUEUE_POLLS = CONFIGS.getConstant(Constants.MAX_QUEUE_POLLS, Integer.class);
        QUEUE_TIMEOUT = CONFIGS.getConstant(Constants.QUEUE_TIMEOUT, Integer.class);

        queues = new ArrayList<>();
        log_tasks = new ArrayList<>();

        queues.add(new LinkedBlockingQueue<>());
        queues.add(new LinkedBlockingQueue<>());

        log_tasks.add(new LogTask(queues.get(0)));
        log_tasks.add(new LogTask(queues.get(1)));
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Get the {@link LogFileManager} instance created during class loading.
     * @return The {@link ConfigurationProvider} instance.
    */
    public static Logger getInstance() {

        return(INSTANCE);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * <p>Inserts the log into the log queue.</p>
     * This method will block the calling thread up to {@link Constants#QUEUE_TIMEOUT} milliseconds.
     * If the insert times out, this method will retry up to {@link Constants#MAX_QUEUE_POLLS} times.
     * If that fails too, this method will simply return.
     * @param message : The message to log.
     * @param severity : The severity of the log event.
    */
    public void log(String message, LogLevel severity) {

        Log log;
        int i;
        int queue_index;

        if(MIN_LOG_LEVEL.compareTo(severity) <= 0) {

            log = new Log(message, severity);
            i = 0;

            while(i < MAX_QUEUE_POLLS) {

                try {

                    queue_index = (int)(log.id() % queues.size());

                    if(queues.get(queue_index).offer(log, QUEUE_TIMEOUT, TimeUnit.MILLISECONDS) == true) {

                        return;
                    }

                    else {

                        LogPrinter.printToConsole(new Log(

                            "Logger.log 1 > Could not insert into the log queue, timed out.",
                            LogLevel.ERROR
                        ));
                    }
                }

                catch(InterruptedException exc) {

                    LogPrinter.printToConsole(new Log(

                        "Logger.log 2 > Could not insert into the log queue, InterruptedException: " +
                        exc.getMessage() + " Retrying...",
                        LogLevel.WARNING
                    ));
                }

                i++;
            }

            LogPrinter.printToConsole(new Log(

                "Logger.log 3 > Could not insert into the log queue, polls exhausted.",
                LogLevel.ERROR
            ));
        }
    }

    //____________________________________________________________________________________________________________________________________
}
