package io.github.clamentos.blackhole.framework.implementation.tasks;

///
import io.github.clamentos.blackhole.framework.implementation.configuration.ConfigurationProvider;

///..
import io.github.clamentos.blackhole.framework.implementation.logging.LogLevels;
import io.github.clamentos.blackhole.framework.implementation.logging.LogPrinter;
import io.github.clamentos.blackhole.framework.implementation.logging.MetricsTracker;

///..
import io.github.clamentos.blackhole.framework.implementation.network.tasks.ServerTask;

///..
import io.github.clamentos.blackhole.framework.implementation.persistence.PersistenceException;

///..
import io.github.clamentos.blackhole.framework.implementation.persistence.pool.ConnectionPool;
import io.github.clamentos.blackhole.framework.implementation.persistence.pool.PooledConnection;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.ExceptionFormatter;
import io.github.clamentos.blackhole.framework.implementation.utility.ResourceReleaser;

///..
import io.github.clamentos.blackhole.framework.scaffolding.ApplicationContext;

///.
import java.io.IOException;

///..
import java.nio.file.Files;
import java.nio.file.Paths;

///..
import java.sql.SQLException;
import java.sql.Statement;

///..
import java.util.Scanner;

///
/**
 * <h3>Application starter</h3>
 * This is the main class and it will instantiate and initialize all the necessary resources.
 * Once done, it will wait indefinetly for the "quit" command from the command line. If the "quit" command is entered,
 * this class will begin the shutdown sequence and free all the resources.
*/
public final class ApplicationStarter {

    ///
    /**
     * Starts the whole application.
     * @param context : The user-defined application context.
     * @see ApplicationContext
    */
    public static void start(ApplicationContext context) {

        printBanner();
        LogPrinter log_printer = LogPrinter.getInstance();

        if(context == null) {

            log_printer.logToFile("ApplicationStarter.start >> The input argument cannot be null", LogLevels.FATAL);
            log_printer.close();

            System.exit(1);
        }

        ConfigurationProvider configuration_provider = ConfigurationProvider.getInstance();

        printProperties(configuration_provider, log_printer);

        MetricsTracker metrics_service = MetricsTracker.getInstance();
        ConnectionPool connection_pool = ConnectionPool.getInstance();

        log_printer.logToFile(

            "ApplicationStarter.start >> Application PID: " + Long.toString(ProcessHandle.current().pid()),
            LogLevels.INFO
        );

        try { // Initialize db schema (if required).

            if(configuration_provider.GENERATE_DATABASE_SCHEMA == true) {

                log_printer.logToFile("ApplicationStarter.start >> Applying database schema...", LogLevels.INFO);
                directQuery("resources/Schema.sql", connection_pool, log_printer);
                log_printer.logToFile("ApplicationStarter.start >> Database schema Applied", LogLevels.SUCCESS);
                metrics_service.incrementDatabaseQueriesOk(1);
            }

            else {

                log_printer.logToFile(

                    "ApplicationStarter.start >> Skipping database schema application. Property is set to false",
                    LogLevels.INFO
                );
            }
        }

        catch(IOException | SQLException | PersistenceException exc) {

            metrics_service.incrementDatabaseQueriesKo(1);

            log_printer.logToFile(

                "ApplicationStarter.start >> Could not generate schema, " +
                exc.getClass().getSimpleName() + ": " + exc.getMessage() + " >> Skipping...",
                LogLevels.WARNING
            );
        }

        try { // Initialize db data (if required).

            if(configuration_provider.INITIALIZE_DATABASE_DATA == true) {

                log_printer.logToFile("ApplicationStarter.start >> Importing data to database...", LogLevels.INFO);
                directQuery("resources/Data.sql", connection_pool, log_printer);
                log_printer.logToFile("ApplicationStarter.start >> Data imported", LogLevels.SUCCESS);
                metrics_service.incrementDatabaseQueriesOk(1);
            }

            else {

                log_printer.logToFile(
                
                    "ApplicationStarter.start >> Skipping data import. Property is set to false",
                    LogLevels.INFO
                );
            }
        }

        catch(IOException | SQLException | PersistenceException exc) {

            metrics_service.incrementDatabaseQueriesKo(1);

            log_printer.logToFile(

                "ApplicationStarter.start > Could not populate the database, " +
                exc.getClass().getSimpleName() + ": " + exc.getMessage() + " Skipping...",
                LogLevels.WARNING
            );
        }

        // Start the server.
        try {

            TaskManager.getInstance().launchThread(new ServerTask(context), "ServerTask");
        }

        catch(IOException exc) {

            log_printer.logToFile(
                
                "ApplicationStarter.start > Could not instantiate the server task, IOException: " +
                exc.getMessage() + " aborting", LogLevels.FATAL
            );

            log_printer.close();
            connection_pool.closePool();

            System.exit(1);
        }

        // Wait for the user to type "quit".
        Scanner scanner = new Scanner(System.in);
        String s;

        while(true) {

            log_printer.logToConsole("Type \"quit\" to gracefully terminate the application", LogLevels.INFO);
            s = scanner.nextLine();

            if(s.equalsIgnoreCase("quit") == true) {

                log_printer.logToFile("ApplicationStarter.start >> Shutting down...", LogLevels.INFO);
                TaskManager.getInstance().shutdown();

                break;
            }

            else {

                log_printer.logToConsole("Unknown comand: \"" + s + "\"", LogLevels.INFO);
            }
        }

        scanner.close();
        connection_pool.closePool();
        log_printer.logToFile("ApplicationStarter.start >> Shut down successfull", LogLevels.SUCCESS);
        log_printer.close();
    }

