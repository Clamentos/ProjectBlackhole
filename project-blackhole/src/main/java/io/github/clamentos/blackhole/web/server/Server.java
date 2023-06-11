package io.github.clamentos.blackhole.web.server;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.config.ConfigurationProvider;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

//________________________________________________________________________________________________________________________________________

/**
 * Server class that listens and accepts socket requests.
 * Once accepted, the sockets will be placed into a queue to be processed.
*/
public class Server {

    private static volatile Server INSTANCE;
    private static ReentrantLock lock = new ReentrantLock();

    private final Logger LOGGER;

    private boolean running;
    private ServerSocket server_socket;
    private LinkedBlockingQueue<Socket> sockets_queue;
    private RequestWorker[] request_workers; 

    //____________________________________________________________________________________________________________________________________

    private Server() {

        LOGGER = Logger.getInstance();
        running = false;
        sockets_queue = new LinkedBlockingQueue<>(ConfigurationProvider.MAX_REQUEST_QUEUE_SIZE);
        request_workers = new RequestWorker[ConfigurationProvider.REQUEST_WORKERS];

        for(int i = 0; i < request_workers.length; i++) {

            request_workers[i] = new RequestWorker(sockets_queue);
            request_workers[i].start();
        }
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Get the Server instance (create if necessary).
     * @return The Server instance.
     */
    public static Server getInstance() {

        Server temp = INSTANCE;

        if(temp == null) {

            lock.lock();
            temp = INSTANCE;

            if(temp == null) {

                INSTANCE = temp = new Server();
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

        if(attempt(ConfigurationProvider.MAX_SERVER_START_RETRIES) == true) {

            LOGGER.log("Web server started", LogLevel.SUCCESS);

            while(true) {

                try {

                    sockets_queue.put(server_socket.accept());
                }

                catch(Exception exc) {

                    LOGGER.log("Could not accept socket, " + exc.getClass().getSimpleName() + ": " + exc.getMessage(), LogLevel.INFO);
                }
            }
        }
    }

    /**
     * Stops all the active {@link RequestWorker}.
     * @param wait : waits for the workers to drain the sockets queue before stopping it.
     *               If set to false, it will stop the workers as soon as they finish
     *               handling the current socket.
    */
    public void stopWorkers(boolean wait) {

        if(wait == true) {

            while(true) {

                if(sockets_queue.size() == 0) {

                    stopWorkers();
                    break;
                }
            }
        }

        else {

            stopWorkers();
        }
    }

    //____________________________________________________________________________________________________________________________________

    private synchronized boolean attempt(int retries) {

        if(running == false) {

            for(int i = 0; i < retries; i++) {

                try {

                    server_socket = new ServerSocket(ConfigurationProvider.SERVER_PORT);
                    server_socket.setSoTimeout(ConfigurationProvider.CONNECTION_TIMEOUT);
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

    private void stopWorkers() {

        for(RequestWorker worker : request_workers) {

            worker.halt();
            worker.interrupt();
        }
    }

    //____________________________________________________________________________________________________________________________________
}
