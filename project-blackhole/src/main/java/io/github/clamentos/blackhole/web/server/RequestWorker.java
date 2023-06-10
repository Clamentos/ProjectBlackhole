package io.github.clamentos.blackhole.web.server;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.config.ConfigurationProvider;
import io.github.clamentos.blackhole.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

//________________________________________________________________________________________________________________________________________

/**
 * Worker thread dedicated to handle queued requests.
*/
public class RequestWorker extends Thread {

    private final Logger LOGGER;

    private AtomicBoolean stopped;
    private BlockingQueue<Socket> socket_queue;
    private Connection db_connection;
    private Dispatcher dispatcher;

    //____________________________________________________________________________________________________________________________________

    /**
     * Instantiates a new request worker.
     * @param socket_queue : The socket queue. 
     * @throws InstantiationException if the worker fails to connect to the database.
     */
    public RequestWorker(BlockingQueue<Socket> socket_queue) throws InstantiationException {

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
        LOGGER = Logger.getInstance();
        this.socket_queue = socket_queue;
        stopped = new AtomicBoolean(true);

        if(attempt(ConfigurationProvider.MAX_DB_CONNECTION_RETRIES) == false) {

            throw new InstantiationException("Could not connect to the database");
        }

        LOGGER.log("Database connection was successful", LogLevel.SUCCESS);
        dispatcher = Dispatcher.getInstance();
    }

    //____________________________________________________________________________________________________________________________________

    @Override
    public void run() {

        DataInputStream in;
        DataOutputStream out;
        Socket socket;
        
        stopped.set(false);

        while(stopped.get() == false) {

            try {

                socket = socket_queue.take();
                in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                refreshIfNeeded();
                dispatcher.dispatch(in, out, db_connection);
                socket.close();
            }

            catch(IOException | SQLException exc) {

                LOGGER.log("Could not dispatch the request, " + exc.getClass().getSimpleName() + ": " + exc.getMessage(), LogLevel.WARNING);
            }

            catch(InterruptedException exc) {

                if(stopped.get() == true) {

                    break;
                }

                LOGGER.log("Interrupted while waiting on the queue, InterruptedException: " + exc.getMessage(), LogLevel.INFO);
            }
        }
    }

    /**
     * Sets the stop flag of the thread.
     * This method does not guarantee that the thread will stop as it may still be blocked on the log queue.
     * After calling {@code halt()} the thread must also be interrupted
     * by calling the {@code interrupt()} method. This will cause the {@link RequestWorker} to check the stop flag.
    */
    public void halt() {

        stopped.set(true);
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
    
                LOGGER.log("Could not connect to the database, SQLException: " + exc.getMessage(), LogLevel.WARNING);
            }

            try {

                Thread.sleep(1000);
            }

            catch(InterruptedException exc) {

                LOGGER.log("Interrupted while waiting on retries, InterruptedException: " + exc.getMessage(), LogLevel.INFO);
            }
        }

        LOGGER.log("Retries exhausted while attempting to connect to the database", LogLevel.ERROR);
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
