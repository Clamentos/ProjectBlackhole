package io.github.clamentos.blackhole.framework.implementation.logging;

///
import io.github.clamentos.blackhole.framework.implementation.configuration.ConfigurationProvider;

///..
import io.github.clamentos.blackhole.framework.implementation.logging.exportable.LogLevels;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.ResourceReleaserInternal;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.exportable.ExceptionFormatter;

///.
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

///..
import java.nio.file.Files;
import java.nio.file.Paths;

///..
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

///..
import java.time.format.DateTimeFormatter;

///..
import java.util.ArrayList;
import java.util.List;

///..
import java.util.concurrent.atomic.AtomicLong;

///..
import java.util.concurrent.locks.ReentrantLock;

///
/**
 * <h3>Log Printer</h3>
 * This class performs the actual logging to the console or log file.
 * @apiNote Use this class when synchronous logging is desired.
*/
public final class LogPrinter {

    ///
    /** Singleton instance of {@code this} instantiated during class loading. */
    private static final LogPrinter INSTANCE = new LogPrinter();

    ///.
    /** Constant dictating the buffer size of the file writer in bytes. */
    private final int WRITER_BUFFER_SIZE;

    ///..
    /** The log timestamp formatter with pattern: {@code dd/MM/yyyy HH:mm:ss.SSS}. */
    private final DateTimeFormatter formatter;

    /** The atomic counter used to generate the runtime unique log identifiers. */
    private final AtomicLong current_id;

    /** The synchronization primitive used to enforce mutual exclusion during log file resteering. */
    private final ReentrantLock lock;

    ///..
    /** The path to the log file currently in use. */
    private volatile String path;

    /** The file writer used to write to the log file currently in use. */
    private volatile BufferedWriter writer;

    ///
    /**
     * <p>Instantiates a new {@code LogPrinter} object.</p>
     * This constructor also creates a new log file if it's not already present.
     * @apiNote Since this class is a singleton, this constructor will only be called once.
    */
    private LogPrinter() {

        WRITER_BUFFER_SIZE = ConfigurationProvider.getInstance().READER_WRITER_BUFFER_SIZE;
        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS");
        current_id = new AtomicLong(0);
        lock = new ReentrantLock();

        try {

            path = "resources/" + findLogFile(true);
            writer = new BufferedWriter(new FileWriter(path, true), WRITER_BUFFER_SIZE);
        }

        catch(IOException exc) {

            if(exc instanceof FileNotFoundException) {

                path = "resources/logs_" + System.currentTimeMillis() + ".log";

                try {

                    Files.createFile(Paths.get(path));
                    writer = new BufferedWriter(new FileWriter(path, true), WRITER_BUFFER_SIZE);
                }

                catch(IOException exc2) {

                    die(exc2);
                }
            }

            else {

                die(exc);
            }
        }

        printToFile(new Log("ApplicationStarter.start => Application starting...", LogLevels.SUCCESS, getNextId()));
        printToFile(new Log("LogPrinter.new => Instantiated successfully", LogLevels.SUCCESS, getNextId()));
    }

    ///
    /** @return The {@link LogPrinter} instance created during class loading. */
    public static LogPrinter getInstance() {

        return(INSTANCE);
    }

    ///
    /**
     * Synchronously logs the message to the log file with the given severity.
     * @param message : The message to log.
     * @param severity : The severity of the log.
     * @throws NullPointerException If {@code log} or any of its parameters are {@code null}.
     * @apiNote If this methods couldn't write to the file, it will use the console as a fallback.
    */
    public void logToFile(String message, LogLevels severity) throws NullPointerException {

        printToFile(new Log(message, severity, getNextId()));
    }

    ///..
    /**
     * Releases all the file descriptors held by {@code this} class.
     * @apiNote This method should only be used on application shutdown since {@code this} cannot be re-opened.
    */
    public synchronized void close() {

        ResourceReleaserInternal.release(this, "LogPrinter", "close", writer);
    }

    ///.
    /**
     * Logs the log to the log file with the given severity synchronously.
     * @param log : The log to log.
     * @throws NullPointerException If {@code log} or any of its parameters are {@code null}.
     * @apiNote If this method couldn't write to the file, it will use the console as a fallback.
    */
    protected void printToFile(Log log) throws NullPointerException {

        String level = "[" + log.log_level().getValue() + "]-";
        String message = "[" + log.message() + "]\n";

        try {

            write(level + partialString(log) + message);
        }

        catch(IOException exc) {

            printToConsole(new Log(

                ExceptionFormatter.format(

                    "LogPrinter.printToFile => Could not write to the log file", exc, "Writing to the console instead..."
                ),

                LogLevels.WARNING,
                getNextId()
            ));

            printToConsole(log);
        }
    }

