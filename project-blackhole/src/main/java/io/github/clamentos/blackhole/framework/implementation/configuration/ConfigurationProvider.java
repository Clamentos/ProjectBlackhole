package io.github.clamentos.blackhole.framework.implementation.configuration;

///
import io.github.clamentos.blackhole.framework.implementation.logging.LogLevels;
import io.github.clamentos.blackhole.framework.implementation.logging.LogPrinter;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.ExceptionFormatter;

///.
import java.io.IOException;

///..
import java.lang.reflect.Field;

///..
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

///..
import java.util.Properties;

///
/**
 * <h3>Configuration provider</h3>
 * Provides constants coming from the {@code Application.properties} file located in {@code [classpath]/resources}.
*/
//TODO: set the actual values
public final class ConfigurationProvider {

    ///
    /** Singleton instance of {@code this} instantiated during class loading. */
    private static final ConfigurationProvider INSTANCE = new ConfigurationProvider();

    ///.
    // Logging.

    /**
     * <p>Specifies if the log printer should flush the write buffer after each print.</p>
     * Default: {@code true}
    */
    public final boolean FLUSH_AFTER_WRITE;

    /**
     * <p>Specifies the maximum amount of milliseconds that the logger can wait while inserting the log into the queue.</p>
     * Default: {@code 500} --- Minimum: {@code 0} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int LOG_QUEUE_INSERT_TIMEOUT;

    /**
     * <p>Specifies the maximum number of milliseconds that the log task can wait while reading from the queue before trying again.</p>
     * Default: {@code 500} --- Minimum: {@code 0} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int LOG_QUEUE_POLL_TIMEOUT;

    /**
     * <p>Specifies the maximum number of busy-wait attempts on the queue that the logger can do before blocking.</p>
     * Default: {@code 10} --- Minimum: {@code 0} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int MAX_LOG_QUEUE_INSERT_ATTEMPTS;

    /**
     * <p>Specifies the maximum number of busy-wait attempts on the queue that the log task can do before blocking.</p>
     * Default: {@code 10} --- Minimum: {@code 0} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int MAX_LOG_QUEUE_POLL_ATTEMPTS;

    /**
     * <p>Specifies the maximum capacity of the log queue.</p>
     * Default: {@code 100000} --- Minimum: {@code 100} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int MAX_LOG_QUEUE_SIZE;

    /**
     * <p>Specifies the size of the log file reader and writer buffers in bytes.</p>
     * Default: {@code 65536} --- Minimum: {@code 256} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int READER_WRITER_BUFFER_SIZE;

    ///..
    // Metrics.

    /**
     * <p>Specifies the number of "sleep chunks" that the metrics task will perform before triggering.</p>
     * Default: {@code 600} --- Minimum: {@code 100} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int METRICS_TASK_SCHEDULING_NUM_CHUNKS;

    /**
     * <p>Specifies the "sleep chunk" size of the metrics task.</p>
     * Default: {@code 500} --- Minimum: {@code 100} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int METRICS_TASK_SCHEDULING_CHUNK_SIZE;

    ///..
    // Network.

    /**
     * <p>Specifies the maximum amount of milliseconds that the server can spend blocked while reading from the input stream.</p>
     * Default: {@code 10000} --- Minimum: {@code 1000} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int CLIENT_SOCKET_TIMEOUT;

    /**
     * <p>Specifies the maximum number of client sockets per remote IP address.</p>
     * Default: {@code 2} --- Minimum: {@code 1} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int MAX_CLIENTS_PER_IP;

    /**
     * <p>Specifies the maximum queue length for incoming client connections.</p>
     * Default: {@code 50} --- Minimum: {@code 1} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int MAX_INCOMING_CONNECTIONS;

    /**
     * <p>Specifies the maximum non-streamed request size in bytes.</p>
     * Default: {@code 1000000} --- Minimum: {@code 100000} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int MAX_REQUEST_SIZE;

    /**
     * <p>Specifies the total maximum number of client sockets that can be handled by the system.</p>
     * Default: {@code 10000} --- Minimum: {@code 10} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int MAX_SOCKETS;

    /**
     * <p>Specifies the TCP port for the server socket.</p>
     * Default: {@code 8080} --- Minimum: {@code 0} --- Maximum: {@code 65535}
    */
    public final int SERVER_PORT;

