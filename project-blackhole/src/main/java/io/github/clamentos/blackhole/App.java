// mvn compile exec:java -Dexec.mainClass="io.github.clamentos.blackhole.App"
package io.github.clamentos.blackhole;

import io.github.clamentos.blackhole.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.web.server.Server;

/**
 * Main App class, just some random stuff for now...
 */
public class App {

    public static void main(String[] args) {

        // set the global exception handler for uncaught exceptions
        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
        Logger LOGGER = Logger.getInstance();

        try {

            // create and start the web server
            Server web_server = Server.getInstance();
            web_server.start();
        }

        catch(Exception exc) {

            LOGGER.log("Failed to start the App " + exc.getClass().getSimpleName() + ": " + exc.getMessage(), LogLevel.ERROR);
        }
    }
}
