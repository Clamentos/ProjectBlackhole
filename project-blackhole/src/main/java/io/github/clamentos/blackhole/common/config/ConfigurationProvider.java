package io.github.clamentos.blackhole.common.config;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.LogPrinter;

import java.lang.reflect.Field;

import java.nio.file.Files;
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

    public static boolean NEED_SESSION_FOR_TAG_CREATE = true;
    public static boolean NEED_SESSION_FOR_TAG_READ = true;
    public static boolean NEED_SESSION_FOR_TAG_UPDATE = true;
    public static boolean NEED_SESSION_FOR_TAG_DELETE = true;

    //____________________________________________________________________________________________________________________________________

    public static String DB_URL = null; //"jdbc:postgresql://localhost:5432/db_prova";
    public static String DB_USERNAME = null; //"admin";
    public static String DB_PASWORD = null; //"admin";

    public static int DB_CONNECTIONS = 1;
    public static int MAX_DB_CONNECTION_RETRIES = 5;
    public static int MAX_DB_CONNECTION_TIMEOUT = 1_000;

    public static boolean INIT_SCHEMA = false;
    public static String SCHEMA_PATH = null;

    //____________________________________________________________________________________________________________________________________

    {
        try {

            Properties prop = new Properties();

            prop.load(Files.newInputStream(Paths.get("resources/Application.properties")));
            LogPrinter.printToConsole("Loaded resources/Application.properties", LogLevel.INFO);

            NUM_LOG_WORKERS = (int)prop.get("NUM_LOG_WORKERS");
            MAX_LOG_QUEUE_SIZE = (int)prop.get("MAX_LOG_QUEUE_SIZE");
            MAX_LOG_FILE_SIZE = (int)prop.get("MAX_LOG_FILE_SIZE");
            MIN_CONSOLE_LOG_LEVEL = (LogLevel)prop.get("MIN_CONSOLE_LOG_LEVEL");
            DEBUG_LEVEL_TO_FILE = (boolean)prop.get("DEBUG_LEVEL_TO_FILE");
            INFO_LEVEL_TO_FILE = (boolean)prop.get("INFO_LEVEL_TO_FILE");
            SUCCESS_LEVEL_TO_FILE = (boolean)prop.get("SUCCESS_LEVEL_TO_FILE");
            NOTE_LEVEL_TO_FILE = (boolean)prop.get("NOTE_LEVEL_TO_FILE");
            WARNING_LEVEL_TO_FILE = (boolean)prop.get("WARNING_LEVEL_TO_FILE");
            ERROR_LEVEL_TO_FILE = (boolean)prop.get("ERROR_LEVEL_TO_FILE");

            SERVER_PORT = (int)prop.get("SERVER_PORT");
            NUM_REQUEST_WORKERS = (int)prop.get("NUM_REQUEST_WORKERS");
            MAX_CONNECTION_TIMEOUT = (int)prop.get("MAX_CONNECTION_TIMEOUT");
            MAX_REQUEST_QUEUE_SIZE = (int)prop.get("MAX_REQUEST_QUEUE_SIZE");
            MAX_SERVER_START_RETRIES = (int)prop.get("MAX_SERVER_START_RETRIES");

            NEED_SESSION_FOR_TAG_CREATE = (boolean)prop.get("NEED_SESSION_FOR_TAG_CREATE");
            NEED_SESSION_FOR_TAG_READ = (boolean)prop.get("NEED_SESSION_FOR_TAG_READ");
            NEED_SESSION_FOR_TAG_UPDATE = (boolean)prop.get("NEED_SESSION_FOR_TAG_UPDATE");
            NEED_SESSION_FOR_TAG_DELETE = (boolean)prop.get("NEED_SESSION_FOR_TAG_DELETE");

            DB_URL = (String)prop.get("DB_URL");
            DB_USERNAME = (String)prop.get("DB_USERNAME");
            DB_PASWORD = (String)prop.get("DB_PASWORD");
            DB_CONNECTIONS = (int)prop.get("DB_CONNECTIONS");
            MAX_DB_CONNECTION_RETRIES = (int)prop.get("MAX_DB_CONNECTION_RETRIES");
            MAX_DB_CONNECTION_TIMEOUT = (int)prop.get("MAX_DB_CONNECTION_TIMEOUT");
            INIT_SCHEMA = (boolean)prop.get("INIT_SCHEMA");
            SCHEMA_PATH = (String)prop.get("SCHEMA_PATH");
        }

        catch(Exception exc) {
            
            LogPrinter.printToConsole("Could not initialize, the defaults will be used. " + exc.getClass().getSimpleName() + ": " + exc.getMessage(), LogLevel.WARNING);
        }

        Field[] fields = ConfigurationProvider.class.getFields();

        try {

            for(Field field : fields) {

                LogPrinter.printToConsole("Property: " + field.getName() + " Value: " + field.get(null), LogLevel.INFO);
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
