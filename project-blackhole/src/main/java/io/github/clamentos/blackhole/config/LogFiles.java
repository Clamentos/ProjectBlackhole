package io.github.clamentos.blackhole.config;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.LogPrinter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

//________________________________________________________________________________________________________________________________________

/**
 * Enumeration listing all the available log files.
 * Any thread must use this to specify where to log the log.
 * If this class fails to open / create the file, it will insert a null.
*/
public enum LogFiles {

    TEST(LogLevel.INFO, create("test.log")),
    DEBUG(LogLevel.DEBUG, create("debug.log"));

    //____________________________________________________________________________________________________________________________________

    private LogLevel log_level;
    private BufferedWriter file_writer;

    //____________________________________________________________________________________________________________________________________

    private LogFiles(LogLevel log_level, BufferedWriter file_writer) {

        this.log_level = log_level;
        this.file_writer = file_writer;
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Get the associated log level.
     * @return the log level (always well-defined).
     */
    public LogLevel getLogLevel() {

        return(log_level);
    }

    /**
     * Get the associated file writer for the log file.
     * @return the file writer.
     */
    public BufferedWriter getFileWriter() {

        return(file_writer);
    }

    //____________________________________________________________________________________________________________________________________

    private static BufferedWriter create(String file_name) {

        try {

            return(new BufferedWriter(new FileWriter(file_name)));
        }

        catch(IOException exc) {

            LogPrinter.printToConsole("Failed to load the target log file, returning null. Message: " + exc.getMessage(), LogLevel.ERROR);
            return(null);
        }
    }

    //____________________________________________________________________________________________________________________________________
}
