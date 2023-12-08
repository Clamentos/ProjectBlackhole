package io.github.clamentos.blackhole.framework.implementation.logging;

///
import io.github.clamentos.blackhole.framework.implementation.configuration.ConfigurationProvider;

///..
import io.github.clamentos.blackhole.framework.implementation.persistence.PersistenceException;

///..
import io.github.clamentos.blackhole.framework.implementation.persistence.models.LogEntity;
import io.github.clamentos.blackhole.framework.implementation.persistence.models.SystemDiagnostics;

///..
import io.github.clamentos.blackhole.framework.implementation.persistence.pool.ConnectionPool;
import io.github.clamentos.blackhole.framework.implementation.persistence.pool.PooledConnection;

///..
import io.github.clamentos.blackhole.framework.implementation.persistence.repositories.Repository;

///..
import io.github.clamentos.blackhole.framework.implementation.tasks.ContinuousTask;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.ExceptionFormatter;
import io.github.clamentos.blackhole.framework.implementation.utility.IndirectInteger;
import io.github.clamentos.blackhole.framework.implementation.utility.ResourceReleaser;

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
 * <h3>Metrics task</h3>
 * Periodically writes to the database a summary of various system statistics and logs.
 * @see ContinuousTask
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
    private final Repository repository;

    ///..
    /** The log timestamp formatter with pattern: {@code dd/MM/yyyy HH:mm:ss.SSS}. */
    private final DateTimeFormatter formatter;

    ///..
    /** The counter used for scheduling sleeps. */
    private int sleep_samples;
    
    ///
    /** Instantiates a new {@link MetricsTask} object. */
    public MetricsTask() {

        super();

        logger = Logger.getInstance();
        log_printer = LogPrinter.getInstance();
        pool = ConnectionPool.getInstance();
        repository = Repository.getInstance();

        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS");

        logger.log("MetricsTask.new >> Instantiated successfully", LogLevels.SUCCESS);
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

        // Wait for scheduling.
        if(sleep_samples < ConfigurationProvider.getInstance().METRICS_TASK_SCHEDULING_NUM_CHUNKS) {

            try {

                Thread.sleep(ConfigurationProvider.getInstance().METRICS_TASK_SCHEDULING_CHUNK_SIZE);
                sleep_samples++;

                return;
            }

            catch(InterruptedException exc) {

                logger.log(ExceptionFormatter.format("MetricsTask.work >> ", exc, " >> Ignoring..."), LogLevels.NOTE);
            }
        }

        sleep_samples = 0;

        // Dump the metrics and logs to the database.
        SystemDiagnostics snapshot = MetricsTracker.getInstance().sample();
        PooledConnection connection = pool.aquireConnection(0);

        if(snapshotToDb(snapshot, connection) == true) {

            logsToDb(connection);
        }

        pool.releaseConnection(0, connection);
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void terminate() {

        logger.log("MetricsTask.terminate >> Shut down successfull", LogLevels.SUCCESS);
    }

    ///
    // Takes a snapshot and save it into the database.
    private boolean snapshotToDb(SystemDiagnostics system_diagnostics, PooledConnection connection) {

        try {

            repository.insert(List.of(system_diagnostics), connection);
            return(true);
        }

        catch(PersistenceException exc) {

            logger.log(ExceptionFormatter.format("MetricsTask.work >> ", exc, " >> Aborting..."), LogLevels.ERROR);
            return(false);
        }
    }

    ///..
    // Reads all the logs in the log file and write them to the database.
    // If the transaction was successfull, this method "cleans" the log file.
    private boolean logsToDb(PooledConnection connection) {

        // NOTE: The number of log files present in the system must be 1 or 2.
        // If there are 0, then it means that somebody deleted them while the app is running, because the app creates 1 at start up if
        // there aren't any. More than 2 is impossible as this task ensures that there will always be max 2.

        int count = countLogFiles();
        String path = null;
        List<LogEntity> log_entities = new ArrayList<>();

        try {

            // Check if there are no "leftovers" from previous failed runs.
            if(count == 1) {

                // Create a new log file and steer the LogPrinter.
                path = log_printer.createNewLogFile();
            }

            else {

                if(count == 0) {

                    // This shouldn't be possible. ERROR.
                    return(false);
                }

                // Get the older of the 2.
                path = log_printer.findOldestLogFile();
            }

            // Read and parse the selected log file.
            BufferedReader reader = new BufferedReader(new FileReader(path), ConfigurationProvider.getInstance().READER_WRITER_BUFFER_SIZE);

            while(reader.ready()) {

                log_entities.add(parseSingle(reader.readLine()));
            }

            ResourceReleaser.release(logger, "MetricsTask.logsToDb", reader);
        }

        catch(IOException exc) {

            logger.log(ExceptionFormatter.format("MetricsTask.work >> ", exc, " >> Aborting..."), LogLevels.ERROR);
            return(false);
        }

        try {

            repository.insert(log_entities, connection);
        }

        catch(PersistenceException exc) {

            logger.log(ExceptionFormatter.format("MetricsTask.work >> ", exc, " >> Aborting..."), LogLevels.ERROR);
            return(false);
        }

        // Delete the old log file if everything went ok.
        try {

            Files.delete(Paths.get(path));
            return(true);
        }

        catch(IOException exc) {

            logger.log(ExceptionFormatter.format("MetricsTask.work >> ", exc, " >> Aborting..."), LogLevels.ERROR);
            return(false);
        }
    }

    ///..
    // Counts all the "*.log" named files in the "resources/" directory.
    private int countLogFiles() {

        // Fetch all the names and count the *.log ones.
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
    // Parses a single log from the given string.
    private LogEntity parseSingle(String line) {

        IndirectInteger idx = new IndirectInteger(0);

        String log_level = split(line, idx);
        String date_time = split(line, idx);
        String log_id = split(line, idx);
        String message = split(line, idx);

        try {

            long millis = LocalDateTime.from(formatter.parse(date_time)).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            return(new LogEntity(0L, Long.parseLong(log_id, 16), millis, log_level, message));
        }

        catch(NumberFormatException | ArithmeticException | DateTimeException exc) {

            logger.log(ExceptionFormatter.format("MetricsTask.parseSingle >> ", exc, ""), LogLevels.WARNING);
            return(null);
        }
    }

    ///..
    // Splits the string on the ] character.
    private String split(String line, IndirectInteger start) {

        StringBuilder piece = new StringBuilder();
        char pos;

        do {

            pos = line.charAt(start.value);

            // Character not '[', ']', '-', whitespace (except space).
            if((Character.isWhitespace(pos) == false || pos == ' ') && pos != '[' && pos != ']' && pos != '-') {

                piece.append(pos);
            }

            start.value++;

        } while(pos != ']');

        return(piece.toString());
    }

    ///
}
