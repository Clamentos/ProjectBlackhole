// OK
package io.github.clamentos.blackhole.common.configuration;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.logging.Log;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.LogPrinter;
import io.github.clamentos.blackhole.logging.LogTask;
import io.github.clamentos.blackhole.logging.Logger;

import java.io.IOException;

import java.lang.reflect.Field;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

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
 *     <li>{@code LOG_QUEUE_TIMEOUT}: specifies the maximum queue wait time (in milliseconds)
 *         before timing out on {@link LinkedBlockingQueue}. Used by {@link Logger}.</li>
 *     <li>{@code NUM_LOG_TASKS}: specifies the number of concurrent {@link LogTask}
 *         present in the system.</li>
 *     <li>{@code MAX_LOG_FILE_SIZE}: maximum log file size in bytes. Above this,
 *         a new log file will be created.</li>
 *     <li>{@code MIN_LOG_LEVEL}: minimum log level below which logs are discarded.
 *         This parameter has no effect if the {@link LogPrinter} object is used directly.</li>
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
 *     <li>{@code STREAM_BUFFER_SIZE}: specifies the size of the stream buffers in bytes.</li>
 *     <li>{@code SOCKET_TIMEOUT}: specifies the amount of milliseconds until a socket times out.</li>
 *     <li>{@code MAX_REQUESTS_PER_SOCKET}: specifies the maximum number of requests that a socket
 *         can have throughout its lifetime. If the limit is exceeded, it must be closed.</li>
 *     <li>{@code MAX_SOCKETS_PER_IP}: specifies the maximum number of sockets that a particular ip
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
 * </ul>
*/
public class ConfigurationProvider {

    private static final ConfigurationProvider INSTANCE = new ConfigurationProvider();

    //____________________________________________________________________________________________________________________________________

    public final int MAX_LOG_QUEUE_POLLS;
    public final int LOG_QUEUE_TIMEOUT;
    public final int NUM_LOG_TASKS;
    public final int MAX_LOG_FILE_SIZE;

    public final LogLevel MIN_LOG_LEVEL;

    public final boolean DEBUG_LEVEL_TO_FILE;
    public final boolean INFO_LEVEL_TO_FILE;
    public final boolean SUCCESS_LEVEL_TO_FILE;
    public final boolean NOTE_LEVEL_TO_FILE;
    public final boolean WARNING_LEVEL_TO_FILE;
    public final boolean ERROR_LEVEL_TO_FILE;

    //____________________________________________________________________________________________________________________________________

    public final int SERVER_PORT;
    public final int MAX_SERVER_START_ATTEMPTS;
    public final int STREAM_BUFFER_SIZE;
    public final int SOCKET_TIMEOUT;
    public final int MAX_REQUESTS_PER_SOCKET;
    public final int MAX_SOCKETS_PER_IP;
    public final int MIN_CLIENT_SPEED;

    //____________________________________________________________________________________________________________________________________

    public final boolean GEN_BD_SCHEMA;
    public final boolean INIT_DB_DATA;

    public final String DB_ADDRESS;
    public final String DB_USERNAME;
    public final String DB_PASSWORD;

    //____________________________________________________________________________________________________________________________________

    // Read the Application.properties file, set all the constants and print their value as a feedback.
    // NOTE: the Logger class should not be used to print as it depends on this very class...
    //       It's however perfectly fine to use the static methods of the LogPrinter class just fine.

