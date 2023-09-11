package io.github.clamentos.blackhole.logging;

///
import io.github.clamentos.blackhole.configuration.ConfigurationProvider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.concurrent.atomic.AtomicLong;

///
/**
 * <h3>Log printer service</h3>
 * 
 * Printing class that holds the actual log printing methods.
 * The methods, since they print directly to the destination, are all synchronous.
 * 
 * <p>The logs will have the following format:</p>
 * {@code [ERROR]-[20/10/2023 14:10:34.123]-[1234567890]-[...]}
 * <ol>
 *     <li>{@code [ERROR]}: The log level.</li>
 *     <li>{@code [20/10/2023 14:34:22.019]}: The log event creation date and time.</li>
 *     <li>{@code [1234567890]}: The unique log id.</li>
 *     <li>{@code [...]}: The actual log message.</li>
 * </ol>
 * 
 * @see {@link Logger}
 * @apiNote This class is an <b>eager-loaded singleton.</b>
*/
public final class LogPrinter {

    ///
    private static final LogPrinter INSTANCE = new LogPrinter();

    private final int MIN_LOG_LEVEL;
    private final int MAX_LOG_FILE_SIZE;

    private AtomicLong current_id;
    private BufferedWriter file_writer;
    private long file_size;

    ///
    /*
     * This method initializes the singleton and finds an "eligible" log file (most recent & size < limit)
     * in the {@code [classpath]/logs} path. If no file is eligible (or the directory is empty), it will
     * create a new one.
    */
    private LogPrinter() {

        MIN_LOG_LEVEL = ConfigurationProvider.getInstance().MIN_LOG_LEVEL;
        MAX_LOG_FILE_SIZE = ConfigurationProvider.getInstance().MAX_LOG_FILE_SIZE;

        current_id = new AtomicLong(0);

        findEligible();
        printLog(new Log("LogPrinter.new > Instantiated successfully", LogLevel.SUCCESS, getNextId()));
    }

    ///
    /** @return The {@link ConfigurationProvider} instance created during class loading. */
    public static LogPrinter getInstance() {

        return(INSTANCE);
    }

    ///
    /**
     * Synchronously log the given message with the specified severity to the console or file depending on
     * the configuration.
     * @param message : The message to log.
     * @param severity : The severity of the log event.
     * @throws IllegalArgumentException If {@code severity} is {@code null}.
     * @see {@link ConfigurationProvider}
     * @see {@link LogLevel}
    */
    public void log(String message, LogLevel severity) throws IllegalArgumentException {

        if(severity == null) {

            throw new IllegalArgumentException("Log level cannot be null");
        }

        if(severity.ordinal() >= MIN_LOG_LEVEL) {

            printLog(new Log(message, severity, getNextId()));
        }
    }

    ///
    /** @return The next unique log id. Overflows will simply silently wrap around. */
    protected long getNextId() {

        return(current_id.getAndIncrement());
    }

    /**
     * Synchronously logs the given message with the specified severity to the console or file
     * depending on the configuration.
     * @param log : The log to log.
     * @see {@link ConfigurationProvider}
    */
    protected void printLog(Log log) {

        if(log.log_level().toFile() == true) {

            printToFile(log);
        }

        else {

            printToConsole(log);
        }
    }

    ///
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
                LogLevel.WARNING,
                getNextId()
            ));

            printToConsole(log);
        }
    }

    // Prepares the partial log message.
    private String partialString(Log log) {

        SimpleDateFormat formatter;
        String id;

        formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        id = "[" + String.format("%016X", log.id()) + "]-";

        return("[" + formatter.format(log.timestamp()) + "]-" + id);
    }

    // Writes the data to the currently selected log file.
    private void write(String data) throws IOException {

        if(file_size >= MAX_LOG_FILE_SIZE) {

            findEligible();
        }

        file_writer.write(data);
        file_writer.flush();
        file_size += data.length();
    }

    // TODO: check if finer grained locks can be done
    // Finds the most "recent" log file below the size limit. If there are none, create one.
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

            if((found > 0) && (files[found - 1].length() < MAX_LOG_FILE_SIZE)) {

                file_size = files[found - 1].length();
                file_writer = new BufferedWriter(new FileWriter(files[found - 1], true));

                return;
            }

            if(file_writer != null) {

                file_writer.close();
            }

            file_size = 0;
            file_writer = new BufferedWriter(new FileWriter("logs/log_" + System.currentTimeMillis() + ".log"));
        }

        catch(IOException exc) {

            printToConsole(new Log(

                "LogFile.findEligible > Could not access file, IOException: " + exc.getMessage(),
                LogLevel.ERROR,
                getNextId()
            ));
        }
    }

    ///
}
