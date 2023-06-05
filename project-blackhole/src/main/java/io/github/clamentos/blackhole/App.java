// mvn compile exec:java -Dexec.mainClass="io.github.clamentos.blackhole.App"
package io.github.clamentos.blackhole;

import io.github.clamentos.blackhole.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.web.Server;

import java.io.IOException;

/**
 * Main App class, just some random stuff for now...
 */
public class App {

    public static void main(String[] args) throws IOException, InterruptedException {

        // set the global exception handler for uncaught exceptions
        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
        Logger logger = Logger.getInstance();
        Server web_server = Server.getInstance();

        // if the method is successful, it will loop forever
        web_server.start();
    }
}