    private ConfigurationProvider() {

        Properties props = new Properties();

        try {

            props.load(Files.newInputStream(Paths.get("resources/Application.properties")));
        }

        catch(InvalidPathException | IOException exc) {

            LogPrinter.printToConsole(new Log(
                    
                "ConfigurationProvider.new 1 > Could not initialize, " +
                exc.getClass().getSimpleName() + ": " + exc.getMessage() +
                " Defaults will be used",
                LogLevel.WARNING
            ));
        }

        MAX_LOG_QUEUE_POLLS = checkInt(props, "MAX_LOG_QUEUE_POLLS", "100", 1, Integer.MAX_VALUE);
        LOG_QUEUE_TIMEOUT = checkInt(props, "LOG_QUEUE_TIMEOUT", "5000", 0, Integer.MAX_VALUE);
        NUM_LOG_TASKS = checkInt(props, "NUM_LOG_TASKS", "1", 1, Integer.MAX_VALUE);
        MIN_LOG_LEVEL = checkLogLevel(props, "MIN_LOG_LEVEL", "INFO");
        MAX_LOG_FILE_SIZE = checkInt(props, "MAX_LOG_FILE_SIZE", "10000000", 10_000, Integer.MAX_VALUE);
        DEBUG_LEVEL_TO_FILE = checkBoolean(props, "DEBUG_LEVEL_TO_FILE", "false");
        INFO_LEVEL_TO_FILE = checkBoolean(props, "INFO_LEVEL_TO_FILE", "false");
        SUCCESS_LEVEL_TO_FILE = checkBoolean(props, "SUCCESS_LEVEL_TO_FILE", "false");
        NOTE_LEVEL_TO_FILE = checkBoolean(props, "NOTE_LEVEL_TO_FILE", "false");
        WARNING_LEVEL_TO_FILE = checkBoolean(props, "WARNING_LEVEL_TO_FILE", "false");
        ERROR_LEVEL_TO_FILE = checkBoolean(props, "ERROR_LEVEL_TO_FILE", "false");

        SERVER_PORT = checkInt(props, "SERVER_PORT", "8080", 1, 65535);
        MAX_SERVER_START_ATTEMPTS = checkInt(props, "MAX_SERVER_START_ATTEMPTS", "5", 1, Integer.MAX_VALUE);
        STREAM_BUFFER_SIZE = checkInt(props, "STREAM_BUFFER_SIZE", "16384", 1024, Integer.MAX_VALUE);
        SOCKET_TIMEOUT = checkInt(props, "SOCKET_TIMEOUT", "10000", 1, Integer.MAX_VALUE);
        MAX_REQUESTS_PER_SOCKET = checkInt(props, "MAX_REQUESTS_PER_SOCKET", "10", 1, Integer.MAX_VALUE);
        MAX_SOCKETS_PER_IP = checkInt(props, "MAX_SOCKETS_PER_IP", "1", 1, Integer.MAX_VALUE);
        MIN_CLIENT_SPEED = checkInt(props, "MIN_CLIENT_SPEED", "5", 1, Integer.MAX_VALUE);

        GEN_BD_SCHEMA = checkBoolean(props, "GEN_BD_SCHEMA", "false");
        INIT_DB_DATA = checkBoolean(props, "INIT_DB_DATA", "false");
        DB_ADDRESS = checkString(props, "DB_ADDRESS", "jdbc:postgresql://127.0.0.1:5432/mock");
        DB_USERNAME = checkString(props, "DB_USERNAME", "admin");
        DB_PASSWORD = checkString(props, "DB_PASSWORD", "admin");

        try { // Print the values of the properties for feedback.

            Field[] fields = ConfigurationProvider.class.getFields();

            for(Field field : fields) {

                // just for aligning the prints... 25 is the longest property name.
                int amt = 25 - field.getName().length();
                String padding = " ".repeat(amt);

                LogPrinter.printToConsole(new Log(
                    
                    "Property: " + field.getName() + padding + "    Value: " + field.get(this).toString(),
                    LogLevel.INFO
                ));
            }
        }

        // This should never happen...
        catch(IllegalAccessException exc) {

            LogPrinter.printToConsole(new Log(
                    
                "ConfigurationProvider.new 2 > Could not access field, IllegalAccessException: " + exc.getMessage() + " Aborting",
                LogLevel.ERROR
            ));

            System.exit(1);
        }
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Get the {@link ConfigurationProvider} instance created during class loading.
     * @return The {@link ConfigurationProvider} instance.
    */
    public static ConfigurationProvider getInstance() {

        return(INSTANCE);
    }

    //____________________________________________________________________________________________________________________________________
    // These methods simply checks if the passed property is valid.

    private int checkInt(Properties p, String name, String def, int low, int high) {

        String s = (String)p.getOrDefault(name, def);
        Log log = new Log(

            "ConfigurationProvider.checkInt > illegal value for property: " + name +
            ". Expected range: " + low + " and " + high + ". The default will be used",
            LogLevel.WARNING
        );

        try {

            int result = Integer.parseInt(s);

            if(result < low || result > high) {

                LogPrinter.printToConsole(log);
                return(Integer.parseInt(def));
            }

            return(result);
        }

        catch(NumberFormatException exc) {

            LogPrinter.printToConsole(log);
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

        LogPrinter.printToConsole(new Log(

            "ConfigurationProvider.checkBoolean > illegal value for property: " + name +
            ". Expected: true or false (ignoring case). The default will be used",
            LogLevel.WARNING
        ));

        return(Boolean.parseBoolean(def));
    }

    private String checkString(Properties p, String name, String def) {

        String s = (String)p.getOrDefault(name, def);

        if(s == null || s == "") {

            LogPrinter.printToConsole(new Log(

                "ConfigurationProvider.checkString > property: " + name + " cannot be null nor empty. The default will be used",
                LogLevel.WARNING
            ));

            return(def);
        }

        return(s);
    }

    private LogLevel checkLogLevel(Properties p, String name, String def) {

        String s = (String)p.getOrDefault(name, def);

        if(s.equalsIgnoreCase("DEBUG")) return(LogLevel.DEBUG);
        if(s.equalsIgnoreCase("INFO")) return(LogLevel.INFO);
        if(s.equalsIgnoreCase("SUCCESS")) return(LogLevel.SUCCESS);
        if(s.equalsIgnoreCase("NOTE")) return(LogLevel.NOTE);
        if(s.equalsIgnoreCase("WARNING")) return(LogLevel.WARNING);
        if(s.equalsIgnoreCase("ERROR")) return(LogLevel.ERROR);

        LogPrinter.printToConsole(new Log(

            "ConfigurationProvider.checkLogLevel > illegal value for property: " + name +
            ". Expected: DEBUG, INFO, SUCCESS, NOTE, WARNING or ERROR (ignoring case). The default will be used",
            LogLevel.WARNING
        ));

        return(LogLevel.valueOf(def));
    }

    //____________________________________________________________________________________________________________________________________
}
