// mvn compile exec:java -Dexec.mainClass="io.github.clamentos.blackhole.App"
package io.github.clamentos.blackhole;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.common.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.common.utility.TaskManager;
import io.github.clamentos.blackhole.logging.Log;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.LogPrinter;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.web.server.Server;

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

        Server server = Server.getInstance();
        Logger logger = Logger.getInstance();

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
        logger.log("App.main 1 > Application PID: " + Long.toString(ProcessHandle.current().pid()), LogLevel.NOTE);

        try { // Initialize db schema (if required).

            if(ConfigurationProvider.getInstance().GEN_BD_SCHEMA == true) {

                logger.log("App.main 2 > Applying database schema...", LogLevel.NOTE);
                directQuery("resources/Schema.sql");
                logger.log("App.main 3 > Database schema Applied", LogLevel.SUCCESS);
            }

            logger.log("App.main 4 > Skipping database schema application. Property is set to false", LogLevel.NOTE);
        }

        catch(IOException | SQLException exc) {

            logger.log(
                        
                "App.main 5 > Could not generate schema, " +
                exc.getClass().getSimpleName() + ": " +
                exc.getMessage() + ", skipping...",
                LogLevel.WARNING
            );
        }

        try { // Initialize db data (if required).

            if(ConfigurationProvider.getInstance().INIT_DB_DATA == true) {

                logger.log("App.main 6 > Importing data to database...", LogLevel.NOTE);
                directQuery("resources/Data.sql");
                logger.log("App.main 7 > Data imported", LogLevel.SUCCESS);
            }

            logger.log("App.main 8 > Skipping data import. Property is set to false", LogLevel.NOTE);
        }

        catch(IOException | SQLException exc) {

            logger.log(
                        
                "App.main 9 > Could not populate the database, " +
                exc.getClass().getSimpleName() + ": " +
                exc.getMessage() + ", skipping...",
                LogLevel.WARNING
            );
        }

        // Start it all!
        server.start();

        Scanner scanner = new Scanner(System.in);
        String s;

        while(true) {

            s = scanner.nextLine();

            if(s.equalsIgnoreCase("quit") == true) {

                LogPrinter.printToConsole(new Log("App.main 10 > Shuting down...", LogLevel.INFO));
                server.stop();
                TaskManager.getInstance().shutdown();

                break;
            }
        }

        scanner.close();
        LogPrinter.printToConsole(new Log("App.main 11 > Shut down completed", LogLevel.SUCCESS));
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

// TODO: rewrite the server task, connection task and log task condensing with the help of abstract continuous task.
// TODO: make it so that the TaskManager can start and stop the server task.
// TODO: move the xxxTask into the framework folder
// TODO: RequestTask shouldn't be stoppable... Make it normal but make it still require to use the TaskManager when quitting.