// mvn compile
// mvn exec:exec
// mvn test
package io.github.clamentos.blackhole;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.common.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.framework.tasks.TaskManager;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.LogPrinter;

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

        try { // Print the very cool banner.

            System.out.print(Files.readString(Paths.get("resources/Banner.txt")));
        }

        catch(IOException exc) { // If it fails, simply log it. It's not a big deal.

            LogPrinter log_printer = LogPrinter.getInstance();

            log_printer.log(
                
                "App.main > Failed to print the banner",
                LogLevel.NOTE
            );
        }

        ConfigurationProvider configuration_provider = ConfigurationProvider.getInstance();
        LogPrinter log_printer = LogPrinter.getInstance();

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
        log_printer.log(
            
            "App.main > Application PID: " + Long.toString(ProcessHandle.current().pid()),
            LogLevel.INFO
        );

        try { // Initialize db schema (if required).

            if(configuration_provider.GEN_BD_SCHEMA == true) {

                log_printer.log("App.main > Applying database schema...", LogLevel.INFO);
                directQuery("resources/Schema.sql");
                log_printer.log("App.main > Database schema Applied", LogLevel.SUCCESS);
            }

            log_printer.log(
                
                "App.main > Skipping database schema application. Property is set to false",
                LogLevel.INFO
            );
        }

        catch(IOException | SQLException exc) {

            log_printer.log(
                        
                "App.main > Could not generate schema, " +
                exc.getClass().getSimpleName() + ": " +
                exc.getMessage() + " Skipping...",
                LogLevel.WARNING
            );
        }

        try { // Initialize db data (if required).

            if(configuration_provider.INIT_DB_DATA == true) {

                log_printer.log("App.main > Importing data to database...", LogLevel.INFO);
                directQuery("resources/Data.sql");
                log_printer.log("App.main > Data imported", LogLevel.SUCCESS);
            }

            log_printer.log("App.main > Skipping data import. Property is set to false", LogLevel.INFO);
        }

        catch(IOException | SQLException exc) {

            log_printer.log(
                        
                "App.main > Could not populate the database, " +
                exc.getClass().getSimpleName() + ": " +
                exc.getMessage() + " Skipping...",
                LogLevel.WARNING
            );
        }

        TaskManager.getInstance().launchServerTask();    // Start the server.

        // Wait for the user to quit.

        String s;
        Scanner scanner = new Scanner(System.in);

        while(true) {

            log_printer.log("Type \"quit\" to gracefully terminate the application", LogLevel.INFO);
            s = scanner.nextLine();

            if(s.equalsIgnoreCase("quit") == true) {

                log_printer.log("App.main > Shuting down...", LogLevel.INFO);
                TaskManager.getInstance().shutdown();

                break;
            }

            else {

                log_printer.log("Unknown comand: \"" + s + "\"", LogLevel.INFO);
            }
        }

        scanner.close();
        log_printer.log("App.main > Shut down successfull", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    // Thread safe.
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

// TODO: IMPORTANT! modify ConnectionTask & RequestTask for big data (use stream)
// TODO: update the javadocs on the code