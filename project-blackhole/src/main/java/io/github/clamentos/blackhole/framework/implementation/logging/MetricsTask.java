package io.github.clamentos.blackhole.framework.implementation.logging;

///
import io.github.clamentos.blackhole.framework.implementation.configuration.ConfigurationProvider;

///..
import io.github.clamentos.blackhole.framework.implementation.logging.exportable.LogLevels;
import io.github.clamentos.blackhole.framework.implementation.logging.exportable.MetricsTracker;

///..
import io.github.clamentos.blackhole.framework.implementation.persistence.models.LogEntity;
import io.github.clamentos.blackhole.framework.implementation.persistence.models.SystemDiagnostics;
import io.github.clamentos.blackhole.framework.implementation.persistence.models.SystemEntities;

///..
import io.github.clamentos.blackhole.framework.implementation.persistence.pool.ConnectionPool;
import io.github.clamentos.blackhole.framework.implementation.persistence.pool.PooledConnection;

///..
import io.github.clamentos.blackhole.framework.implementation.persistence.query.Repository;

///..
import io.github.clamentos.blackhole.framework.implementation.tasks.ContinuousTask;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.ResourceReleaserInternal;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.exportable.ExceptionFormatter;

///..
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.PersistenceException;

///.
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

///..
import java.nio.file.Files;
import java.nio.file.Paths;

///..
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;

///..
import java.time.format.DateTimeFormatter;

///..
import java.util.ArrayList;
import java.util.List;

///
/**
 * <h3>Metrics Task</h3>
 * Periodically writes to the database a summary of various system statistics and logs.
*/
public final class MetricsTask extends ContinuousTask {

    ///
    /** The service used to log notable events. */
    private final Logger logger;

    /** The service used to perform log file resteering. */
    private final LogPrinter log_printer;

    /** The service used to aquire database connections. */
    private final ConnectionPool pool;

    /** The service used to perform database queries. */
    private Repository repository;

    ///..
    /** The log timestamp formatter with pattern: {@code dd/MM/yyyy HH:mm:ss.SSS}. */
    private final DateTimeFormatter formatter;

    ///..
    /** The split index used when parsing log strings (always holds 1 element to simulate a reference to a mutable int). */
    private final int[] split_idx;

    /** The counter used for scheduling sleeps. */
    private int sleep_samples;
    
    ///
    /** Instantiates a new {@link MetricsTask} object. */
    public MetricsTask() {

        super();

        logger = Logger.getInstance();
        log_printer = LogPrinter.getInstance();
        pool = ConnectionPool.getInstance();
        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS");
        split_idx = new int[]{0};

        logger.log("MetricsTask.new => Instantiated successfully", LogLevels.SUCCESS);
    }