    /**
     * <p>Specifies the server socket timeout in milliseconds.</p>
     * <p>On timeout, the socket won't be closed and the server will simply retry.</p>
     * Default: {@code 500} --- Minimum: {@code 100} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int SERVER_SOCKET_TIMEOUT;

    ///..
    // Security.

    /**
     * <p>Specifies the maximum number of sessions that a single user can have.</p>
     * Default: {@code 2} --- Minimum: {@code 1} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int MAX_USER_SESSIONS;

    /**
     * <p>Specifies the duration of a user session in milliseconds.</p>
     * Default: {@code 3600000} --- Minimum: {@code 900000} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int SESSION_DURATION;

    ///..
    // Persistence.

    /**
     * <p>Specifies the database URL.</p>
     * Default: {@code jdbc:postgresql://127.0.0.1:5432/example_db}
    */
    public final String DATABASE_ADDRESS;

    /**
     * <p>Specifies the maximum number of seconds to wait when checking a connection for validity before timing out.</p>
     * Default: {@code 5} --- Minimum: {@code 1} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int DATABASE_CONNECTION_CHECK_TIMEOUT;

    /**
     * <p>Specifies the database username for login.</p>
     * Default: {@code admin}
    */
    public final String DATABASE_USERNAME;

    /**
     * <p>Specifies the database password for login.</p>
     * Default: {@code admin}
    */
    public final String DATABASE_PASSWORD;

    /**
     * <p>Specifies if the system should re-apply the database schema on startup.</p>
     * Default: {@code false}
    */
    public final boolean GENERATE_DATABASE_SCHEMA;

    /**
     * <p>Specifies if the system should import the data to the database on startup.</p>
     * Default: {@code false}
    */
    public final boolean INITIALIZE_DATABASE_DATA;

    /**
     * <p>Specifies the maximum number of prepared statements that can be cached.</p>
     * Default: {@code 250} --- Minimum: {@code 1} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int MAX_NUM_CACHEABLE_PREPARED_STATEMENTS;

    /**
     * <p>Specifies the maximum number of busy-wait attempts on the pool before blocking.</p>
     * Default: {@code 10} --- Minimum: {@code 0} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int MAX_POOL_POLL_ATTEMPTS;

    /**
     * <p>Specifies the maximum size in mebibytes that each prepared statement cache entry can be.</p>
     * Default: {@code 5} --- Minimum: {@code 1} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int MAX_PREPARED_STATEMENTS_CACHE_ENTRY_SIZE;

    /**
     * <p>Specifies the number of pooled database connections.</p>
     * Default: {@code 10} --- Minimum: {@code 1} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int NUM_DATABASE_CONNECTIONS;

    /**
     * <p>Specifies the number of database connection per sub-pools.</p>
     * <p>This value MUST divide {@code NUM_DATABASE_CONNECTIONS}.</p>
     * Default: {@code 10} --- Minimum: {@code 1} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int NUM_DATABASE_CONNECTIONS_PER_POOL;

    /**
     * <p>Specifies the "sleep chunk" size while closing the pool in milliseconds.</p>
     * Default: {@code 500} --- Minimum: {@code 100} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int POOL_SHUTDOWN_SLEEP_CHUNK_SIZE;

    /**
     * <p>Specifies the number of executions on a prepared statement before switching to database-side statement caching.</p>
     * Default: {@code 5} --- Minimum: {@code 1} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int PREPARE_THRESHOLD;

    ///..
    // Caching.

    /**
     * <p>Specifies the maximum size of the cache in bytes.</p>
     * Default: {@code 1073741824} --- Minimum: {@code 0} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int CACHE_CAPACITY;

    /**
     * <p>Specifies the timeout in milliseconds of each cache entry.</p>
     * Default: {@code 60000} --- Minimum: {@code 10000} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int CACHE_ENTRY_DURATION;

    ///..
    // Task managing.

    /**
     * <p>Specifies the "sleep chunk" size while shutting down the tasks.</p>
     * Default: {@code 500} --- Minimum: {@code 100} --- Maximum: {@code Integer.MAX_VALUE}
    */
    public final int TASK_MANAGER_SLEEP_CHUNK_SIZE;

