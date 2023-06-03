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
 * Main logging class.
 * This is responsible for managing the logger thread and inserting logs into the queue.
*/
public class Logger {

    private LogLevel min_level;
    private LogWorker log_worker;
    private LinkedBlockingQueue<Log> logs;
    private HashMap<String, BufferedWriter> file_writers;

    //____________________________________________________________________________________________________________________________________
    
    /**
     * Basic constructor.
     * 
     * @param min_level : Minimum logging level.
     *     If a log has a level below {@code min_level}, it will be ignored.
     *     If it's null, it will default to {@link LogLevel#INFO}
    */
    public Logger(LogLevel min_level) {

        init(min_level);
    }

    /**
     * Complete constructor.
     * 
     * @param min_level : Minimum logging level.
     *     If a log has a level below {@code min_level}, it will be ignored.
     *     If it's null, it will default to {@link LogLevel#INFO}
     *
     * @param paths : Log files paths. The array must not be null.
     * @throws NullPointerException if {@code paths} is null
     * @throws IOException if the files at the specified paths cannot be opened.
    */
    public Logger(String[] paths, LogLevel min_level) throws NullPointerException, IOException {

        init(min_level);

        for(String path : paths) {

            addFilePath(path);
        }
    }

    //____________________________________________________________________________________________________________________________________

    /** @returns The current minimum log level */
    public LogLevel getMinLevel() {

        return(min_level);
    }

    /**
     * @param min_level : Minimum logging level.
     *     If a log has a level below {@code min_level}, it will be ignored.
     *     If it's null, it will default to {@link LogLevel#INFO}
    */
    public void setMinLevel(LogLevel min_level) {

        if(min_level == null) this.min_level = LogLevel.INFO;
        else this.min_level = min_level;
    }

    public void addFilePath(String path) throws IOException {

        file_writers.put(path, new BufferedWriter(new FileWriter(path, true)));
    }

    public void restart() throws IllegalThreadStateException {

        log_worker.start();
    }

    public void stop(boolean wait) throws IOException {

        if(wait == true) {

            while(true) {

                if(logs.size() == 0) {

                    log_worker.interrupt();
                    break;
                }
            }
        }

        else log_worker.interrupt();
    }

    public void log(String message, LogLevel log_level, String file_path) {

        logs.add(new Log(message, log_level, file_path));
    }

    public void log(String message, LogLevel log_level) {

        logs.add(new Log(message, log_level, null));
    }

    public void closeFiles() throws IOException {

        for(String writer : file_writers.keySet()) {

            file_writers.get(writer).close();
        }
    }

    //____________________________________________________________________________________________________________________________________

    private void init(LogLevel min_level) {

        logs = new LinkedBlockingQueue<>();
        file_writers = new HashMap<>();
        
        if(min_level == null) this.min_level = LogLevel.INFO;
        else this.min_level = min_level;

        log_worker = new LogWorker(this.min_level, logs, file_writers);
        log_worker.setName("Log Worker 0");
        log_worker.setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
        log_worker.start();
    }

    //____________________________________________________________________________________________________________________________________
}
