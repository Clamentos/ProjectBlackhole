package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.ConfigurationProvider;

import java.io.BufferedWriter;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

//________________________________________________________________________________________________________________________________________

/**
 * Main logging class responsible for managing the logger thread and inserting logs into the queue.
 * This class is a singleton.
*/
public class Logger {

    private static volatile Logger INSTANCE;
    private static Object dummy_mutex = new Object();
    
    private int offer_timeout;
    private LogLevel min_level;
    private LogWorker log_worker;
    private LinkedBlockingQueue<Log> logs;
    private HashMap<String, BufferedWriter> file_writers;

    //____________________________________________________________________________________________________________________________________
    
    /**
     * Constructs a new Logger.
     * @param offer_timeout : Maximum queue insertion timeout (in milliseconds).
     * @param min_level : Minimum logging level.
     *     If {@code min_level} is null, it will default to {@link LogLevel#INFO}
     * @param max_queue_capacity : Maximum number of elements that the queue can hold.
    */
    private Logger(int offer_timeout, LogLevel min_level, int max_queue_capacity) {

        logs = new LinkedBlockingQueue<>(max_queue_capacity);
        file_writers = new HashMap<>();
        
        if(min_level == null) this.min_level = LogLevel.INFO;
        else this.min_level = min_level;

        this.offer_timeout = offer_timeout;
        log_worker = new LogWorker(this.min_level, logs, file_writers);
        log_worker.setName("Log Worker 0");
        log_worker.start();
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Get the Logger instance.
     * @return The Logger instance.
     */
    public static Logger getInstance() {

        Logger temp = INSTANCE;

        if(temp == null) {

            synchronized(dummy_mutex) {

                temp = INSTANCE;

                if(temp == null) {

                    temp = new Logger(
            
                        ConfigurationProvider.OFFER_TIMEOUT,
                        ConfigurationProvider.MINIMUM_LOG_LEVEL,
                        ConfigurationProvider.MAX_LOG_QUEUE_SIZE
                    );
                }
            }
        }

        return(temp);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Get the current minimum log level.
     * @returns The current minimum log level.
    */
    public LogLevel getMinLevel() {

        return(min_level);
    }

    /*
     * Stops the log worker thread and closes any open log file.
     * This method is intended to be called once when the application is being stopped.
     * @param wait : Wait for the log worker to empty the logging queue before stopping it.
     * @throws IOException if any I/O error occur while closing the log files.
     * @throws InterruptedException if this thread is interrupted while waiting.
    */
    /*public void stop(boolean wait) throws IOException, InterruptedException {

        if(wait == true) {

            while(true) {

                if(logs.size() == 0) {

                    log_worker.interrupt();
                    break;
                }
            }
        }

        else {

            log_worker.interrupt();
        }

        log_worker.join();

        for(String writer : file_writers.keySet()) {

            file_writers.get(writer).close();
        }
    }*/

    /**
     * Adds the message to the log queue.
     * If there is no space in the queue, it will block the thread up to a fixed amount of time.
     * @param message : The message to be logged.
     * @param log_level : The severity of the message.
     * @param file_path : The destination file.
     * @returns false if the insertion failed, true otherwise.
     */
    public boolean log(String message, LogLevel log_level, String file_path) throws InterruptedException {

        try {

            return(logs.offer(new Log(message, log_level, file_path), offer_timeout, TimeUnit.MILLISECONDS));
        }

        catch(InterruptedException exc) {

            LogPrinter.printToConsole("Thread: " + Thread.currentThread().getName() + " Id: " + Thread.currentThread().threadId() + " threw " + exc.getClass().getName() + " message: " + exc.getMessage(), log_level);

            return(false);
        }
    }

    /**
     * Adds the message to the log queue.
     * If there is no space in the queue, it will block the thread up to a fixed amount of time.
     * @param message : The message to be logged.
     * @param log_level : The severity of the message.
     * @returns false if the insertion failed, true otherwise.
     */
    public boolean log(String message, LogLevel log_level) {

        try {

            return(logs.offer(new Log(message, log_level, null), offer_timeout, TimeUnit.MILLISECONDS));
        }

        catch(InterruptedException exc) {

            LogPrinter.printToConsole("Thread: " + Thread.currentThread().getName() + " Id: " + Thread.currentThread().threadId() + " threw " + exc.getClass().getName() + " message: " + exc.getMessage(), log_level);

            return(false);
        }
    }

    //____________________________________________________________________________________________________________________________________
}