    ///
    // Perform a query aquiring the connection from the pool and then releasing it.
    private static void directQuery(String file_path, ConnectionPool connection_pool, LogPrinter log_printer) throws IOException, SQLException, PersistenceException {

        Statement sql;
        PooledConnection connection = connection_pool.aquireConnection(0);

        connection.refreshConnection();
        sql = connection.getConnection().createStatement();
        sql.execute(Files.readString(Paths.get(file_path)));
        ResourceReleaser.release(log_printer, "ApplicationStarter.directQuery", sql);
        connection_pool.releaseConnection(0, connection);
    }

    ///..
    private static void printProperties(ConfigurationProvider configuration_provider, LogPrinter log_printer) {

        String[] properties;

        try {

            properties = configuration_provider.getPropertiesToLog();
        }

        // This should never happen...
        catch(IllegalAccessException exc) {

            log_printer.logToFile(ExceptionFormatter.format("ConfigurationProvider.printFields >> ", exc, ""), LogLevels.FATAL);
            System.exit(1);

            return;
        }

        for(String property : properties) {

            log_printer.logToFile(property, LogLevels.INFO);
        }
    }

    ///..
    // Simple ASCII art graphics.
    private static void printBanner() {

        System.out.print(
            
                                                                                     "\r\n" +    
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
            "..                                                                       \r\n"
        );

        System.out.println(
            
            "  _____           _           _     ____  _            _    _           _      \n" + //
            " |  __ \\         (_)         | |   |  _ \\| |          | |  | |         | |     \n" + //
            " | |__) | __ ___  _  ___  ___| |_  | |_) | | __ _  ___| | _| |__   ___ | | ___ \n" + //
            " |  ___/ '__/ _ \\| |/ _ \\/ __| __| |  _ <| |/ _` |/ __| |/ / '_ \\ / _ \\| |/ _ \\\n" + //
            " | |   | | | (_) | |  __/ (__| |_  | |_) | | (_| | (__|   <| | | | (_) | |  __/\n" + //
            " |_|   |_|  \\___/| |\\___|\\___|\\__| |____/|_|\\__,_|\\___|_|\\_\\_| |_|\\___/|_|\\___|\n" + //
            "                _/ |                                                           \n" + //
            "               |__/                                                            \n"
        );
    }

    ///
}
