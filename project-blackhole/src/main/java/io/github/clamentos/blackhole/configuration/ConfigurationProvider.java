package io.github.clamentos.blackhole.configuration;

///
// Some imports are only used for JavaDocs.

import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.LogTask;
import io.github.clamentos.blackhole.network.tasks.ServerTask;

import java.io.IOException;

import java.lang.reflect.Field;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

///
/**
 * <h3>Repository of all configuration constants</h3>
 * 
 * <p>This class will use the {@code Application.properties} file located in
 * {@code [classpath]/resources}.</p>
 * 
 * The constructor will read the configuration file and if such file doesn't exist, then the defaults for
 * all properties will be used. If a particular property isn't defined in the file, then its default will be
 * used.
 * 
 * The configuration properties are:
 * 
 * <ul>
 *     <li>{@code MAX_LOG_QUEUE_POLLS}: Specifies the maximum number of polls on the log queue.
 *         <ul>
 *             <li>{@code min}: 1</li>
 *             <li>{@code max}: Integer.MAX_VALUE</li>
 *             <li>{@code default}: 100</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code LOG_QUEUE_SAMPLE_TIME}: Specifies the maximum log queue wait time (in milliseconds) of each
 *         poll sample when the queue is empty.
 *         <ul>
 *             <li>{@code min}: 1</li>
 *             <li>{@code max}: Integer.MAX_VALUE</li>
 *             <li>{@code default}: 500</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code LOG_QUEUE_INSERT_TIMEOUT}: Specifies the log queue timeout (in milliseconds) when 
 *         attempting to insert a log.
 *         <ul>
 *             <li>{@code min}: 1</li>
 *             <li>{@code max}: Integer.MAX_VALUE</li>
 *             <li>{@code default}: 5000</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code MAX_LOG_QUEUE_SIZE}: Specifies the maximum number of logs that the queue can hold.
 *         <ul>
 *             <li>{@code min}: 1000</li>
 *             <li>{@code max}: Integer.MAX_VALUE</li>
 *             <li>{@code default}: 100000</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code NUM_LOG_TASKS}: Specifies the number of concurrent log tasks present in the system.
 *         <ul>
 *             <li>{@code min}: 1</li>
 *             <li>{@code max}: Integer.MAX_VALUE</li>
 *             <li>{@code default}: 1</li>
 *         </ul>
 *    </li>
 * 
 *     <li>{@code MAX_LOG_FILE_SIZE}: Specifies the maximum log file size in bytes. Above this, a new log 
 *         file will be created.
 *         <ul>
 *             <li>{@code min}: 10000</li>
 *             <li>{@code max}: Integer.MAX_VALUE</li>
 *             <li>{@code default}: 10000000</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code MIN_LOG_LEVEL}: Specifies the minimum log level (index) below which logs are discarded.
 *         <ul>
 *             <li>{@code min}:</li>
 *             <li>{@code max}:</li>
 *             <li>{@code default}:</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code DEBUG_LEVEL_TO_FILE}: Specifies if logs with level of {@link LogLevel#DEBUG} should be
 *         printed to file or console.
 *         <ul>
 *             <li>{@code min}: false</li>
 *             <li>{@code max}: true</li>
 *             <li>{@code default}: false</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code INFO_LEVEL_TO_FILE}: Specifies if logs with level of {@link LogLevel#INFO} should be
 *         printed to file or console.
 *         <ul>
 *             <li>{@code min}: false</li>
 *             <li>{@code max}: true</li>
 *             <li>{@code default}: false</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code SUCCESS_LEVEL_TO_FILE}: Specifies if logs with level of {@link LogLevel#SUCCESS} should be
 *         printed to file or console.
 *         <ul>
 *             <li>{@code min}: false</li>
 *             <li>{@code max}: true</li>
 *             <li>{@code default}: false</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code NOTE_LEVEL_TO_FILE}: Specifies if logs with level of {@link LogLevel#NOTE} should be
 *         printed to file or console.
 *         <ul>
 *             <li>{@code min}: false</li>
 *             <li>{@code max}: true</li>
 *             <li>{@code default}: false</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code WARNING_LEVEL_TO_FILE}: Specifies if logs with level of {@link LogLevel#WARNING} should be
 *         printed to file or console.
 *         <ul>
 *             <li>{@code min}: false</li>
 *             <li>{@code max}: true</li>
 *             <li>{@code default}: false</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code ERROR_LEVEL_TO_FILE}: Specifies if logs with level of {@link LogLevel#ERROR} should be
 *         printed to file or console.
 *         <ul>
 *             <li>{@code min}: false</li>
 *             <li>{@code max}: true</li>
 *             <li>{@code default}: false</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code SERVER_PORT}: Specifies the TCP port of the server.
 *         <ul>
 *             <li>{@code min}: 1</li>
 *             <li>{@code max}: 65535</li>
 *             <li>{@code default}: 8080</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code MAX_SERVER_START_ATTEMPTS}: Specifies how many attempts to do before giving up when
 *         attempting to start the server.
 *         <ul>
 *             <li>{@code min}: 1</li>
 *             <li>{@code max}: Integer.MAX_VALUE</li>
 *             <li>{@code default}: 5</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code SERVER_SOCKET_SAMPLE_TIME}: Specifies the server socket timeout for one sample
 *         in milliseconds.
 *         <ul>
 *             <li>{@code min}: 1</li>
 *             <li>{@code max}: Integer.MAX_VALUE</li>
 *             <li>{@code default}: 500</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code MAX_SERVER_SOCKET_SAMPLES}: Specifies the maximum number of server socket timeout samples
 *         before actually timing out.
 *         <ul>
 *             <li>{@code min}: 1</li>
 *             <li>{@code max}: Integer.MAX_VALUE</li>
 *             <li>{@code default}: 60</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code STREAM_BUFFER_SIZE}: Specifies the size of the stream buffers in bytes.
 *         <ul>
 *             <li>{@code min}: 1024</li>
 *             <li>{@code max}: Integer.MAX_VALUE</li>
 *             <li>{@code default}: 16384</li>
 *         </ul>
 *    </li>
 * 
 *     <li>{@code CLIENT_SOCKET_SAMPLE_TIME}: Specifies the client socket timeout for one sample
 *         in milliseconds.
 *         <ul>
 *             <li>{@code min}: 1</li>
 *             <li>{@code max}: Integer.MAX_VALUE</li>
 *             <li>{@code default}: 500</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code MAX_CLIENT_SOCKET_SAMPLES}: Specifies the maximum number of client socket timeout samples
 *         before actually timing out.
 *         <ul>
 *             <li>{@code min}: 1</li>
 *             <li>{@code max}: Integer.MAX_VALUE</li>
 *             <li>{@code default}: 60</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code MAX_REQUESTS_PER_CLIENT}: Specifies the maximum number of requests that a client socket
 *         can have throughout its lifetime. If the limit is exceeded, it must be closed.
 *         <ul>
 *             <li>{@code min}: 1</li>
 *             <li>{@code max}: Integer.MAX_VALUE</li>
 *             <li>{@code default}: 10</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code MAX_CLIENTS_PER_IP}: Specifies the maximum number of sockets that a particular ip address
 *         can have. Beyond this, sockets must be refused for that address.
 *         <ul>
 *             <li>{@code min}: 1</li>
 *             <li>{@code max}: Integer.MAX_VALUE</li>
 *             <li>{@code default}: 1</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code MIN_CLIENT_SPEED}: Specifies the maximum wait time (in milliseconds) that the server is
 *         allowed to wait on each read, above which the socket will be closed.
 *         <ul>
 *             <li>{@code min}:</li>
 *             <li>{@code max}:</li>
 *             <li>{@code default}:</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code MAX_USER_SESSIONS}: Specifies the maximum number of sessions that a user can have.
 *         <ul>
 *             <li>{@code min}:</li>
 *             <li>{@code max}:</li>
 *             <li>{@code default}:</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code SESSION_DURATION}: Specifies the duration (in milliseconds) of user sessions.
 *         <ul>
 *             <li>{@code min}:</li>
 *             <li>{@code max}:</li>
 *             <li>{@code default}:</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code GEN_DB_SCHEMA}: Specifies if it's necessary to export the schema to the database or not
 *         during application startup.
 *         <ul>
 *             <li>{@code min}:</li>
 *             <li>{@code max}:</li>
 *             <li>{@code default}:</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code INIT_DB_DATA}: Specifies if it's necessary to export the data to the database or not
 *         during application startup.
 *         <ul>
 *             <li>{@code min}:</li>
 *             <li>{@code max}:</li>
 *             <li>{@code default}:</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code DB_ADDRESS}: Specifies the address of the database.</li>
 *     <li>{@code DB_USERNAME}: Specifies the database username.</li>
 *     <li>{@code DB_PASSWORD}: Specifies the database password.</li>
 * 
 *     <li>{@code NUM_DB_CONNECTIONS}: Specifies the number of connections in the database connection pool.
 *         <ul>
 *             <li>{@code min}:</li>
 *             <li>{@code max}:</li>
 *             <li>{@code default}:</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code DB_CONNECTION_TIMEOUT}: Specifies the timeout (in milliseconds) of a single database
 *         connection.
 *         <ul>
 *             <li>{@code min}:</li>
 *             <li>{@code max}:</li>
 *             <li>{@code default}:</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code MAX_DB_ATTEMPTS}: Specifies the maximum number of database connection attempts,
 *         after which, the query will fail.
 *         <ul>
 *             <li>{@code min}:</li>
 *             <li>{@code max}:</li>
 *             <li>{@code default}:</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code PREPARE_THRESHOLD}: Specifies the number of PreparedStatement executions required
 *         before switching over to use server side prepared statements.
 *         <ul>
 *             <li>{@code min}:</li>
 *             <li>{@code max}:</li>
 *             <li>{@code default}:</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code MAX_NUM_CACHEABLE_PS}: Specifies the number of queries that are cached in each connection.
 *         <ul>
 *             <li>{@code min}:</li>
 *             <li>{@code max}:</li>
 *             <li>{@code default}:</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code MAX_PS_CACHE_ENTRY_SIZE}: Specifies the maximum size (in mebibytes) of the prepared queries
 *         cache.
 *         <ul>
 *             <li>{@code min}:</li>
 *             <li>{@code max}:</li>
 *             <li>{@code default}:</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code CACHE_CAPACITY}: Specifies the maximum number of objects that the cache can hold.
 *         Powers of two are preferred but not required.
 *         <ul>
 *             <li>{@code min}:</li>
 *             <li>{@code max}:</li>
 *             <li>{@code default}:</li>
 *         </ul>
 *     </li>
 * 
 *     <li>{@code CACHE_ENTRY_DURATION}: Specifies the duration (in milliseconds) of all cache entries.
 *         After this, the entry will be considered as expired and, thus, will cause a miss.
 *         <ul>
 *             <li>{@code min}:</li>
 *             <li>{@code max}:</li>
 *             <li>{@code default}:</li>
 *         </ul>
 *     </li>
 * </ul>
 * 
 * @see {@link LogTask}
 * @see {@link LogLevel}
 * @see {@link ServerTask}
 * @apiNote This class is an <b>eager-loaded singleton</b>.
*/
public final class ConfigurationProvider {

