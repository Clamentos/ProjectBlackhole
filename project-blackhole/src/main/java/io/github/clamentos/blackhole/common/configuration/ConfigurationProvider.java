package io.github.clamentos.blackhole.common.configuration;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.framework.logging.LogLevel;
import io.github.clamentos.blackhole.framework.logging.LogTask;
import io.github.clamentos.blackhole.framework.logging.Logger;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Eager-loaded singleton.</b></p>
 * <p>Repository of all configuration constants.</p>
 * <p>This class will use the {@code Application.properties} file located in {@code /resources}.</p>
 * <p>The constructor will read the configuration file and if such file doesn't exist,
 * then the defaults for all properties will be used. If a particular property
 * isn't defined in the file, then the default (for that particular constant) will be used.</p>
 * 
 * The configuration properties are:
 * 
 * <ul>
 *     <li>{@code MAX_LOG_QUEUE_POLLS}: maximum number of polls on {@link LinkedBlockingQueue}
 *         before blocking. Used by {@link LogTask} and {@link Logger}.</li>
 *     <li>{@code LOG_QUEUE_SAMPLE_TIME}: specifies the maximum queue wait time (in milliseconds)
 *         of each poll sample on {@link LinkedBlockingQueue}. Used by {@link Logger}.</li>
 *     <li>{@code LOG_QUEUE_INSERT_TIMEOUT}: specifies the timeout (in milliseconds) when attempting
 *         to insert a log in the log queue.</li>
 *     <li>{@code MAX_LOG_QUEUE_SIZE}: specifies the maximum number of logs that the queue
 *         can hold.</li>
 *     <li>{@code NUM_LOG_TASKS}: specifies the number of concurrent {@link LogTask}
 *         present in the system.</li>
 *     <li>{@code MAX_LOG_FILE_SIZE}: maximum log file size in bytes. Above this,
 *         a new log file will be created.</li>
 *     <li>{@code MIN_LOG_LEVEL}: minimum log level (index) below which logs are discarded.</li>
 *     <li>{@code DEBUG_LEVEL_TO_FILE}: specifies if logs with level of {@link LogLevel#DEBUG}
 *         should be printed to file or console.</li>
 *     <li>{@code INFO_LEVEL_TO_FILE}: specifies if logs with level of {@link LogLevel#INFO}
 *         should be printed to file or console.</li>
 *     <li>{@code SUCCESS_LEVEL_TO_FILE}: specifies if logs with level of {@link LogLevel#SUCCESS}
 *         should be printed to file or console.</li>
 *     <li>{@code NOTE_LEVEL_TO_FILE}: specifies if logs with level of {@link LogLevel#NOTE}
 *         should be printed to file or console.</li>
 *     <li>{@code WARNING_LEVEL_TO_FILE}: specifies if logs with level of {@link LogLevel#WARNING}
 *         should be printed to file or console.</li>
 *     <li>{@code ERROR_LEVEL_TO_FILE}: specifies if logs with level of {@link LogLevel#ERROR}
 *         should be printed to file or console.</li>
 *     <li>{@code SERVER_PORT}: specifies the TCP port of the server.</li>
 *     <li>{@code MAX_SERVER_START_ATTEMPTS}: specifies how many attempts to do before giving up
 *         when attempting to start the server.</li>
 *     <li>{@code SERVER_SOCKET_SAMPLE_TIME}: specifies the server socket timeout for one sample.</li>
 *     <li>{@code MAX_SERVER_SOCKET_SAMPLES}: specifies the maximum number of server socket timeout
 *         samples before actually timing out.</li>
 *     <li>{@code STREAM_BUFFER_SIZE}: specifies the size of the stream buffers in bytes.</li>
 *     <li>{@code CLIENT_SOCKET_SAMPLE_TIME}: specifies the client socket timeout for one sample.</li>
 *     <li>{@code MAX_CLIENT_SOCKET_SAMPLES}: specifies the maximum number of client socket timeout
 *         samples before actually timing out.</li>
 *     <li>{@code MAX_REQUESTS_PER_CLIENT}: specifies the maximum number of requests that a socket
 *         can have throughout its lifetime. If the limit is exceeded, it must be closed.</li>
 *     <li>{@code MAX_CLIENTS_PER_IP}: specifies the maximum number of sockets that a particular ip
 *         address can have. Beyond this, sockets must be refused for that address.</li>
 *     <li>{@code MIN_CLIENT_SPEED}: specifies the maximum wait time (in milliseconds) that the
 *         server is allowed to wait on each read, above which the socket will be closed.</li>
 *     <li>{@code GEN_BD_SCHEMA}: specifies if it's necessary to export the schema
 *         to the database or not.</li>
 *     <li>{@code INIT_DB_DATA}: specifies if it's necessary to export the data
 *         to the database or not.</li>
 *     <li>{@code DB_ADDRESS}: the address of the database.</li>
 *     <li>{@code DB_USERNAME}: the database username.</li>
 *     <li>{@code DB_PASSWORD}: the database password.</li>
 *     <li>{@code NUM_DB_CONNECTIONS}: specifies the number of connections in the database
 *         connection pool.</li>
 *     <li>{@code DB_CONNECTION_TIMEOUT}: specifies the timeout (in milliseconds) of
 *         a single database connection.</li>
 * </ul>
*/
public class ConfigurationProvider {

    private static final ConfigurationProvider INSTANCE = new ConfigurationProvider();
    private Map<String, String> problems;

    //____________________________________________________________________________________________________________________________________

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

    //____________________________________________________________________________________________________________________________________

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

    //____________________________________________________________________________________________________________________________________

    public final boolean GEN_BD_SCHEMA;
    public final boolean INIT_DB_DATA;

    public final String DB_ADDRESS;
    public final String DB_USERNAME;
    public final String DB_PASSWORD;

    public final int NUM_DB_CONNECTIONS;
    public final int DB_CONNECTION_TIMEOUT;

    //____________________________________________________________________________________________________________________________________

    // Thread safe assuming that nobody else is touching the Application.properties file.
    private ConfigurationProvider() {

        Properties props = new Properties();
        problems = new HashMap<>();

        try {

            props.load(Files.newInputStream(Paths.get("resources/Application.properties")));
        }

        // Ignore the exception because defaults will be used.
        catch(InvalidPathException | IOException exc) {}

        MAX_LOG_QUEUE_POLLS = checkInt(props, "MAX_LOG_QUEUE_POLLS", "100", 1, Integer.MAX_VALUE);
        LOG_QUEUE_SAMPLE_TIME = checkInt(props, "LOG_QUEUE_SAMPLE_TIME", "500", 1, Integer.MAX_VALUE);
        LOG_QUEUE_INSERT_TIMEOUT = checkInt(props, "LOG_QUEUE_INSERT_TIMEOUT", "5000", 1, Integer.MAX_VALUE);
        MAX_LOG_QUEUE_SIZE = checkInt(props, "MAX_LOG_QUEUE_SIZE", "100000", 1000, Integer.MAX_VALUE);
        NUM_LOG_TASKS = checkInt(props, "NUM_LOG_TASKS", "1", 1, Integer.MAX_VALUE);
        MIN_LOG_LEVEL = checkLogLevel(props, "MIN_LOG_LEVEL", "1");
        MAX_LOG_FILE_SIZE = checkInt(props, "MAX_LOG_FILE_SIZE", "10000000", 10_000, Integer.MAX_VALUE);
        DEBUG_LEVEL_TO_FILE = checkBoolean(props, "DEBUG_LEVEL_TO_FILE", "false");
        INFO_LEVEL_TO_FILE = checkBoolean(props, "INFO_LEVEL_TO_FILE", "false");
        SUCCESS_LEVEL_TO_FILE = checkBoolean(props, "SUCCESS_LEVEL_TO_FILE", "false");
        NOTE_LEVEL_TO_FILE = checkBoolean(props, "NOTE_LEVEL_TO_FILE", "false");
        WARNING_LEVEL_TO_FILE = checkBoolean(props, "WARNING_LEVEL_TO_FILE", "false");
        ERROR_LEVEL_TO_FILE = checkBoolean(props, "ERROR_LEVEL_TO_FILE", "false");

        SERVER_PORT = checkInt(props, "SERVER_PORT", "8080", 1, 65535);
        MAX_SERVER_START_ATTEMPTS = checkInt(props, "MAX_SERVER_START_ATTEMPTS", "5", 1, Integer.MAX_VALUE);
        SERVER_SOCKET_SAMPLE_TIME = checkInt(props, "SERVER_SOCKET_SAMPLE_TIME", "500", 1, Integer.MAX_VALUE);
        MAX_SERVER_SOCKET_SAMPLES = checkInt(props, "MAX_SERVER_SOCKET_SAMPLES", "60", 1, Integer.MAX_VALUE);
        STREAM_BUFFER_SIZE = checkInt(props, "STREAM_BUFFER_SIZE", "16384", 1024, Integer.MAX_VALUE);
        CLIENT_SOCKET_SAMPLE_TIME = checkInt(props, "CLIENT_SOCKET_SAMPLE_TIME", "500", 1, Integer.MAX_VALUE);
        MAX_CLIENT_SOCKET_SAMPLES = checkInt(props, "MAX_CLIENT_SOCKET_SAMPLES", "60", 1, Integer.MAX_VALUE);
        MAX_REQUESTS_PER_CLIENT = checkInt(props, "MAX_REQUESTS_PER_CLIENT", "10", 1, Integer.MAX_VALUE);
        MAX_CLIENTS_PER_IP = checkInt(props, "MAX_CLIENTS_PER_IP", "1", 1, Integer.MAX_VALUE);
        MIN_CLIENT_SPEED = checkInt(props, "MIN_CLIENT_SPEED", "5", 1, Integer.MAX_VALUE);

        GEN_BD_SCHEMA = checkBoolean(props, "GEN_BD_SCHEMA", "false");
        INIT_DB_DATA = checkBoolean(props, "INIT_DB_DATA", "false");
        DB_ADDRESS = checkString(props, "DB_ADDRESS", "jdbc:postgresql://127.0.0.1:5432/mock");
        DB_USERNAME = checkString(props, "DB_USERNAME", "admin");
        DB_PASSWORD = checkString(props, "DB_PASSWORD", "admin");
        NUM_DB_CONNECTIONS = checkInt(props, "NUM_DB_CONNECTIONS", "5", 1, Integer.MAX_VALUE);
        DB_CONNECTION_TIMEOUT = checkInt(props, "DB_CONNECTION_TIMEOUT", "10000", 1, Integer.MAX_VALUE);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * @return The {@link ConfigurationProvider} instance created during class loading.
    */
    public static ConfigurationProvider getInstance() {

        return(INSTANCE);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Get the problems map, which associates configuration names with the error
     * generated during property initialization. If a configuration constant didn't generate
     * an error, there will be no entry in the map for that name.
     * @return The never {@code null} problems map.
    */
    public Map<String, String> getProblems() {

        return(problems);
    }

    //____________________________________________________________________________________________________________________________________
    // These methods simply checks if the passed property is valid (all thread safe).

    private int checkInt(Properties p, String name, String def, int low, int high) {

        String s = (String)p.getOrDefault(name, def);

        try {

            int result = Integer.parseInt(s);

            if(result < low || result > high) {

                problems.put(
                
                    name,
                    "Property " + name + "must be between " + low + " and " + high + ". The default value of: " + def + "will be used"
                );

                return(Integer.parseInt(def));
            }

            return(result);
        }

        catch(NumberFormatException exc) {

            problems.put(
                
                name,
                "Erroneous number format on property " + name + ": " + exc.getMessage() + ". The default value of: " + def + "will be used"
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

        problems.put(name, "Property " + name + " must be either true or false. The default value of: " + def + "will be used");
        return(Boolean.parseBoolean(def));
    }

    private String checkString(Properties p, String name, String def) {

        String s = (String)p.getOrDefault(name, def);

        if(s == null || s == "") {

            problems.put(name, "Property " + name + " must not be null nor empty. The default value of: " + def + "will be used");
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

    //____________________________________________________________________________________________________________________________________
}