    ///.
    private final LogPrinter log_printer;

    ///..
    private final Properties properties;
    private final Field[] fields;

    ///..
    private int current_field_index;

    ///
    private ConfigurationProvider() {

        log_printer = LogPrinter.getInstance();

        properties = new Properties();
        fields = ConfigurationProvider.class.getFields();

        current_field_index = 0;

        try {

            properties.load(Files.newInputStream(Paths.get("resources/Application.properties")));
        }

        catch(InvalidPathException | IOException exc) {

            log_printer.logToFile(

                ExceptionFormatter.format("ConfigurationProvider.new >> ", exc, " >> The defaults will be used"), LogLevels.WARNING
            );
        }

        FLUSH_AFTER_WRITE = checkBoolean("true");
        LOG_QUEUE_INSERT_TIMEOUT = checkInt("500", 0, Integer.MAX_VALUE);
        LOG_QUEUE_POLL_TIMEOUT = checkInt("500", 0, Integer.MAX_VALUE);
        MAX_LOG_QUEUE_INSERT_ATTEMPTS = checkInt("10", 0, Integer.MAX_VALUE);
        MAX_LOG_QUEUE_POLL_ATTEMPTS = checkInt("10", 0, Integer.MAX_VALUE);
        MAX_LOG_QUEUE_SIZE = checkInt("100000", 100, Integer.MAX_VALUE);
        READER_WRITER_BUFFER_SIZE = checkInt("65536", 256, Integer.MAX_VALUE);

        METRICS_TASK_SCHEDULING_NUM_CHUNKS = checkInt("600", 100, 100000);
        METRICS_TASK_SCHEDULING_CHUNK_SIZE = checkInt("500", 100, 1000);

        CLIENT_SOCKET_TIMEOUT = checkInt("10000", 1000, Integer.MAX_VALUE);
        MAX_CLIENTS_PER_IP = checkInt("2", 1, Integer.MAX_VALUE);
        MAX_INCOMING_CONNECTIONS = checkInt("50", 1, Integer.MAX_VALUE);
        MAX_REQUEST_SIZE = checkInt("1000000", 100000, Integer.MAX_VALUE);
        MAX_SOCKETS = checkInt("10000", 10, Integer.MAX_VALUE);
        SERVER_PORT = checkInt("8080", 0, Integer.MAX_VALUE);
        SERVER_SOCKET_TIMEOUT = checkInt("500", 100, Integer.MAX_VALUE);
        MAX_USER_SESSIONS = checkInt("2", 1, Integer.MAX_VALUE);
        SESSION_DURATION = checkInt("3600000", 900000, Integer.MAX_VALUE);

        DATABASE_ADDRESS = checkString("jdbc:postgresql://127.0.0.1:5432/mock_db");
        DATABASE_CONNECTION_CHECK_TIMEOUT = checkInt("5", 1, Integer.MAX_VALUE);
        DATABASE_USERNAME = checkString("admin");
        DATABASE_PASSWORD = checkString("admin");
        GENERATE_DATABASE_SCHEMA = checkBoolean("false");
        INITIALIZE_DATABASE_DATA = checkBoolean("false");
        MAX_NUM_CACHEABLE_PREPARED_STATEMENTS = checkInt("256", 1, Integer.MAX_VALUE);
        MAX_POOL_POLL_ATTEMPTS = checkInt("10", 0, Integer.MAX_VALUE);
        MAX_PREPARED_STATEMENTS_CACHE_ENTRY_SIZE = checkInt("5", 1, Integer.MAX_VALUE);
        NUM_DATABASE_CONNECTIONS = checkInt("10", 1, Integer.MAX_VALUE);
        NUM_DATABASE_CONNECTIONS_PER_POOL = checkInt("10", 1, Integer.MAX_VALUE);
        POOL_SHUTDOWN_SLEEP_CHUNK_SIZE = checkInt("500", 100, Integer.MAX_VALUE);
        PREPARE_THRESHOLD = checkInt("5", 1, Integer.MAX_VALUE);

        CACHE_CAPACITY = checkInt("1073741824", 0, Integer.MAX_VALUE);
        CACHE_ENTRY_DURATION = checkInt("60000", 10000, Integer.MAX_VALUE);

        TASK_MANAGER_SLEEP_CHUNK_SIZE = checkInt("500", 100, Integer.MAX_VALUE);

        printFields();
        log_printer.logToFile("ConfigurationProvider.new >> Instantiation successfull", LogLevels.SUCCESS);
    }