    ///
    private static final ConfigurationProvider INSTANCE = new ConfigurationProvider();
    private Map<String, String> problems;

    ///
    // Logging
    public final int MAX_LOG_QUEUE_POLLS;
    public final int LOG_QUEUE_SAMPLE_TIME;
    public final int LOG_QUEUE_INSERT_TIMEOUT;
    public final int MAX_LOG_QUEUE_SIZE;

    public final int NUM_LOG_TASKS;

    public final int MAX_LOG_FILE_SIZE;

    public final int MIN_LOG_LEVEL;

    public final boolean DEBUG_LEVEL_TO_FILE;
    public final boolean INFO_LEVEL_TO_FILE;
    public final boolean SUCCESS_LEVEL_TO_FILE;
    public final boolean NOTE_LEVEL_TO_FILE;
    public final boolean WARNING_LEVEL_TO_FILE;
    public final boolean ERROR_LEVEL_TO_FILE;

    ///
    // Network
    public final int SERVER_PORT;
    public final int MAX_SERVER_START_ATTEMPTS;

    public final int SERVER_SOCKET_SAMPLE_TIME;
    public final int MAX_SERVER_SOCKET_SAMPLES;

    public final int STREAM_BUFFER_SIZE;

    public final int CLIENT_SOCKET_SAMPLE_TIME;
    public final int MAX_CLIENT_SOCKET_SAMPLES;
    public final int MAX_REQUESTS_PER_CLIENT;
    public final int MAX_CLIENTS_PER_IP;
    public final int MIN_CLIENT_SPEED;