    ///..
    /** @return The next runtime-unique log id. */
    protected long getNextId() {

        return(current_id.getAndIncrement());
    }

    ///..
    /**
     * <p>Creates a new empty log file and modifies the current writer to point to it.</p>
     * The newly created file will have the following name: {@code "logs_" + System.currentTimeMillis() + ".log"}
     * @return The path to the previous log file.
     * @throws IOException If any IO error occurs.
    */
    protected String createNewLogFile() throws IOException {

        lock.lock();

        String old_path = path;
        String new_path = "resources/logs_" + System.currentTimeMillis() + ".log";
        Files.createFile(Paths.get(new_path));

        try {

            ResourceReleaserInternal.release(this, "LogPrinter", "createNewLogFile", writer);
            writer = new BufferedWriter(new FileWriter(new_path, true), WRITER_BUFFER_SIZE);
        }

        catch(IOException exc) {

            lock.unlock();
            throw exc;
        }

        path = new_path;
        lock.unlock();

        return(old_path);
    }

    ///..
    /**
     * Scans the {@code resource/} directory and returns the name of the oldest {@code *.log} file.
     * @return The latest or oldest log file according to the parameter.
     * @throws FileNotFoundException If no file matching the name was found.
    */
    protected String findOldestLogFile() throws FileNotFoundException {

        return(findLogFile(false));
    }

    ///..
    /**
     * Logs the log to the console with the given severity synchronously.
     * @param log : The log to log.
     * @throws NullPointerException If {@code log} or any of its parameters are {@code null}.
    */
    private void printToConsole(Log log) throws NullPointerException {

        String level = "[" + log.log_level().getColor() + log.log_level().getValue() + "\u001B[0m]-";
        String message = "[" + log.log_level().getColor() + log.message() + "\u001B[0m]";

        System.out.println(level + partialString(log) + message);
    }

    ///..
    /**
     * Constructs a partial log string.
     * @param log : The log used to build the string.
     * @return The partial log string containing the log timestamp and id.
    */
    private String partialString(Log log) {

        String id = "[" + String.format("%016X", log.id()) + "]-";
        return("[" + formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(log.timestamp()), ZoneId.systemDefault())) + "]-" + id);
    }

    ///..
    /**
     * Writes to the currently open file {@code writer}.
     * @param data : The string to write.
     * @throws IOException If any IO error occurs.
    */
    private void write(String data) throws IOException {

        lock.lock();
        writer.write(data);

        if(ConfigurationProvider.getInstance().FLUSH_AFTER_WRITE == true) {

            writer.flush();
        }

        lock.unlock();
    }

    ///..
    /**
     * Scans the {@code resource/} directory and returns the name of the latest or oldest {@code *.log} file.
     * @param latest_mode : Search mode. {@code true} for latest, {@code false} for oldest.
     * @return The name of the latest or oldest {@code *.log} file.
     * @throws FileNotFoundException If no eligible file was found.
    */
    private String findLogFile(boolean latest_mode) throws FileNotFoundException {

        String latest;
        List<String> log_names = new ArrayList<>();
        String[] all_names = new File("resources/").list();

        for(String name : all_names) {

            if(name.endsWith(".log")) {

                log_names.add(name);
            }
        }

        if(log_names.size() > 0) {

            latest = log_names.get(0);

            for(String name : log_names) {

                if(latest_mode == true) {

                    if(name.compareTo(latest) > 0) {

                        latest = name;
                    }
                }
                
                else {

                    if(name.compareTo(latest) < 0) {

                        latest = name;
                    }
                }
            }

            return(latest);
        }

        else {

            throw new FileNotFoundException("LogPrinter.findLogFile -> No eligible log file was found");
        }
    }

    ///..
    /**
     * Forcefully terminate the application due to an unrecoverable exception while instantiating {@code this}.
     * @param exc : The exception.
    */
    private void die(Exception exc) {

        printToFile(new Log(
   
            ExceptionFormatter.format("LogPrinter.new => Could not instantiate", exc, "Aborting..."),
            LogLevels.FATAL,
            getNextId()
        ));

        close();
        System.exit(1);
    }

    ///
}
