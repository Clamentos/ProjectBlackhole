package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import java.io.BufferedWriter;
import java.io.IOException;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

//________________________________________________________________________________________________________________________________________

/**
 * Main logging class.
 * This is responsible for managing the logger thread and inserting logs into the queue.
*/
public class Logger {

    private LogLevel min_level;
    private int offer_timeout;
    private LogWorker log_worker;
    private LinkedBlockingQueue<Log> logs;
    private HashMap<String, BufferedWriter> file_writers;

    //____________________________________________________________________________________________________________________________________
    
    /**
     * Basic constructor.
     * @param min_level : Minimum logging level.
     *     If a log has a level below {@code min_level}, it will be ignored.
     *     If {@code min_level} is null, it will default to {@link LogLevel#INFO}
     * @param offer_timeout : Maximum time (in milliseconds) to wait before leaving while inserting a log into the queue.
     * @param max_queue_capacity : Maximum number of elements that the queue can hold.
    */
    public Logger(LogLevel min_level, int offer_timeout, int max_queue_capacity) {

        this.offer_timeout = offer_timeout;
        logs = new LinkedBlockingQueue<>(max_queue_capacity);
        file_writers = new HashMap<>();
        
        if(min_level == null) this.min_level = LogLevel.INFO;
        else this.min_level = min_level;

        log_worker = new LogWorker(this.min_level, logs, file_writers);
        log_worker.setName("Log Worker 0");
        log_worker.start();
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * @returns The current minimum log level
    */
    public LogLevel getMinLevel() {

        return(min_level);
    }

    /**
     * Stops the log worker thread and closes any open log file.
     * This method is intended to be called once when the application is being stopped.
     * @param wait : Wait for the log worker to empty the logging queue before stopping it.
     * @throws IOException if any I/O error occur while closing the log files.
     * @throws InterruptedException if this thread is interrupted while waiting.
    */
    public void stop(boolean wait) throws IOException, InterruptedException {

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
    }

    /**
     * Adds the message to the log queue.
     * If there is no space in the queue, it will block the thread up to a fixed amount of time.
     * @param message : The message to be logged.
     * @param log_level : The severity of the message.
     * @param file_path : The destination file.
     * @throws InterruptedException if this thread is interrupted while waiting.
     * @returns false if the insertion failed, true otherwise.
     */
    public boolean log(String message, LogLevel log_level, String file_path) throws InterruptedException {

        return(logs.offer(new Log(message, log_level, file_path), offer_timeout, TimeUnit.MILLISECONDS));
    }

    /**
     * Adds the message to the log queue.
     * If there is no space in the queue, it will block the thread up to a fixed amount of time.
     * @param message : The message to be logged.
     * @param log_level : The severity of the message.
     * @throws InterruptedException if this thread is interrupted while waiting.
     * @returns false if the insertion failed, true otherwise.
     */
    public boolean log(String message, LogLevel log_level) throws InterruptedException {

        return(logs.offer(new Log(message, log_level, null), offer_timeout, TimeUnit.MILLISECONDS));
    }

    //____________________________________________________________________________________________________________________________________
}