    ///
    // Security
    public final int MAX_USER_SESSIONS;
    public final long SESSION_DURATION;

    ///
    // Persistence
    public final boolean GEN_DB_SCHEMA;
    public final boolean INIT_DB_DATA;

    public final String DB_ADDRESS;
    public final String DB_USERNAME;
    public final String DB_PASSWORD;

    public final int NUM_DB_CONNECTIONS;
    public final int DB_CONNECTION_TIMEOUT;
    public final int NUM_POOLS;

    public final int MAX_DB_ATTEMPTS;

    public final int PREPARE_THRESHOLD;
    public final int MAX_NUM_CACHEABLE_PS;
    public final int MAX_PS_CACHE_ENTRY_SIZE;

    ///
    // Caching
    public final int CACHE_CAPACITY;
    public final long CACHE_ENTRY_DURATION;

    ///
    /*
     * This method reads the Application.properties file and initializes all the constants as well as
     * the "problems" map. The map can be used by other objects to see if the value of a specific
     * constant came from the file, or a default was chosen.
    */
    private ConfigurationProvider() {

        int int_max = Integer.MAX_VALUE;
        long long_max = Long.MAX_VALUE;

        Properties props = new Properties();
        problems = new HashMap<>();

        try {

            props.load(Files.newInputStream(Paths.get("resources/Application.properties")));
        }

        catch(InvalidPathException | IOException exc) {

            printValue("Could not read the Application.properties file, the defaults will be used", true);
        }

        MAX_LOG_QUEUE_POLLS = checkInt(props, "MAX_LOG_QUEUE_POLLS", "100", 1, int_max);
        LOG_QUEUE_SAMPLE_TIME = checkInt(props, "LOG_QUEUE_SAMPLE_TIME", "500", 1, int_max);
        LOG_QUEUE_INSERT_TIMEOUT = checkInt(props, "LOG_QUEUE_INSERT_TIMEOUT", "5000", 1, int_max);
        MAX_LOG_QUEUE_SIZE = checkInt(props, "MAX_LOG_QUEUE_SIZE", "100000", 1000, int_max);
        NUM_LOG_TASKS = checkInt(props, "NUM_LOG_TASKS", "1", 1, int_max);
        MAX_LOG_FILE_SIZE = checkInt(props, "MAX_LOG_FILE_SIZE", "10000000", 10000, int_max);
        MIN_LOG_LEVEL = checkLogLevel(props, "MIN_LOG_LEVEL", "1");
        DEBUG_LEVEL_TO_FILE = checkBoolean(props, "DEBUG_LEVEL_TO_FILE", "false");
        INFO_LEVEL_TO_FILE = checkBoolean(props, "INFO_LEVEL_TO_FILE", "false");
        SUCCESS_LEVEL_TO_FILE = checkBoolean(props, "SUCCESS_LEVEL_TO_FILE", "false");
        NOTE_LEVEL_TO_FILE = checkBoolean(props, "NOTE_LEVEL_TO_FILE", "false");
        WARNING_LEVEL_TO_FILE = checkBoolean(props, "WARNING_LEVEL_TO_FILE", "false");
        ERROR_LEVEL_TO_FILE = checkBoolean(props, "ERROR_LEVEL_TO_FILE", "false");

        SERVER_PORT = checkInt(props, "SERVER_PORT", "8080", 1, 65535);
        MAX_SERVER_START_ATTEMPTS = checkInt(props, "MAX_SERVER_START_ATTEMPTS", "5", 1, int_max);
        SERVER_SOCKET_SAMPLE_TIME = checkInt(props, "SERVER_SOCKET_SAMPLE_TIME", "500", 1, int_max);
        MAX_SERVER_SOCKET_SAMPLES = checkInt(props, "MAX_SERVER_SOCKET_SAMPLES", "60", 1, int_max);
        STREAM_BUFFER_SIZE = checkInt(props, "STREAM_BUFFER_SIZE", "16384", 1024, int_max);
        CLIENT_SOCKET_SAMPLE_TIME = checkInt(props, "CLIENT_SOCKET_SAMPLE_TIME", "500", 1, int_max);
        MAX_CLIENT_SOCKET_SAMPLES = checkInt(props, "MAX_CLIENT_SOCKET_SAMPLES", "60", 1, int_max);
        MAX_REQUESTS_PER_CLIENT = checkInt(props, "MAX_REQUESTS_PER_CLIENT", "10", 1, int_max);
        MAX_CLIENTS_PER_IP = checkInt(props, "MAX_CLIENTS_PER_IP", "1", 1, int_max);
        MIN_CLIENT_SPEED = checkInt(props, "MIN_CLIENT_SPEED", "5", 1, int_max);

        MAX_USER_SESSIONS = checkInt(props, "MAX_USER_SESSIONS", "1", 1, int_max);
        SESSION_DURATION = checkLong(props, "SESSION_DURATION", "3600000", 60000, long_max);

        GEN_DB_SCHEMA = checkBoolean(props, "GEN_DB_SCHEMA", "false");
        INIT_DB_DATA = checkBoolean(props, "INIT_DB_DATA", "false");
        DB_ADDRESS = checkString(props, "DB_ADDRESS", "jdbc:postgresql://127.0.0.1:5432/mock");
        DB_USERNAME = checkString(props, "DB_USERNAME", "admin");
        DB_PASSWORD = checkString(props, "DB_PASSWORD", "admin");
        NUM_DB_CONNECTIONS = checkInt(props, "NUM_DB_CONNECTIONS", "5", 1, int_max);
        DB_CONNECTION_TIMEOUT = checkInt(props, "DB_CONNECTION_TIMEOUT", "10000", 1, int_max);
        NUM_POOLS = checkInt(props, "NUM_POOLS", "1", 1, int_max);
        MAX_DB_ATTEMPTS = checkInt(props, "MAX_DB_ATTEMPTS", "2", 1, int_max);
        PREPARE_THRESHOLD = checkInt(props, "PREPARE_THRESHOLD", "5", 1, int_max);
        MAX_NUM_CACHEABLE_PS = checkInt(props, "MAX_NUM_CACHEABLE_PS", "256", 0, int_max);
        MAX_PS_CACHE_ENTRY_SIZE = checkInt(props, "MAX_PS_CACHE_ENTRY_SIZE", "5", 0, int_max);

        CACHE_CAPACITY = checkInt(props, "CACHE_CAPACITY", "10000", 0, int_max);
        CACHE_ENTRY_DURATION = checkLong(props, "CACHE_ENTRY_DURATION", "5000", 100, long_max);

        printFields();
    }

