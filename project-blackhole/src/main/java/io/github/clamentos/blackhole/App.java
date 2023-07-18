// mvn compile
// mvn exec:exec
// mvn test
package io.github.clamentos.blackhole;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.common.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.common.utility.TaskManager;
import io.github.clamentos.blackhole.logging.Log;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.LogPrinter;
import io.github.clamentos.blackhole.logging.Logger;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Scanner;

//________________________________________________________________________________________________________________________________________

/**
 * Main App class, initialize and start.
*/
public class App {

    //____________________________________________________________________________________________________________________________________

    public static void main(String[] args) {

        try {

            // Print the very cool banner.
            System.out.print(Files.readString(Paths.get("resources/Banner.txt")));
        }

        // If it fails, ignore it. It's not a big deal.
        catch(IOException exc) {

            LogPrinter.printToConsole(new Log(

                "App.main 1 > Could not print the banner, IOException: " + exc.getMessage(),
                LogLevel.NOTE
            ));
        }

        //Signal.handle(new Signal("..."), signal -> {});
        Logger logger = Logger.getInstance();

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
        logger.log("App.main 2 > Application PID: " + Long.toString(ProcessHandle.current().pid()), LogLevel.INFO);

        try { // Initialize db schema (if required).

            if(ConfigurationProvider.getInstance().GEN_BD_SCHEMA == true) {

                logger.log("App.main 3 > Applying database schema...", LogLevel.INFO);
                directQuery("resources/Schema.sql");
                logger.log("App.main 4 > Database schema Applied", LogLevel.SUCCESS);
            }

            logger.log("App.main 5 > Skipping database schema application. Property is set to false", LogLevel.INFO);
        }

        catch(IOException | SQLException exc) {

            logger.log(
                        
                "App.main 6 > Could not generate schema, " +
                exc.getClass().getSimpleName() + ": " +
                exc.getMessage() + " Skipping...",
                LogLevel.WARNING
            );
        }

        try { // Initialize db data (if required).

            if(ConfigurationProvider.getInstance().INIT_DB_DATA == true) {

                logger.log("App.main 7 > Importing data to database...", LogLevel.INFO);
                directQuery("resources/Data.sql");
                logger.log("App.main 8 > Data imported", LogLevel.SUCCESS);
            }

            logger.log("App.main 9 > Skipping data import. Property is set to false", LogLevel.INFO);
        }

        catch(IOException | SQLException exc) {

            logger.log(
                        
                "App.main 10 > Could not populate the database, " +
                exc.getClass().getSimpleName() + ": " +
                exc.getMessage() + " Skipping...",
                LogLevel.WARNING
            );
        }

        TaskManager.getInstance().launchServerTask();

        String s;
        Scanner scanner = new Scanner(System.in);

        while(true) {

            LogPrinter.printToConsole(new Log("Type \"quit\" to gracefully terminate the application", LogLevel.INFO));
            s = scanner.nextLine();

            if(s.equalsIgnoreCase("quit") == true) {

                LogPrinter.printToConsole(new Log("App.main 11 > Shuting down...", LogLevel.INFO));
                TaskManager.getInstance().shutdown();

                break;
            }

            else {

                LogPrinter.printToConsole(new Log("Unknown comand", LogLevel.INFO));
            }
        }

        scanner.close();
        LogPrinter.printToConsole(new Log("App.main 12 > Shut down completed", LogLevel.SUCCESS));
    }

    //____________________________________________________________________________________________________________________________________

    private static void directQuery(String file_path) throws IOException, SQLException {

        Connection db_connection = DriverManager.getConnection(

            ConfigurationProvider.getInstance().DB_ADDRESS,
            ConfigurationProvider.getInstance().DB_USERNAME,
            ConfigurationProvider.getInstance().DB_PASSWORD
        );

        Statement sql = db_connection.createStatement();
        sql.execute(Files.readString(Paths.get(file_path)));
        db_connection.close();
    }

    //____________________________________________________________________________________________________________________________________
}