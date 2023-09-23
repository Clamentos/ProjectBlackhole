// mvn compile
// mvn exec:exec
// mvn test
package io.github.clamentos.blackhole;

///
import io.github.clamentos.blackhole.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.LogPrinter;
import io.github.clamentos.blackhole.scaffolding.tasks.TaskManager;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Scanner;

///
/**
 * Main App class, initialize and start.
*/
public class App {

    ///
    public static void main(String[] args) {

        printBanner();
        
        ConfigurationProvider configuration_provider = ConfigurationProvider.getInstance();
        LogPrinter log_printer = LogPrinter.getInstance();

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
        log_printer.log(
            
            "App.main > Application PID: " + Long.toString(ProcessHandle.current().pid()),
            LogLevel.INFO
        );

        try { // Initialize db schema (if required).

            if(configuration_provider.GEN_DB_SCHEMA == true) {

                log_printer.log("App.main > Applying database schema...", LogLevel.INFO);
                directQuery("resources/Schema.sql");
                log_printer.log("App.main > Database schema Applied", LogLevel.SUCCESS);
            }

            else {

                log_printer.log(
                
                    "App.main > Skipping database schema application. Property is set to false",
                    LogLevel.INFO
                );
            }
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

            else {

                log_printer.log(
                
                    "App.main > Skipping data import. Property is set to false",
                    LogLevel.INFO
                );
            }
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

    ///
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

    private static void printBanner() {

        System.out.println("\r\n" + //
                
            "                               ................                          \r\n" +
            "                             ...',,;::ccc:;;,'.................          \r\n" +
            "                        .....,:loooooooddodddolcc:,''''',;;;;,,''..'',,,'\r\n" +
            "                       ...,;:lodlc:;,''',,;:loxkkkxolc:;::cooollllllcc;,.\r\n" +
            "                    ....,;cool;,....      ...':ok0KKOxddoodxkkkxdol:,..  \r\n" +
            "                 .....,:ldxd:'..              .,cd0XX0OOkkkkkxdl:,..     \r\n" +
            "               .....';lxO0xc'.                ..,cx0KK00Okxdl:,..        \r\n" +
            "              .....';cx0X0o;.              ...';ldO0000Okdc;'..          \r\n" +
            "             .',,'',:dOKKk:'.         ....';codxkxkkOKKOdc,..            \r\n" +
            "            ..;::;,;lx0K0xc'.     ....,:codxxxdl:;:lkKKkl,..             \r\n" +
            "           ..,:cc:::ok0XKkl,.....',:lodxkxdl:,'...':dkxo;..              \r\n" +
            "           ..;cllllloxOKK0xl::cldxxxxdoc:,'...   ..;lo:,..               \r\n" +
            "           .':lddddddxO0K00kkkkxool:;'....        .;:;'..                \r\n" +
            "          ..,:odxxxxkkOO00000ko:,'...           ..','...                 \r\n" +
            "         ..';coxkkkkkkkkkkO00Odc;'....       ..',,,....                  \r\n" +
            "       ...';codxxxdollc::cldxkxdlc:,,,''','',;;;,'...                    \r\n" +
            "     ...,;clllc:;,'.......',;;::::::cclllcc:,,'.....                     \r\n" +
            "   ..',;;;,,....            .......'',,,,''......                        \r\n" +
            " .........                         ... ......                            \r\n" +
            "..                                                                       \r\n" +
            "\r\n" +
            ""
        );
    }

    ///
}

// TODO: IMPORTANT! modify ConnectionTask for big data (use stream)
// TODO: update the javadocs on the code