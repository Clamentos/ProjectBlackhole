// mvn compile exec:java -Dexec.mainClass="io.github.clamentos.blackhole.App"
package io.github.clamentos.blackhole;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.config.ConfigurationProvider;
import io.github.clamentos.blackhole.common.config.Container;
import io.github.clamentos.blackhole.common.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.LogPrinter;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import java.security.NoSuchAlgorithmException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

//________________________________________________________________________________________________________________________________________

/**
 * Main App class, initialize and start
 */
public class App {

    //____________________________________________________________________________________________________________________________________

    public static void main(String[] args) {

        try {

            ConfigurationProvider.init();
            Container.init();
            Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());

            // initialize db schema
            if(ConfigurationProvider.INIT_SCHEMA) {

                try {

                    Connection db_connection = DriverManager.getConnection(

                        ConfigurationProvider.DB_URL,
                        ConfigurationProvider.DB_USERNAME,
                        ConfigurationProvider.DB_PASWORD
                    );

                    Statement sql = db_connection.createStatement();
                    sql.execute(Files.readString(Paths.get(ConfigurationProvider.SCHEMA_PATH)));
                    db_connection.close();
                }

                catch(SQLException | InvalidPathException | IOException exc) {

                    LogPrinter.printToConsole("Could not initialize schema, " + exc.getClass().getSimpleName() + ": " + exc.getMessage() + " skipping...", LogLevel.WARNING);
                }
            }

            Container.web_server.start();
        }

        catch(NoSuchAlgorithmException exc) {

            LogPrinter.printToConsole("Could not fully start the app, NoSuchAlgorithmException: " + exc.getMessage(), LogLevel.ERROR);
            System.exit(1);
        }
    }

    //____________________________________________________________________________________________________________________________________
}
