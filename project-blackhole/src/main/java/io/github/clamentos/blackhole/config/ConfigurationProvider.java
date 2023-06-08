package io.github.clamentos.blackhole.config;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.web.servlets.Servlet;
import io.github.clamentos.blackhole.web.servlets.UserServlet;

//________________________________________________________________________________________________________________________________________

/**
 * Global configuration constants.
*/
public class ConfigurationProvider {

    public static final LogLevel MINIMUM_CONSOLE_LOG_LEVEL = LogLevel.INFO;
    public static final int MAX_LOG_QUEUE_SIZE = 10_000;

    public static final int SERVER_PORT = 8080;
    public static final int REQUEST_WORKERS = 2;
    public static final int CONNECTION_TIMEOUT = 5_000;
    public static final int MAX_REQUEST_QUEUE_SIZE = 1_000_000;
    public static final int MAX_SERVER_START_RETRIES = 3;

    public static final Servlet[] SERVLETS = {

        UserServlet.getInstance()
    };

    public static final String DB_URL = "jdbc:postgresql://localhost:5432/db_prova";
    public static final String DB_USERNAME = "prova";
    public static final String DB_PASWORD = "prova";
    public static final int MAX_DB_CONNECTION_RETRIES = 3;
    public static final int MAX_DB_CONNECTION_TIMEOUT = 1000;

    //____________________________________________________________________________________________________________________________________
}
