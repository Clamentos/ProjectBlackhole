// TODO: finish jdocs + exception refactor a bit
package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import java.io.BufferedWriter;
import java.io.IOException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

//________________________________________________________________________________________________________________________________________

/**
 * Thread that actually does the logging.
 * It waits for logs to be put in the queue and prints them.
 * If there are no logs, it will simply wait.
*/
public class LogWorker extends Thread {

    private final String RESET_COLOR = "\u001B[0m";
    
    private DateTimeFormatter date_formatter;
    private LogLevel min_level;
    private LinkedBlockingQueue<Log> logs;
    private HashMap<String, BufferedWriter> file_writers;

    //____________________________________________________________________________________________________________________________________

    public LogWorker(LogLevel min_level, LinkedBlockingQueue<Log> logs, HashMap<String, BufferedWriter> file_writers) {

        date_formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        this.min_level = min_level;
        this.logs = logs;
        this.file_writers = file_writers;
    }

    //____________________________________________________________________________________________________________________________________

    @Override
    public void run() {

        Log log;

        while(true) {

            try {

                log = logs.take();
                printLog(log.message(), log.log_level(), log.file_path());
            }

            catch(InterruptedException exc) {

                printLog("LogWorker stopped", LogLevel.INFO, null);
                break;
            }
        }
    }

    //____________________________________________________________________________________________________________________________________

    private void printLog(String message, LogLevel log_level, String file_path) {

        if(min_level.compareTo(log_level) <= 0) {

            if(file_path == null) {

                printToConsole(message, log_level);
            }
    
            else {
    
                printToFile(message, log_level, file_path);
            }
        }
    }

    // if paths array is empty -> map will be empty -> null exception when getting...
    private void printToFile(String message, LogLevel log_level, String file_path) {

        BufferedWriter file_writer;
        String prefix = "[ " + LocalDateTime.now().format(date_formatter) + " ] -------- [ ";
        String level = log_level.getValue();

        try {
    
            file_writer = file_writers.get(file_path);
            file_writer.write(prefix + level + " ] -------- : " + message + "\n");
            file_writer.flush();
        }

        catch(IOException exc) {

            printToConsole("IOException: " + exc.getMessage() + "\n\nStack trace:\n" + exc.getStackTrace(), LogLevel.WARNING);
        }
    }

    private void printToConsole(String message, LogLevel log_level) {

        String prefix = "[ " + LocalDateTime.now().format(date_formatter) + " ] -------- [ ";
        String color = log_level.getColor();
        String level = log_level.getValue();

        System.out.println(prefix + color + level + RESET_COLOR + " ] -------- : " + color + message + RESET_COLOR);
    }

    //____________________________________________________________________________________________________________________________________
}