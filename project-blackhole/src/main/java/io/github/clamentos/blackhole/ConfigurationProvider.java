package io.github.clamentos.blackhole;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.web.Servlet;
import io.github.clamentos.blackhole.web.servlets.UserServlet;

//________________________________________________________________________________________________________________________________________

/**
 * Global configuration constants.
*/
public class ConfigurationProvider {
    
    public static final LogLevel MINIMUM_LOG_LEVEL = LogLevel.INFO;
    public static final int OFFER_TIMEOUT = 1_000;
    public static final int MAX_LOG_QUEUE_SIZE = 10_000;

    public static final int SERVER_PORT = 8080;
    public static final int REQUEST_WORKERS = 4;
    public static final int CONNECTION_TIMEOUT = 1000;
    public static final int MAX_REQUEST_QUEUE_SIZE = 1_000_000;

    public static final String DB_URL = "...";
    public static final String DB_USERNAME = "...";
    public static final String DB_PASWORD = "...";

    public static final Servlet[] SERVLETS = {new UserServlet()};

    //____________________________________________________________________________________________________________________________________
}
