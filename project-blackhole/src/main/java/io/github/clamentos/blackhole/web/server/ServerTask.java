package io.github.clamentos.blackhole.web.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.common.configuration.Constants;
import io.github.clamentos.blackhole.common.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.common.utility.TaskLauncher;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.web.connection.ConnectionTask;

public class ServerTask implements Runnable {

    private final Logger LOGGER;
    private final ConfigurationProvider CONFIGS;

    private final int SERVER_PORT;
    private final int SOCKET_TIMEOUT;
    private final int MAX_SERVER_START_RETRIES;
    private final int MAX_SOCKETS_PER_IP;

    private boolean running;
    private ServerSocket server_socket;
    private HashMap<SocketAddress, Integer> sockets_per_ip;

    protected ServerTask() {

        LOGGER = Logger.getInstance();
        CONFIGS = ConfigurationProvider.getInstance();

        SERVER_PORT = CONFIGS.getConstant(Constants.SERVER_PORT, Integer.class);
        SOCKET_TIMEOUT = CONFIGS.getConstant(Constants.SOCKET_TIMEOUT, Integer.class);
        MAX_SERVER_START_RETRIES = CONFIGS.getConstant(Constants.MAX_SERVER_START_RETRIES, Integer.class);
        MAX_SOCKETS_PER_IP = CONFIGS.getConstant(Constants.MAX_SOCKETS_PER_IP, Integer.class);

        running = false;
        sockets_per_ip = new HashMap<>();

        LOGGER.log("Server task instantiated successfully", LogLevel.SUCCESS);
    }

    @Override
    public void run() {

        Socket client_socket;
        SocketAddress client_address;
        Integer num;

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());

        if(attempt(MAX_SERVER_START_RETRIES) == true) {

            running = true;
            LOGGER.log("Server task started successfully", LogLevel.SUCCESS);

            while(running == true) {

                try {

                    client_socket = server_socket.accept();
                    client_address = client_socket.getRemoteSocketAddress();
                    
                    num = sockets_per_ip.get(client_address);

                    if(num != null) {

                        if(num < MAX_SOCKETS_PER_IP) {

                            sockets_per_ip.put(client_address, num + 1);
                            TaskLauncher.launch(new ConnectionTask(client_socket));
                        }

                        else {

                            // too many, close
                            client_socket.close();
                        }
                    }

                    else {

                        sockets_per_ip.put(client_address, 1);
                        TaskLauncher.launch(new ConnectionTask(client_socket));
                    }
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
