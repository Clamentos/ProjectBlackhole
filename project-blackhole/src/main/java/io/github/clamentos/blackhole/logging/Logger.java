package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.config.ConfigurationProvider;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

//________________________________________________________________________________________________________________________________________

/**
 * Logging class responsible for managing the logger thread and inserting logs into the queue.
*/
public class Logger {

    private static volatile Logger INSTANCE;
    private static ReentrantLock lock = new ReentrantLock();

    private LogLevel min_console_log_level;
    private LogWorker log_worker;
    private LinkedBlockingQueue<Log> logs;

    //____________________________________________________________________________________________________________________________________

    private Logger() {
        
        min_console_log_level = ConfigurationProvider.MINIMUM_CONSOLE_LOG_LEVEL;
        logs = new LinkedBlockingQueue<>(ConfigurationProvider.MAX_LOG_QUEUE_SIZE);
        log_worker = new LogWorker(logs);
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

            lock.lock();
            temp = INSTANCE;

            if(temp == null) {

                INSTANCE = temp = new Logger();
                LogPrinter.printToConsole("Logger loaded " + INSTANCE, LogLevel.SUCCESS);
            }

            lock.unlock();
        }

        return(temp);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Adds the message to the log queue.
     * If there is no space in the queue, this method will block the thread.
     * @param message : The message to be logged.
     * @param log_level : The severity of the message.
     */
    public void log(String message, LogLevel log_level) {

        if(min_console_log_level.compareTo(log_level) <= 0) {

            try {

                logs.put(new Log(message, log_level));
            }
    
            catch(InterruptedException exc) {
    
                LogPrinter.printToConsole("Could not insert into log queue, InterruptedException: " + exc.getMessage(), LogLevel.WARNING);
            }
        }
    }

    //____________________________________________________________________________________________________________________________________
}
