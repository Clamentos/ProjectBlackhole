package io.github.clamentos.blackhole.web;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.config.ConfigurationProvider;
import io.github.clamentos.blackhole.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.LogPrinter;
import io.github.clamentos.blackhole.logging.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.net.Socket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.concurrent.LinkedBlockingQueue;

//________________________________________________________________________________________________________________________________________

/**
 * Worker thread dedicated to handle queued requests.
*/
public class RequestWorker extends Thread {

    private final Logger LOGGER;

    private LinkedBlockingQueue<Socket> socket_queue;
    private Connection db_connection;
    private Dispatcher dispatcher;

    //____________________________________________________________________________________________________________________________________

    /**
     * Instantiates a new request worker.
     * @param name : The worker name, used for identification in logs.
     * @param socket_queue : The socket queue. 
     */
    public RequestWorker(String name, LinkedBlockingQueue<Socket> socket_queue) {

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
        Thread.currentThread().setName(name);

        LOGGER = Logger.getInstance();

        this.socket_queue = socket_queue;
        dispatcher = Dispatcher.getInstance();
    }

    //____________________________________________________________________________________________________________________________________

    @Override
    public void run() {

        DataInputStream in;
        DataOutputStream out;
        Socket socket;

        if(attempt(ConfigurationProvider.MAX_DB_CONNECTION_RETRIES) == true) {

            LOGGER.log("Database connection was successful", LogLevel.SUCCESS);

            while(true) {

                try {
    
                    socket = socket_queue.take();
                    in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    refreshIfNeeded();
                    dispatcher.dispatch(in, out, db_connection);
                }

                catch(Exception exc) {

                    LOGGER.log("Could not dispatch the request, " + exc.getClass().getCanonicalName() + ": " + exc.getMessage(), LogLevel.WARNING);
                }
            }
        }
    }

    //____________________________________________________________________________________________________________________________________

    private boolean attempt(int retries) {

        for(int i = 0; i < retries; i++) {

            try {

                db_connection = DriverManager.getConnection(
    
                    ConfigurationProvider.DB_URL,
                    ConfigurationProvider.DB_USERNAME,
                    ConfigurationProvider.DB_PASWORD
                );

                return(true);
            }

            catch(SQLException exc) {
    
                LogPrinter.printToConsole("Could not connect to the database, SQLException: " + exc.getMessage(), LogLevel.WARNING);
            }

            try {

                Thread.sleep(1000);
            }

            catch(InterruptedException exc) {

                LOGGER.log("Interrupted while waiting on retries, InterruptedException: " + exc.getMessage(), LogLevel.INFO);
            }
        }

        LOGGER.log("Retries exhausted", LogLevel.ERROR);
        return(false);
    }

    private void refreshIfNeeded() throws SQLException {

        if(db_connection.isValid(ConfigurationProvider.MAX_DB_CONNECTION_TIMEOUT) == false) {

            db_connection = DriverManager.getConnection(
    
                ConfigurationProvider.DB_URL,
                ConfigurationProvider.DB_USERNAME,
                ConfigurationProvider.DB_PASWORD
            );
        }
    }

    //____________________________________________________________________________________________________________________________________
}