    ///
    /** {@inheritDoc} */
    @Override
    public void initialize() {

        sleep_samples = 0;
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void work() {

        if(sleep_samples < ConfigurationProvider.getInstance().METRICS_TASK_SCHEDULING_NUM_CHUNKS) {

            try {

                Thread.sleep(ConfigurationProvider.getInstance().METRICS_TASK_SCHEDULING_CHUNK_SIZE);
                sleep_samples++;

                return;
            }

            catch(InterruptedException exc) {

                logger.log(ExceptionFormatter.format("MetricsTask.work => Could not sleep", exc, "Ignoring..."), LogLevels.NOTE);
            }
        }

        logger.log("MetricsTask.work => Begin", LogLevels.INFO);

        repository = Repository.getInstance(); // Initialize lazily to avoid circular dependencies.
        sleep_samples = 0;

        SystemDiagnostics snapshot = MetricsTracker.getInstance().sample();
        PooledConnection connection = pool.aquireConnection();

        if(snapshotToDb(snapshot, connection) == true) {

            logsToDb(connection);
        }

        pool.releaseConnection(connection);
        logger.log("MetricsTask.work => End", LogLevels.SUCCESS);
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void terminate() {

        logger.log("MetricsTask.terminate => Shut down successfull", LogLevels.SUCCESS);
    }

    ///
    /**
     * Saves the system diagnostics snapshot to the database.
     * @param system_diagnostics : The entity to persist.
     * @param connection : The connection to the database.
     * @return {@code true} if successfull, {@code false} otherwise.
    */
    private boolean snapshotToDb(SystemDiagnostics system_diagnostics, PooledConnection connection) {

        try {

            repository.insert(List.of(system_diagnostics), SystemEntities.SYSTEM_DIAGNOSTICS, connection, false);
            return(true);
        }

        catch(PersistenceException exc) {

            logger.log(

                ExceptionFormatter.format("MetricsTask.snapshotToDb => Could not insert the system diagnostics", exc, "Aborting..."),
                LogLevels.ERROR
            );

            return(false);
        }
    }

    ///..
    /**
     * Reads all the logs in the log file and write them to the database.
     * If the transaction was successfull, this method will "clean" the log file.
     * @param connection : The connection to the database.
    */
    private void logsToDb(PooledConnection connection) {

        String old_path = null;
        BufferedReader reader = null;
        List<LogEntity> log_entities = new ArrayList<>();
        int count = countLogFiles();

        try {

            if(count == 1) {

                old_path = log_printer.createNewLogFile();
            }

            else {

                if(count == 0) {

                    logger.log("MetricsTask.logsToDb => No log file(s) found. Aborting...", LogLevels.ERROR);
                    return;
                }

                old_path = log_printer.findOldestLogFile();
            }

            reader = new BufferedReader(new FileReader(old_path), ConfigurationProvider.getInstance().READER_WRITER_BUFFER_SIZE);

            while(reader.ready()) {

                log_entities.add(parseSingle(reader.readLine()));
            }

            ResourceReleaserInternal.release(logger, "MetricsTask", "logsToDb", reader);
        }

        catch(IOException exc) {

            logger.log(

                ExceptionFormatter.format("MetricsTask.logsToDb => Could not process the log file", exc, "Aborting..."),
                LogLevels.ERROR
            );

            ResourceReleaserInternal.release(logger, "MetricsTask", "logsToDb", reader);
            return;
        }

        try {

            repository.insert(log_entities, SystemEntities.LOG_ENTITY, connection, true);
        }

        catch(PersistenceException exc) {

            logger.log(

                ExceptionFormatter.format("MetricsTask.logsToDb => Could not insert the logs", exc, "Aborting..."),
                LogLevels.ERROR
            );

            return;
        }

        try {

            Files.delete(Paths.get(old_path));
        }

        catch(IOException exc) {

            logger.log(

                ExceptionFormatter.format("MetricsTask.logsToDb => Could not delete the old log file", exc, "Aborting..."),
                LogLevels.ERROR
            );
        }
    }

    ///..
    /** @return The number of {@code *.log} named files in the {@code resources/} directory. */
    private int countLogFiles() {

        String[] all_names = new File("resources/").list();
        int count = 0;

        for(String name : all_names) {

            if(name.endsWith(".log")) {

                count++;
            }
        }

        return(count);
    }

    ///..
    /**
     * Parses a single log from the given string and constructs a new log entity object.
     * @param line : The log line to parse.
     * @return A new log entity that contains the parsed log.
    */
    private LogEntity parseSingle(String line) {

        split_idx[0] = 0;

        String log_level = split(line, split_idx);
        String date_time = split(line, split_idx);
        String log_id = split(line, split_idx);
        String message = split(line, split_idx);

        try {

            long millis = LocalDateTime.from(formatter.parse(date_time)).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            return(new LogEntity(0L, Long.parseLong(log_id, 16), millis, log_level, message));
        }

        catch(NumberFormatException | ArithmeticException | DateTimeException exc) {

            logger.log(

                ExceptionFormatter.format("MetricsTask.parseSingle => Could not parse the log line", exc, "This entry will be skipped"),
                LogLevels.WARNING
            );

            return(null);
        }
    }

    ///..
    /**
     * Splits the string on the {@code ]} character.
     * @param line : The log line to split.
     * @param start : The starting index of the string.
     * The array should always contain one element used as a reference to an integer.
     * @return The prefix up to the first {@code ]} character of {@code line}.
    */
    private String split(String line, int[] start) {

        StringBuilder piece = new StringBuilder();
        char pos;

        do {

            pos = line.charAt(start[0]);

            if((Character.isWhitespace(pos) == false || pos == ' ') && pos != '[' && pos != ']' && pos != '-') {

                piece.append(pos);
            }

            start[0]++;

        } while(pos != ']');

        return(piece.toString());
    }

    ///
}
