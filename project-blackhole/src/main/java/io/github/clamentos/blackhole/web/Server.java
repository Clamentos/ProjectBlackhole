package io.github.clamentos.blackhole.web;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.ConfigurationProvider;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.SocketTimeoutException;

//________________________________________________________________________________________________________________________________________

/**
 * This class listen and accepts socket requests.
 * Stereotype : singleton
*/
public class Server {

    private static volatile Server INSTANCE;
    private static Object dummy_mutex = new Object();

    private final int PORT;
    private final int CONNECTION_TIMEOUT;
    private final Logger LOGGER;

    private RequestPool request_pool;
    private ServerSocket server_socket;
    private boolean running; 

    //____________________________________________________________________________________________________________________________________

    /**
     * Instantiates a new Server with the given parameters.
     * @param port : Port at which the server listens for TCP connections.
     * @param connection_timeout : The maximum connection timeout time (in milliseconds).
     * @param request_pool : The pool in which to queue the requests.
     */
    private Server(int port, int connection_timeout, RequestPool request_pool) {

        PORT = port;
        CONNECTION_TIMEOUT = connection_timeout;
        this.request_pool = request_pool;

        LOGGER = Logger.getInstance();
        running = false;
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Get the Server instance.
     * @return The Server instance.
     */
    public static Server getInstance() {

        Server temp = INSTANCE;

        if(temp == null) {

            synchronized(dummy_mutex) {

                temp = INSTANCE;

                if(temp == null) {

                    temp = new Server(
            
                        ConfigurationProvider.SERVER_PORT,
                        ConfigurationProvider.CONNECTION_TIMEOUT,
                        RequestPool.getInstance()
                    );
                }
            }
        }

        return(temp);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Starts the server.
     * This method will block the calling thread until a connection is made or times out.
     * @throws IOException if there are any I/O errors.
     * @throws SocketTimeoutException if the connection timed out.
     * @throws IllegalStateException if the server is already running.
     */
    public void start() throws IOException, SocketTimeoutException, IllegalStateException {

        synchronized(this) {

            if(running == true) {

                throw new IllegalStateException("The web server is already running!");
            }

            server_socket = new ServerSocket(PORT);
            server_socket.setSoTimeout(CONNECTION_TIMEOUT);
            running = true;
        }

        LOGGER.log("Web server started, listening for requests...", LogLevel.INFO);

        while(true) {

            request_pool.add(server_socket.accept());
        }
    }

    public void stop() {

        // TODO: ...
    }

    //____________________________________________________________________________________________________________________________________
}