    ///
    /** @return The {@link ConfigurationProvider} instance created during class loading. */
    public static ConfigurationProvider getInstance() {

        return(INSTANCE);
    }

    ///
    // Checks if the value of the current property is "ok", else use the provided default.
    private int checkInt(String default_value, int low, int high) {

        String constant_name = fields[current_field_index++].getName();
        String property = (String)properties.getOrDefault(constant_name, default_value);

        try {

            int result = Integer.parseInt(property);

            if(result < low || result > high) {

                log_printer.logToFile(
                
                    "ConfigurationProvider.checkInt >> Property " + constant_name + " must be between "
                    + low + " and " + high + ". The default value of: " + default_value + " will be used",
                    LogLevels.WARNING
                );

                return(Integer.parseInt(default_value));
            }

            return(result);
        }

        catch(NumberFormatException exc) {

            log_printer.logToFile(

                ExceptionFormatter.format(
                    
                    "ConfigurationProvider.checkInt >> ", exc,
                    " >> On property " + constant_name + " The default value of:" + default_value + "will be used"
                ),

                LogLevels.WARNING
            );

            return(Integer.parseInt(default_value));
        }
    }

    ///..
    // Checks if the value of the current property is "ok", else use the provided default.
    private boolean checkBoolean(String default_value) {

        String constant_name = fields[current_field_index++].getName();
        String property = (String)properties.getOrDefault(constant_name, default_value);

        if(property.equalsIgnoreCase("true")) {

            return(true);
        }

        if(property.equalsIgnoreCase("false")) {

            return(false);
        }

        log_printer.logToFile(

            "ConfigurationProvider.checkBoolean >> Property " + constant_name +
            " must be either true or false. The default value of: " + default_value + " will be used",
            LogLevels.WARNING
        );

        return(Boolean.parseBoolean(default_value));
    }

    ///..
    // Checks if the value of the current property is "ok", else use the provided default.
    private String checkString(String default_value) {

        String constant_name = fields[current_field_index++].getName();
        String property = (String)properties.getOrDefault(constant_name, default_value);

        if(property == null || property.equals("")) {

            log_printer.logToFile(

                "ConfigurationProvider.checkString >> Property " + constant_name +
                " must not be null nor empty. The default value of: " + default_value + " will be used",
                LogLevels.WARNING
            );
            
            return(default_value);
        }

        return(property);
    }

    ///..
    // Prints the aquired properties as a feedback.
    private void printFields() {

        String padding;
        int longest_name = 0;

        try {

            for(Field field : fields) {

                if(field.getName().length() > longest_name) {

                    longest_name = field.getName().length();
                }
            }

            for(Field field : fields) {

                padding = " ".repeat(longest_name - field.getName().length());

                log_printer.logToFile(

                    "Property: " + field.getName() + padding + "    Value: " + field.get(this).toString(), LogLevels.INFO
                );
            }
        }

        // This should never happen...
        catch(IllegalAccessException exc) {

            log_printer.logToFile(ExceptionFormatter.format("ConfigurationProvider.printFields >> ", exc, ""), LogLevels.FATAL);
            System.exit(1);
        }
    }

    ///
}