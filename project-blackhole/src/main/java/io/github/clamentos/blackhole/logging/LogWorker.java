package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.config.ConfigurationProvider;
import io.github.clamentos.blackhole.common.framework.Worker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.concurrent.BlockingQueue;

//________________________________________________________________________________________________________________________________________

/**
 * Worker thread that actually does the logging.
*/
public class LogWorker extends Worker<Log> {
    
    private long current_log_file_size;
    private BufferedWriter current_file_writer;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Instantiates a new log worker on the given log queue.
     * @param identifier : The log worker identifier.
     * @param logs_queue : The log queue on which the thread will consume and log.
    */
    public LogWorker(int identifier, BlockingQueue<Log> logs_queue) {

        super(identifier, logs_queue);
        findEligible();
        LogPrinter.printToConsole("Log worker started", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Method that prints the aquired log.
     * @param log : The log to log.
    */
    @Override
    public void doWork(Log log) {

        if(log.log_level().getToFile() == true) {

            if(current_log_file_size >= ConfigurationProvider.MAX_LOG_FILE_SIZE) {

                createNewLogFile();
            }

            current_log_file_size += LogPrinter.printToFile(log.message(), log.log_level(), current_file_writer);
        }

        else {

            LogPrinter.printToConsole(log.message(), log.log_level());
        }
    }

    /**
     * {@inheritDoc}
    */
    @Override
    public void catchInterrupted(InterruptedException exc) {

        LogPrinter.printToConsole("LogWorker.doWork > Interrupted while waiting on queue, InterruptedException: " + exc.getMessage(), LogLevel.NOTE);
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
                    found = i + 1;
                }
            }

            if((found > 0) && (files[found - 1].length() < ConfigurationProvider.MAX_LOG_FILE_SIZE)) {

                current_log_file_size = files[found - 1].length();
                current_file_writer = new BufferedWriter(new FileWriter(files[found - 1], true));

                return;
            }

            createNewLogFile();
        }

        catch(IOException exc) {

            LogPrinter.printToConsole("LogWorker.findEligible > Could not access file, IOException: " + exc.getMessage(), LogLevel.ERROR);
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

            LogPrinter.printToConsole("LogWorker.createNewLogFile > Could not access file, IOException: " + exc.getMessage(), LogLevel.ERROR);
        }
    }

    //____________________________________________________________________________________________________________________________________
}
