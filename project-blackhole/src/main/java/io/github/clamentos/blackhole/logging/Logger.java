package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.WorkerManager;
import io.github.clamentos.blackhole.common.config.ConfigurationProvider;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

//________________________________________________________________________________________________________________________________________

/**
 * Logging class responsible for managing the logger threads and inserting the logs into the queue.
*/
public class Logger extends WorkerManager<Log, LogWorker> {

    private static volatile Logger INSTANCE;
    private static ReentrantLock lock = new ReentrantLock();

    private LogLevel min_console_log_level;

    //____________________________________________________________________________________________________________________________________

    private Logger(BlockingQueue<Log> log_queue, LogWorker[] log_workers) {
        
        super(log_queue, log_workers);
        min_console_log_level = ConfigurationProvider.MINIMUM_CONSOLE_LOG_LEVEL;
        LogPrinter.printToConsole("Logger instantiated and workers started", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Gets the Logger instance.
     * If the instance doesn't exist, create it with the values configured in
     * {@link ConfigurationProvider} and start the workers.
     * @return The Logger instance.
    */
    public static Logger getInstance() {

        Logger temp = INSTANCE;

        LinkedBlockingQueue<Log> log_queue;
        LogWorker[] log_workers;

        if(temp == null) {

            lock.lock();
            temp = INSTANCE;

            if(temp == null) {

                log_queue = new LinkedBlockingQueue<>(ConfigurationProvider.MAX_LOG_QUEUE_SIZE);
                log_workers = new LogWorker[ConfigurationProvider.LOG_WORKERS];

                for(LogWorker worker : log_workers) {

                    worker = new LogWorker(log_queue);
                    worker.start();
                }

                INSTANCE = temp = new Logger(log_queue, log_workers);
            }

            lock.unlock();
        }

        return(temp);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Adds the message to the log queue.
     * If there is no space in the queue, this method will block the thread.
     * @param message : The message to be logged.
     * @param log_level : The severity of the message.
    */
    public void log(String message, LogLevel log_level) {

        if(min_console_log_level.compareTo(log_level) <= 0) {

            try {

                super.getResourceQueue().put(new Log(message, log_level));
            }
    
            catch(InterruptedException exc) {
    
                LogPrinter.printToConsole("Could not insert into the log queue, InterruptedException: " + exc.getMessage(), LogLevel.WARNING);
            }
        }
    }

    //____________________________________________________________________________________________________________________________________
}
