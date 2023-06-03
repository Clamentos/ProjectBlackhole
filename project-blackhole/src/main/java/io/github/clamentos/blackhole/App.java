// mvn compile exec:java -Dexec.mainClass="io.github.clamentos.blackhole.App"
package io.github.clamentos.blackhole;

import java.io.IOException;

import io.github.clamentos.blackhole.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;

/**
 * Main App class, just some random stuff for now...
 */
public class App {

    public static void main(String[] args) throws IOException {
        
        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());

        Logger logger = new Logger(LogLevel.DEBUG);
        logger.addFilePath("testLog.log");

        logger.log("Messaggio di prova", LogLevel.DEBUG);
        logger.log("Messaggio di prova", LogLevel.INFO);
        logger.log("Messaggio di prova", LogLevel.NOTE);
        logger.log("Messaggio di prova", LogLevel.SUCCESS);
        logger.log("Messaggio di prova", LogLevel.WARNING);
        logger.log("Messaggio di prova", LogLevel.ERROR);

        logger.log("Messaggio di prova", LogLevel.DEBUG, "testLog");
        logger.log("Messaggio di prova", LogLevel.INFO, "testLog");
        logger.log("Messaggio di prova", LogLevel.NOTE, "testLog");
        logger.log("Messaggio di prova", LogLevel.SUCCESS, "testLog");
        logger.log("Messaggio di prova", LogLevel.WARNING, "testLog");
        logger.log("Messaggio di prova", LogLevel.ERROR, "testLog");

        logger.stop(true);

        // test the global exc handler
        throw new ArithmeticException("Main failed to do calculations");
    }
}
