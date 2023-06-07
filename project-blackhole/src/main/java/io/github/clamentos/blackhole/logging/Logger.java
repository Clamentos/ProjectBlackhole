package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.config.ConfigurationProvider;
import io.github.clamentos.blackhole.config.LogFiles;

import java.util.concurrent.LinkedBlockingQueue;

//________________________________________________________________________________________________________________________________________

/**
 * Logging class responsible for managing the logger thread and inserting logs into the queue.
*/
public class Logger {

    private static volatile Logger INSTANCE;
    private static Object dummy_mutex = new Object();

    private LogLevel min_console_log_level;
    private LogWorker log_worker;
    private LinkedBlockingQueue<Log> logs;

    //____________________________________________________________________________________________________________________________________

    private Logger() {

        logs = new LinkedBlockingQueue<>(ConfigurationProvider.MAX_LOG_QUEUE_SIZE);
        min_console_log_level = ConfigurationProvider.MINIMUM_CONSOLE_LOG_LEVEL;
        log_worker = new LogWorker("LW_0", logs);
        log_worker.start();
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Get the Logger instance.
     * If the instance doesn't exist, create it with default values.
     * See {@link ConfigurationProvider} for more information.
     * @return The Logger instance.
     */
    public static Logger getInstance() {

        Logger temp = INSTANCE;

        if(temp == null) {

            synchronized(dummy_mutex) {

                temp = INSTANCE;

                if(temp == null) {

                    temp = new Logger();
                }
            }
        }

        return(temp);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Adds the message to the log queue.
     * If there is no space in the queue, it will block the thread.
     * @param message : The message to be logged.
     * @param log_level : The severity of the message.
     * @param log_file : The destination file.
     */
    public void log(String message, LogLevel log_level, LogFiles log_file) {

        if(log_file.getLogLevel().compareTo(log_level) >= 0) {

            try {

                logs.put(new Log(message, log_level, log_file));
            }

            catch(InterruptedException exc) {

                exceptionPrinter(exc);
            }
        }
    }

    /**
     * Adds the message to the log queue.
     * If there is no space in the queue, this method will block the thread.
     * @param message : The message to be logged.
     * @param log_level : The severity of the message.
     */
    public void log(String message, LogLevel log_level) {

        if(min_console_log_level.compareTo(log_level) >= 0) {

            try {

                logs.put(new Log(message, log_level, null));
            }
    
            catch(InterruptedException exc) {
    
                exceptionPrinter(exc);
            }
        }
    }

    //____________________________________________________________________________________________________________________________________

    private void exceptionPrinter(InterruptedException exc) {

        LogPrinter.printToConsole("Could not insert into log queue, InterruptedException: " + exc.getMessage(), LogLevel.WARNING);
    }

    //____________________________________________________________________________________________________________________________________
}
