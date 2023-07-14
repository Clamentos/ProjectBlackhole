// mvn compile exec:java -Dexec.mainClass="io.github.clamentos.blackhole.App"
package io.github.clamentos.blackhole;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.web.server.Server;

//________________________________________________________________________________________________________________________________________

/**
 * Main App class, initialize and start
*/
public class App {

    //____________________________________________________________________________________________________________________________________

    public static void main(String[] args) {

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
        Server server = Server.getInstance();
        Thread t = server.start();

        try {

            t.join();
        }

        catch(InterruptedException exc) {

            System.out.println("interrupted in main");
        }

        /*try {

            ConfigurationProvider.init();
            Container.init();
            Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());

            // initialize db schema
            if(ConfigurationProvider.INIT_SCHEMA == true) {

                try {

                    String query = Files.readString(Paths.get(ConfigurationProvider.SCHEMA_PATH));
                    directQuery(query);
                }

                catch(Exception exc) {

                    LogPrinter.printToConsole(
                        
                        "Could not initialize schema, " +
                        exc.getClass().getSimpleName() + ": " +
                        exc.getMessage() + ", skipping...",
                        LogLevel.WARNING
                    );
                }
            }

            // load data into db
            if(ConfigurationProvider.LOAD_DATA_TO_DB == true) {

                try {

                    String query = Files.readString(Paths.get(ConfigurationProvider.DB_DATA_PATH));
                    directQuery(query);
                }

                catch(Exception exc) {

                    LogPrinter.printToConsole(
                        
                        "Could not populate the db with data, " + exc.getClass().getSimpleName() + ": " + exc.getMessage() + ", skipping...", LogLevel.WARNING
                    );
                }
            }

            //Container.web_server.start();
            Container.web_server.testNIO();
        }

        catch(Exception exc) {

            LogPrinter.printToConsole("App.main > Could not fully start the app, " + exc.getClass().getName() + ": " + exc.getMessage(), LogLevel.ERROR);
            System.exit(1);
        }*/
    }

    //____________________________________________________________________________________________________________________________________

    /*private static void directQuery(String query) throws SQLException {

        Connection db_connection = DriverManager.getConnection(

            ConfigurationProvider.DB_URL,
            ConfigurationProvider.DB_USERNAME,
            ConfigurationProvider.DB_PASSWORD
         );

        Statement sql = db_connection.createStatement();
        sql.execute(query);
        db_connection.close();
    }*/

    //____________________________________________________________________________________________________________________________________
}
