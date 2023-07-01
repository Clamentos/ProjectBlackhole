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
 * <p>This class will read (just after class loading) the configuration file in /resources.
 * If no file or configurations are defined, then defaults will be used.</p>
*/
public class ConfigurationProvider {

    //____________________________________________________________________________________________________________________________________

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
    public static int MAX_CONNECTION_TIMEOUT = 1_000;
    public static int MAX_REQUEST_QUEUE_SIZE = 1_000_000;
    public static int MAX_SERVER_START_RETRIES = 5;

    public static long SESSION_DURATION = 14_400_000;

    public static boolean NEED_SESSION_FOR_TAG_CREATE = true;
    public static boolean NEED_SESSION_FOR_TAG_READ = true;
    public static boolean NEED_SESSION_FOR_TAG_UPDATE = true;
    public static boolean NEED_SESSION_FOR_TAG_DELETE = true;

    //____________________________________________________________________________________________________________________________________

    public static String DB_URL = null;
    public static String DB_USERNAME = null;
    public static String DB_PASWORD = null;

    public static int DB_CONNECTIONS = 1;
    public static int MAX_DB_CONNECTION_RETRIES = 5;
    public static int MAX_DB_CONNECTION_TIMEOUT = 1_000;

    public static boolean INIT_SCHEMA = false;
    public static String SCHEMA_PATH = null;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is NOT thread safe and should be called at the beginning of everything.</b></p>
     * This method will initialize all the configuration constants.
    */
    public static void init() {

        try {

            Properties prop = new Properties();

            prop.load(Files.newInputStream(Paths.get("resources/Application.properties")));
            LogPrinter.printToConsole("Loaded resources/Application.properties", LogLevel.SUCCESS);

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
            MAX_CONNECTION_TIMEOUT = Integer.parseInt((String)prop.getOrDefault("MAX_CONNECTION_TIMEOUT", "1000"));
            MAX_REQUEST_QUEUE_SIZE = Integer.parseInt((String)prop.getOrDefault("MAX_REQUEST_QUEUE_SIZE", "1000000"));
            MAX_SERVER_START_RETRIES = Integer.parseInt((String)prop.getOrDefault("MAX_SERVER_START_RETRIES", "5"));

            SESSION_DURATION = Long.parseLong((String)prop.getOrDefault("SESSION_DURATION", "14400000"));

            NEED_SESSION_FOR_TAG_CREATE = Boolean.parseBoolean((String)prop.getOrDefault("NEED_SESSION_FOR_TAG_CREATE", "false"));
            NEED_SESSION_FOR_TAG_READ = Boolean.parseBoolean((String)prop.getOrDefault("NEED_SESSION_FOR_TAG_READ", "false"));
            NEED_SESSION_FOR_TAG_UPDATE = Boolean.parseBoolean((String)prop.getOrDefault("NEED_SESSION_FOR_TAG_UPDATE", "false"));
            NEED_SESSION_FOR_TAG_DELETE = Boolean.parseBoolean((String)prop.getOrDefault("NEED_SESSION_FOR_TAG_DELETE", "false"));

            DB_URL = (String)prop.getOrDefault("DB_URL", null);
            DB_USERNAME = (String)prop.getOrDefault("DB_USERNAME", null);
            DB_PASWORD = (String)prop.getOrDefault("DB_PASWORD", null);
            DB_CONNECTIONS = Integer.parseInt((String)prop.getOrDefault("DB_CONNECTIONS", "1"));
            MAX_DB_CONNECTION_RETRIES = Integer.parseInt((String)prop.getOrDefault("MAX_DB_CONNECTION_RETRIES", "1"));
            MAX_DB_CONNECTION_TIMEOUT = Integer.parseInt((String)prop.getOrDefault("MAX_DB_CONNECTION_TIMEOUT", "1000"));
            INIT_SCHEMA = Boolean.parseBoolean((String)prop.getOrDefault("INIT_SCHEMA", "false"));
            SCHEMA_PATH = (String)prop.getOrDefault("SCHEMA_PATH", null);
            
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
            
            LogPrinter.printToConsole("Could not initialize, the defaults will be used. " + exc.getClass().getSimpleName() + ": " + exc.getMessage(), LogLevel.NOTE);
        }

        Field[] fields = ConfigurationProvider.class.getFields();

        try {

            int amt;
            String padding;

            for(Field field : fields) {

                amt = 27 - field.getName().length();
                padding = " ".repeat(amt);
                
                LogPrinter.printToConsole("Property: " + field.getName() + padding + "    Value: " + field.get(null), LogLevel.INFO);
            }
        }

        // should never happen...
        // if it somewhat fails, not a big deal...
        catch(IllegalAccessException exc) {

            LogPrinter.printToConsole("Could not access property to print it. IllegalAccessException: " + exc.getMessage(), LogLevel.NOTE);
        }
    }

    //____________________________________________________________________________________________________________________________________
}
