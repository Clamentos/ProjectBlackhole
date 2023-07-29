package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.concurrent.atomic.AtomicLong;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>STEREOTYPE: Eager-loaded singleton.</b></p>
 * <p>Log printer service.</p>
 * <p>Printing class that holds the actual log printing methods.</p>
 * <p>The logs will have the following format:</p>
 * {@code [ERROR]-[20/10/2023 14:10:34.123]-[1234567890]-[...]}
 * <ol>
 *     <li>{@code [ERROR]}: The log level.</li>
 *     <li>{@code [20/10/2023]}: The log event creation date and time.</li>
 *     <li>{@code [1234567890]}: The unique log id.</li>
 *     <li>{@code [...]}: The actual log message.</li>
 * </ol>
*/
public final class LogPrinter {

    private static final LogPrinter INSTANCE = new LogPrinter();

    private ConfigurationProvider configuration_provider;

    private AtomicLong current_id;
    private BufferedWriter file_writer;
    private long file_size;

    //____________________________________________________________________________________________________________________________________

    private LogPrinter() {

        configuration_provider = ConfigurationProvider.getInstance();
        current_id = new AtomicLong(0);

        findEligible();
        printLog(new Log("LogPrinter.new > Instantiated successfully", LogLevel.SUCCESS, getNextId()));
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * @return The {@link ConfigurationProvider} instance created during class loading.
    */
    public static LogPrinter getInstance() {

        return(INSTANCE);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is partially thread safe.</b></p>
     * <p>(Thread safe on a line-per-line basys. Interleaved lines are possible).</p>
     * Synchronously log the given message with the specified severity to the console or file
     * depending on the configuration (see {@link ConfigurationProvider}).
     * @param message : The message to log.
     * @param severity : The severity of the log event.
     * @throws IllegalArgumentException If {@code severity} is {@code null}.
    */
    public void log(String message, LogLevel severity) throws IllegalArgumentException {

        if(severity == null) {

            throw new IllegalArgumentException();
        }

        if(severity.ordinal() >= configuration_provider.MIN_LOG_LEVEL) {

            printLog(new Log(message, severity));
        }
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * @return The next unique log id. Overflows will simply wrap around without throwing any exception.
    */
    protected long getNextId() {

        return(current_id.getAndIncrement());
    }

    /**
     * <p><b>This method is partially thread safe.</b></p>
     * <p>(Thread safe on a line-per-line basys. Interleaved lines are possible).</p>
     * Synchronously logs the given message with the specified severity to the console or file
     * depending on the configuration (see {@link ConfigurationProvider}).
     * @param log : The log to log.
    */
    protected void printLog(Log log) {

        if(log.log_level().toFile() == true) {

            printToFile(log);
        }

        else {

            printToConsole(log);
        }
    }

    //____________________________________________________________________________________________________________________________________

    // Formats and prints to the console.
    private void printToConsole(Log log) {

        String level;
        String partial;
        String message;

        level = "[" + log.log_level().getColor() + log.log_level().getValue() + "\u001B[0m]-";
        message = "[" + log.log_level().getColor() + log.message() + "\u001B[0m]";
        partial = partialString(log);

        System.out.println(level + partial + message);
    }

    // Formats and prints to the current log file.
    private void printToFile(Log log) {

        String level;
        String partial;
        String message;

        level = "[" + log.log_level().getValue() + "]-";
        partial = partialString(log);
        message = "[" + log.message() + "]\n";

        try {

            write(level + partial + message);
        }

        catch(IOException exc) {

            printToConsole(new Log(
                
                "LogPrinter.printToFile > Could not write to log file, IOException: " +
                exc.getMessage() + " Writing to console instead",
                LogLevel.WARNING
            ));

            printToConsole(log);
        }
    }

    // Prepares the partial log message.
    private String partialString(Log log) {

        SimpleDateFormat formatter;

        String timestamp;
        String id;

        formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");

        timestamp = "[" + formatter.format(log.timestamp()) + "]-";
        id = "[" + String.format("%016X", log.id()) + "]-";

        return(timestamp + id);
    }

    // TODO: maybe sync all?
    // Writes the data to the currently selected log file.
    private void write(String data) throws IOException {

        if(file_size >= configuration_provider.MAX_LOG_FILE_SIZE) {

            findEligible();
        }

        file_writer.write(data);
        file_writer.flush();
        file_size += data.length();
    }

    // TODO: check if sync all is needed
    // Finds the most "recent" log file. If there are none, create one.
    private synchronized void findEligible() {

        File[] files;
        long last_modified = 0;
        int found = 0;

        try {

            files = new File("logs/").listFiles();

            for(int i = 0; i < files.length; i++) {

                if(files[i].lastModified() > last_modified) {

                    last_modified = files[i].lastModified();
                    found = i + 1;
                }
            }

            if((found > 0) && (files[found - 1].length() < configuration_provider.MAX_LOG_FILE_SIZE)) {

                file_size = files[found - 1].length();
                file_writer = new BufferedWriter(new FileWriter(files[found - 1], true));

                return;
            }

            createNewLogFile();
        }

        catch(IOException exc) {

            printToConsole(new Log(

                "LogFile.findEligible > Could not access file, IOException: " + exc.getMessage(),
                LogLevel.ERROR
            ));
        }
    }

    // Creates a new log file with "log_<timestamp>.log" name.
    private void createNewLogFile() {

        String name;

        try {

            name = "logs/log_" + System.currentTimeMillis() + ".log";

            if(file_writer != null) {

                file_writer.close();
            }

            file_writer = new BufferedWriter(new FileWriter(name));
            file_size = 0;
        }

        catch(IOException exc) {

            printToConsole(new Log(

                "LogFile.createNewLogFile > Could not access file, IOException: " + exc.getMessage(),
                LogLevel.ERROR
            ));
        }
    }

    //____________________________________________________________________________________________________________________________________
}