package io.github.clamentos.blackhole.web.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import io.github.clamentos.blackhole.common.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.common.utility.TaskLauncher;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.web.connection.ConnectionTask;

public class ServerTask implements Runnable {

    private final Logger LOGGER;
    private final int SERVER_PORT;
    private final int SOCKET_TIMEOUT;
    private final int MAX_START_RETRIES;

    private boolean running;
    private ServerSocket server_socket;

    protected ServerTask(int server_port, int socket_timeout, int max_start_retries) {

        LOGGER = Logger.getInstance();

        SERVER_PORT = server_port;
        SOCKET_TIMEOUT = socket_timeout;
        MAX_START_RETRIES = max_start_retries;

        running = false;

        LOGGER.log("Server task instantiated successfully", LogLevel.SUCCESS);
    }

    @Override
    public void run() {

        Socket client_socket;
        long socket_id;

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());

        if(attempt(MAX_START_RETRIES) == true) {

            running = true;
            socket_id = 0;

            LOGGER.log("Server task started successfully", LogLevel.SUCCESS);

            while(running == true) {

                try {

                    client_socket = server_socket.accept();
                    TaskLauncher.launch(new ConnectionTask(client_socket, socket_id, SOCKET_TIMEOUT));
                    socket_id++;
                }

                catch(IOException exc) {

                    LOGGER.log("Server task could not accept socket", LogLevel.NOTE);
                }
            }

            LOGGER.log("Server task stopped successfully", LogLevel.SUCCESS);
        }
    }

    public void stop() {

        running = false;
    }

    // attemp to create the server socket with N retries
    private synchronized boolean attempt(int retries) {

        if(running == false) {

            for(int i = 0; i < retries; i++) {

                try {

                    server_socket = new ServerSocket(SERVER_PORT);
                    server_socket.setSoTimeout(SOCKET_TIMEOUT);
                    running = true;

                    return(true);
                }

                catch(Exception exc) {
    
                    LOGGER.log(

                        "Server.attempt > Could not create server socket, " +
                        exc.getClass().getSimpleName() + ": " +
                        exc.getMessage(),
                        LogLevel.ERROR
                    );
                }

                try {

                    Thread.sleep(1000);
                }

                catch(InterruptedException exc) {

                    LOGGER.log(
                        
                        "Server.attempt > Interrupted while waiting on retries, InterruptedException: " +
                        exc.getMessage(),
                        LogLevel.INFO
                    );
                }
            }

            LOGGER.log(

                "Server.attempt > Retries exhausted while attempting to start the server",
                LogLevel.ERROR
            );
        }

        return(false);
    }
}
