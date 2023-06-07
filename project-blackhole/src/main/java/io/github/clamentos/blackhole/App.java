// mvn compile exec:java -Dexec.mainClass="io.github.clamentos.blackhole.App"
package io.github.clamentos.blackhole;

import io.github.clamentos.blackhole.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.web.Server;

/**
 * Main App class, just some random stuff for now...
 */
public class App {

    public static void main(String[] args) {

        // set the global exception handler for uncaught exceptions
        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());

        // create and start the web server
        Server web_server = Server.getInstance();
        web_server.start();
    }
}
