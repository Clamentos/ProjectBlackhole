package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.config.ConfigurationProvider;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

//________________________________________________________________________________________________________________________________________

/**
 * Logging class responsible for managing the logger threads and inserting the logs into the queue.
*/
public class Logger {

    private static volatile Logger INSTANCE;
    private static ReentrantLock lock = new ReentrantLock();

    private LogLevel min_console_log_level;
    private LinkedBlockingQueue<Log> logs;
    private LogWorker[] log_workers;

    //____________________________________________________________________________________________________________________________________

    private Logger() {
        
        min_console_log_level = ConfigurationProvider.MINIMUM_CONSOLE_LOG_LEVEL;
        logs = new LinkedBlockingQueue<>(ConfigurationProvider.MAX_LOG_QUEUE_SIZE);

        log_workers = new LogWorker[ConfigurationProvider.LOG_WORKERS];

        for(LogWorker worker : log_workers) {

            worker = new LogWorker(logs);
            worker.start();
        }

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

        if(temp == null) {

            lock.lock();
            temp = INSTANCE;

            if(temp == null) {

                INSTANCE = temp = new Logger();
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

                logs.put(new Log(message, log_level));
            }
    
            catch(InterruptedException exc) {
    
                LogPrinter.printToConsole("Could not insert into the log queue, InterruptedException: " + exc.getMessage(), LogLevel.WARNING);
            }
        }
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Starts all the inactive {@link LogWorker}.
    */
    public synchronized void startWorkers() {

        for(LogWorker worker : log_workers) {

            if(worker.getRunning() == false) {

                worker.start();
            }
        }
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Stops all the active {@link LogWorker}.
     * @param wait : Waits for the workers to drain the log queue before stopping it.
     *               If set to false, it will stop the workers as soon as they finish
     *               logging the current message.
    */
    public synchronized void stopWorkers(boolean wait) {

        if(wait == true) {

            while(true) {

                if(logs.size() == 0) {

                    stopWorkers();
                    break;
                }
            }
        }

        else {

            stopWorkers();
        }
    }

    //____________________________________________________________________________________________________________________________________

    private void stopWorkers() {

        for(LogWorker worker : log_workers) {

            worker.halt();
            worker.interrupt();
        }
    }

    //____________________________________________________________________________________________________________________________________
}