    ///
    /** @return The {@link ConfigurationProvider} instance created during class loading. */
    public static ConfigurationProvider getInstance() {

        return(INSTANCE);
    }

    ///
    /**
     * Gets the problems map, which associates configuration names with the error generated
     * during property initialization. If a configuration constant didn't generate an error,
     * there will be no entry in the map for that name.
     * @return The never {@code null} problems map.
    */
    public Map<String, String> getProblems() {

        return(problems);
    }

    ///
    // These methods check if the value of a particular property is "ok", else they use the provided default.

    private int checkInt(Properties p, String name, String def, int low, int high) {

        String s = (String)p.getOrDefault(name, def);

        try {

            int result = Integer.parseInt(s);

            if(result < low || result > high) {

                problems.put(
                
                    name,
                    "Property " + name + "must be between " + low + " and " + high +
                    ". The default value of: " + def + "will be used"
                );

                return(Integer.parseInt(def));
            }

            return(result);
        }

        catch(NumberFormatException exc) {

            problems.put(
                
                name,
                "Erroneous number format on property " + name + ": " + exc.getMessage() +
                ". The default value of: " + def + "will be used"
            );

            return(Integer.parseInt(def));
        }
    }

    private long checkLong(Properties p, String name, String def, long low, long high) {

        String s = (String)p.getOrDefault(name, def);

        try {

            long result = Long.parseLong(s);

            if(result < low || result > high) {

                problems.put(
                
                    name,
                    "Property " + name + "must be between " + low + " and " + high +
                    ". The default value of: " + def + "will be used"
                );

                return(Long.parseLong(def));
            }

            return(result);
        }

        catch(NumberFormatException exc) {

            problems.put(
                
                name,
                "Erroneous number format on property " + name + ": " + exc.getMessage() +
                ". The default value of: " + def + "will be used"
            );

            return(Integer.parseInt(def));
        }
    }

