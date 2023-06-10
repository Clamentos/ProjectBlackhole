package io.github.clamentos.blackhole.web.server;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.config.ConfigurationProvider;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;

import java.io.IOException;

import java.net.ServerSocket;

import java.util.concurrent.locks.ReentrantLock;

//________________________________________________________________________________________________________________________________________

/**
 * Server class that listens and accepts socket requests.
 * Once accepted, the sockets will be placed into a queue to be processed.
*/
public class Server {

    private static volatile Server INSTANCE;
    private static ReentrantLock lock = new ReentrantLock();

    private final int PORT;
    private final int CONNECTION_TIMEOUT;
    private final int RETRIES;
    private final Logger LOGGER;

    private RequestPool request_pool;
    private ServerSocket server_socket;
    private boolean running; 

    //____________________________________________________________________________________________________________________________________

    private Server(RequestPool request_pool) {

        PORT = ConfigurationProvider.SERVER_PORT;
        CONNECTION_TIMEOUT = ConfigurationProvider.CONNECTION_TIMEOUT;
        RETRIES = ConfigurationProvider.MAX_SERVER_START_RETRIES;
        this.request_pool = request_pool;

        LOGGER = Logger.getInstance();
        running = false;
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Get the Server instance (create if necessary).
     * @return The Server instance.
     * @throws InstantiationException if the instantiation of the request pool fails.
     */
    public static Server getInstance() throws InstantiationException {

        Server temp = INSTANCE;

        if(temp == null) {

            lock.lock();
            temp = INSTANCE;

            if(temp == null) {

                INSTANCE = temp = new Server(RequestPool.getInstance());
            }

            lock.unlock();
        }

        return(temp);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Starts the server and listens for requests.
     * If the server is already running, this method will return immediately without doing anything.
     * If this method successfully starts the server, it will occupy the thread indefinetly.
     * If this method fails in starting the server, it will simply return.
     */
    public void start() {

        if(attempt(RETRIES) == true) {

            LOGGER.log("Web server started", LogLevel.SUCCESS);

            while(true) {

                try {

                    request_pool.add(server_socket.accept());
                }

                catch(Exception exc) {

                    LOGGER.log("Could not accept socket, " + exc.getClass().getSimpleName() + ": " + exc.getMessage(), LogLevel.INFO);
                }
            }
        }
    }

    //____________________________________________________________________________________________________________________________________

    private synchronized boolean attempt(int retries) {

        if(running == false) {

            for(int i = 0; i < retries; i++) {

                try {

                    server_socket = new ServerSocket(PORT);
                    server_socket.setSoTimeout(CONNECTION_TIMEOUT);
                    running = true;

                    return(true);
                }

                catch(IOException exc) {
    
                    LOGGER.log("Could not create server socket, IOException: " + exc.getMessage(), LogLevel.ERROR);
                }

                try {

                    Thread.sleep(1000);
                }

                catch(InterruptedException exc) {

                    LOGGER.log("Interrupted while waiting on retries, InterruptedException: " + exc.getMessage(), LogLevel.INFO);
                }
            }

            LOGGER.log("Retries exhausted while attempting to start the server", LogLevel.ERROR);
        }

        return(false);
    }

    //____________________________________________________________________________________________________________________________________
}
