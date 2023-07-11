// maybe make the fields read-only?
package io.github.clamentos.blackhole.common.config;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.LogPrinter;

import java.io.IOException;

import java.lang.reflect.Field;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import java.util.Properties;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Global configuration constants.<p/>
 * <p>Use the method {@link ConfigurationProvider#init}
 * to read the configuration file in the directory {@code /resources}.
 * If no file or configurations are defined, then the defaults will be used.</p>
*/
public class ConfigurationProvider {

    public static int QUEUE_POLL_LIMIT = 100;

    public static int NUM_LOG_WORKERS = 1;
    public static int MAX_LOG_QUEUE_SIZE = 10_000;
    public static int MAX_LOG_FILE_SIZE = 10_000_000;

    public static LogLevel MIN_CONSOLE_LOG_LEVEL = LogLevel.INFO;

    public static boolean DEBUG_LEVEL_TO_FILE = false;
    public static boolean INFO_LEVEL_TO_FILE = false;
    public static boolean SUCCESS_LEVEL_TO_FILE = false;
    public static boolean NOTE_LEVEL_TO_FILE = false;
    public static boolean WARNING_LEVEL_TO_FILE = false;
    public static boolean ERROR_LEVEL_TO_FILE = false;

    //____________________________________________________________________________________________________________________________________

    public static int SERVER_PORT = 8080;
    public static int NUM_REQUEST_WORKERS = 1;
    public static int CONNECTION_TIMEOUT = 5_000;
    public static int MAX_REQUEST_QUEUE_SIZE = 1_000_000;
    public static int MAX_SERVER_START_RETRIES = 5;
    public static int STREAM_BUFFER_SIZE = 1_000;

    public static long SESSION_DURATION = 14_400_000;    // 4h in ms

    public static boolean NEED_SESSION_FOR_TAG_CREATE = true;
    public static boolean NEED_SESSION_FOR_TAG_READ = true;
    public static boolean NEED_SESSION_FOR_TAG_UPDATE = true;
    public static boolean NEED_SESSION_FOR_TAG_DELETE = true;
    //...

    //____________________________________________________________________________________________________________________________________

    public static String DB_URL = null;
    public static String DB_USERNAME = null;
    public static String DB_PASSWORD = null;

    public static int DB_CONNECTIONS = 1;
    public static int MAX_DB_CONNECTION_RETRIES = 5;
    public static int DB_CONNECTION_TIMEOUT = 5_000;

    public static boolean INIT_SCHEMA = false;
    public static boolean LOAD_DATA_TO_DB = false;
    public static String SCHEMA_PATH = "resources/Schema.sql";
    public static String DB_DATA_PATH = "resources/Data.sql";

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * <p><b>{@code init()} MUST be called at the beginning of everything in the main.</b></p>
     * This method will initialize all the configuration constants
     * from the configuration file in {@code /resources}.
    */
    public static void init() {

        try {

            Properties prop = new Properties();

            prop.load(Files.newInputStream(Paths.get("resources/Application.properties")));
            LogPrinter.printToConsole("Loaded resources/Application.properties", LogLevel.SUCCESS);

            QUEUE_POLL_LIMIT = Integer.parseInt((String)prop.getOrDefault("QUEUE_POLL_LIMIT", "100"));

            NUM_LOG_WORKERS = Integer.parseInt((String)prop.getOrDefault("NUM_LOG_WORKERS", "1"));
            MAX_LOG_QUEUE_SIZE = Integer.parseInt((String)prop.getOrDefault("MAX_LOG_QUEUE_SIZE", "10000"));
            MAX_LOG_FILE_SIZE = Integer.parseInt((String)prop.getOrDefault("MAX_LOG_FILE_SIZE", "10000000"));
            DEBUG_LEVEL_TO_FILE = Boolean.parseBoolean((String)prop.getOrDefault("DEBUG_LEVEL_TO_FILE", "false"));
            INFO_LEVEL_TO_FILE = Boolean.parseBoolean((String)prop.getOrDefault("INFO_LEVEL_TO_FILE", "false"));
            SUCCESS_LEVEL_TO_FILE = Boolean.parseBoolean((String)prop.getOrDefault("SUCCESS_LEVEL_TO_FILE", "false"));
            NOTE_LEVEL_TO_FILE = Boolean.parseBoolean((String)prop.getOrDefault("NOTE_LEVEL_TO_FILE", "false"));
            WARNING_LEVEL_TO_FILE = Boolean.parseBoolean((String)prop.getOrDefault("WARNING_LEVEL_TO_FILE", "false"));
            ERROR_LEVEL_TO_FILE = Boolean.parseBoolean((String)prop.getOrDefault("ERROR_LEVEL_TO_FILE", "false"));

            SERVER_PORT = Integer.parseInt((String)prop.getOrDefault("SERVER_PORT", "8080"));
            NUM_REQUEST_WORKERS = Integer.parseInt((String)prop.getOrDefault("NUM_REQUEST_WORKERS", "1"));
            CONNECTION_TIMEOUT = Integer.parseInt((String)prop.getOrDefault("CONNECTION_TIMEOUT", "5000"));
            MAX_REQUEST_QUEUE_SIZE = Integer.parseInt((String)prop.getOrDefault("MAX_REQUEST_QUEUE_SIZE", "1000000"));
            MAX_SERVER_START_RETRIES = Integer.parseInt((String)prop.getOrDefault("MAX_SERVER_START_RETRIES", "5"));
            STREAM_BUFFER_SIZE = Integer.parseInt((String)prop.getOrDefault("STREAM_BUFFER_SIZE", "1000"));

            SESSION_DURATION = Long.parseLong((String)prop.getOrDefault("SESSION_DURATION", "14400000"));

            NEED_SESSION_FOR_TAG_CREATE = Boolean.parseBoolean((String)prop.getOrDefault("NEED_SESSION_FOR_TAG_CREATE", "false"));
            NEED_SESSION_FOR_TAG_READ = Boolean.parseBoolean((String)prop.getOrDefault("NEED_SESSION_FOR_TAG_READ", "false"));
            NEED_SESSION_FOR_TAG_UPDATE = Boolean.parseBoolean((String)prop.getOrDefault("NEED_SESSION_FOR_TAG_UPDATE", "false"));
            NEED_SESSION_FOR_TAG_DELETE = Boolean.parseBoolean((String)prop.getOrDefault("NEED_SESSION_FOR_TAG_DELETE", "false"));

            DB_URL = (String)prop.getOrDefault("DB_URL", null);
            DB_USERNAME = (String)prop.getOrDefault("DB_USERNAME", null);
            DB_PASSWORD = (String)prop.getOrDefault("DB_PASSWORD", null);
            DB_CONNECTIONS = Integer.parseInt((String)prop.getOrDefault("DB_CONNECTIONS", "1"));
            MAX_DB_CONNECTION_RETRIES = Integer.parseInt((String)prop.getOrDefault("MAX_DB_CONNECTION_RETRIES", "1"));
            DB_CONNECTION_TIMEOUT = Integer.parseInt((String)prop.getOrDefault("DB_CONNECTION_TIMEOUT", "5000"));
            INIT_SCHEMA = Boolean.parseBoolean((String)prop.getOrDefault("INIT_SCHEMA", "false"));
            LOAD_DATA_TO_DB = Boolean.parseBoolean((String)prop.getOrDefault("LOAD_DATA_TO_DB", "false"));
            SCHEMA_PATH = (String)prop.getOrDefault("SCHEMA_PATH", "resources/Schema.sql");
            DB_DATA_PATH = (String)prop.getOrDefault("DB_DATA_PATH", "resources/Data.sql");

            switch((String)prop.getOrDefault("MIN_CONSOLE_LOG_LEVEL", "INFO")) {

                case "DEBUG": MIN_CONSOLE_LOG_LEVEL = LogLevel.DEBUG; break;
                case "INFO": MIN_CONSOLE_LOG_LEVEL = LogLevel.INFO; break;
                case "SUCCESS": MIN_CONSOLE_LOG_LEVEL = LogLevel.SUCCESS; break;
                case "NOTE": MIN_CONSOLE_LOG_LEVEL = LogLevel.NOTE; break;
                case "WARNING": MIN_CONSOLE_LOG_LEVEL = LogLevel.WARNING; break;
                case "ERROR": MIN_CONSOLE_LOG_LEVEL = LogLevel.ERROR; break;

                default: MIN_CONSOLE_LOG_LEVEL = LogLevel.INFO; break;
            }
        }

        catch(InvalidPathException | IOException exc) {
            
            LogPrinter.printToConsole("ConfigurationProvider.init > Could not initialize, the defaults will be used. " + exc.getClass().getSimpleName() + ": " + exc.getMessage(), LogLevel.NOTE);
        }

        try {

            Field[] fields = ConfigurationProvider.class.getFields();

            // just for pretty printing (27 is the longest field name)
            int amt;
            String padding;

            for(Field field : fields) {

                amt = 27 - field.getName().length();
                padding = " ".repeat(amt);
                
                LogPrinter.printToConsole("Property: " + field.getName() + padding + "    Value: " + field.get(null), LogLevel.INFO);
            }
        }

        // should never happen... if it somewhat fails, not a big deal...
        catch(IllegalAccessException exc) {

            LogPrinter.printToConsole("ConfigurationProvider.init > Could not access property to print it, IllegalAccessException: " + exc.getMessage(), LogLevel.NOTE);
        }
    }

    //____________________________________________________________________________________________________________________________________
}