    private boolean checkBoolean(Properties p, String name, String def) {

        String s = (String)p.getOrDefault(name, def);

        if(s.equalsIgnoreCase("true")) {

            return(true);
        }

        if(s.equalsIgnoreCase("false")) {

            return(false);
        }

        problems.put(

            name,
            "Property " + name + " must be either true or false. The default value of: " +
            def + " will be used"
        );

        return(Boolean.parseBoolean(def));
    }

    private String checkString(Properties p, String name, String def) {

        String s = (String)p.getOrDefault(name, def);

        if(s == null || s == "") {

            problems.put(
                
                name,
                "Property " + name + " must not be null nor empty. The default value of: " +
                def + "will be used"
            );
            
            return(def);
        }

        return(s);
    }

    private int checkLogLevel(Properties p, String name, String def) {

        String s = (String)p.getOrDefault(name, def);

        if(s.equalsIgnoreCase("DEBUG")) return(0);
        if(s.equalsIgnoreCase("INFO")) return(1);
        if(s.equalsIgnoreCase("SUCCESS")) return(2);
        if(s.equalsIgnoreCase("NOTE")) return(3);
        if(s.equalsIgnoreCase("WARNING")) return(4);
        if(s.equalsIgnoreCase("ERROR")) return(5);

        problems.put(
            
            name,
            "Property " + name + " must be DEBUG, INFO, SUCCESS, NOTE, WARNING or ERROR. The default value of: " + def + "will be used"
        );

        return(Integer.parseInt(def));
    }

    // Used to log the aquired properties to the console as a feedback.
    private void printFields() {

        Field[] fields;
        String problem;
        String padding;

        fields = ConfigurationProvider.class.getFields();

        try {

            for(Field field : fields) {

                // 25 is the longest property name.
                padding = " ".repeat(25 - field.getName().length());
                problem = problems.get(field.getName());

                if(problem != null) {

                    printValue(problem, true);
                }

                else {

                    printValue(
                        
                        "Property: " + field.getName() + padding +
                        "    Value: " + field.get(this).toString(),
                        false
                    );
                }
            }
        }

        catch(IllegalAccessException exc) { // This should never happen...

            System.exit(1);
        }
    }

    private void printValue(String val, boolean is_warning) {

        String color;
        String level;
        String message;

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");

        if(is_warning == true) {

            color = "\u001B[33m";
            level = "[" + color + "WARNING\u001B[0m]-";
        }

        else {

            color = "\u001B[34m";
            level = "[" + color + "INFO   \u001B[0m]-";
        }

        message = "[" + color + val + "\u001B[0m]";
        System.out.println(level + "[" + formatter.format(System.currentTimeMillis()) + "]" + message);
    }

    ///
}
