package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.config.ConfigurationProvider;
import io.github.clamentos.blackhole.exceptions.GlobalExceptionHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

//________________________________________________________________________________________________________________________________________

/**
 * Worker thread that actually does the logging.
*/
public class LogWorker extends Thread {

    private AtomicBoolean stopped;
    private BlockingQueue<Log> log_queue;
    private long current_log_file_size;
    private BufferedWriter current_file_writer;

    //____________________________________________________________________________________________________________________________________

    /**
     * Instantiates a new log worker on the given log queue.
     * @param log_queue : The log queue on which the thread will consume and log.
     */
    public LogWorker(BlockingQueue<Log> log_queue) {

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
        findEligible();
        this.log_queue = log_queue;
        stopped = new AtomicBoolean(true);
    }

    //____________________________________________________________________________________________________________________________________

    @Override
    public void run() {

        Log log;
        stopped.set(false);

        while(stopped.get() == false) {

            try {

                log = log_queue.take();
                printHelper(log.message(), log.log_level());
            }

            catch(InterruptedException exc) {

                if(stopped.get() == true) {

                    break;
                }

                printHelper("Interrupted while waiting on the queue, InterruptedException: " + exc.getMessage(), LogLevel.INFO);
            }
        }
    }

    /**
     * Sets the stop flag of the thread.
     * This method does not guarantee that the thread will stop as it may still be blocked on the log queue.
     * After calling {@code halt()} the thread must also be interrupted
     * by calling the {@code interrupt()} method. This will cause the {@link LogWorker} to check the stop flag.
    */
    public void halt() {

        stopped.set(true);
    }

    //____________________________________________________________________________________________________________________________________

    private void findEligible() {

        try {

            File[] files = new File("logs/").listFiles();
            long last_modified = 0;
            int found = 0;
            
            for(int i = 0; i < files.length; i++) {

                if(files[i].lastModified() > last_modified) {

                    last_modified = files[i].lastModified();
                    found = i;
                }
            }

            if(found > 0) {

                if(files[found].length() < ConfigurationProvider.MAX_LOG_FILE_SIZE) {

                    current_log_file_size = files[found].length();
                    current_file_writer = new BufferedWriter(new FileWriter(files[found]));

                    return;
                }
            }

            createNewLogFile();
        }

        catch(IOException exc) {

            LogPrinter.printToConsole("Could not access file, IOException: " + exc.getMessage(), LogLevel.ERROR);
        }
    }

    private void createNewLogFile() {

        String name;

        try {

            name = "logs/log" + System.currentTimeMillis() + ".log";

            if(current_file_writer != null) {

                current_file_writer.close();
            }

            current_file_writer = new BufferedWriter(new FileWriter(name));
            current_log_file_size = 0;
        }

        catch(IOException exc) {

            LogPrinter.printToConsole("Could not access file, IOException: " + exc.getMessage(), LogLevel.ERROR);
        }
    }

    private void printHelper(String message, LogLevel log_level) {

        if(log_level.getToFile() == true) {

            if(current_log_file_size >= ConfigurationProvider.MAX_LOG_FILE_SIZE) {

                createNewLogFile();
            }

            current_log_file_size += LogPrinter.printToFile(message, log_level, current_file_writer);
        }

        else {

            LogPrinter.printToConsole(message, log_level);
        }
    }

    //____________________________________________________________________________________________________________________________________
}