// TODO: finish jdocs
package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.exceptions.GlobalExceptionHandler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

//________________________________________________________________________________________________________________________________________

/**
 * Thread that actually does the logging.
 * It waits for logs to be put in the queue and prints them.
 * If there are no logs, it will simply wait.
*/
public class LogWorker extends Thread {

    private LogLevel min_level;
    private LinkedBlockingQueue<Log> logs;
    private HashMap<String, BufferedWriter> file_writers;

    //____________________________________________________________________________________________________________________________________

    public LogWorker(LogLevel min_level, LinkedBlockingQueue<Log> logs, HashMap<String, BufferedWriter> file_writers) {

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
        
        this.min_level = min_level;
        this.logs = logs;
        this.file_writers = file_writers;
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Spins indefinetly and prints logs as long as there are any in the queue.
     * If the queue is empty, it simply waits.
     * If the thread is interrupted, it will print that to the console and terminate.
    */
    @Override
    public void run() {

        Log log;

        while(true) {

            try {

                log = logs.take();
                printLog(log.message(), log.log_level(), log.file_path());
            }

            catch(InterruptedException exc) {

                LogPrinter.printToConsole(Thread.currentThread().getName() + " stopped", LogLevel.INFO);
                break;
            }
        }
    }

    //____________________________________________________________________________________________________________________________________

    private void printLog(String message, LogLevel log_level, String file_path) {

        BufferedWriter file_writer;
        
        if(min_level.compareTo(log_level) <= 0) {

            if(file_path == null) {

                LogPrinter.printToConsole(message, log_level);
            }

            else {
                
                try {

                    file_writer = file_writers.get(file_path);

                    if(file_writer == null) {

                        file_writer = new BufferedWriter(new FileWriter(file_path, true));
                        file_writers.put(file_path, file_writer);
                    }

                    LogPrinter.printToFile(message, log_level, file_writer);
                }

                catch(IOException exc) {

                    LogPrinter.printToConsole("Thread: " + Thread.currentThread().getName() + " Id: " + Thread.currentThread().threadId() + " threw " + exc.getClass().getName() + " message: " + exc.getMessage(), log_level);
                }
            }
        }
    }

    //____________________________________________________________________________________________________________________________________
}