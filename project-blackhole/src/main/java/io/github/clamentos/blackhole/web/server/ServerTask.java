package io.github.clamentos.blackhole.web.server;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.common.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.common.framework.ContinuousTask;
import io.github.clamentos.blackhole.common.utility.TaskManager;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.web.connection.ConnectionTask;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Web server task.</p>
 * This class is responsible for accepting the incoming client sockets,
 * as well as to manage each {@link ConnectionTask}.
*/
public class ServerTask extends ContinuousTask {

    private final Logger LOGGER;
    private final ConfigurationProvider CFG;

    private ServerSocket server_socket;
    private HashMap<SocketAddress, Integer> sockets_per_ip;
    private AtomicBoolean stop_completed;

    //____________________________________________________________________________________________________________________________________

    protected ServerTask() {

        super();

        LOGGER = Logger.getInstance();
        CFG = ConfigurationProvider.getInstance();

        sockets_per_ip = new HashMap<>();
        stop_completed = new AtomicBoolean(false);

        LOGGER.log("ServerTask.new > Server task instantiated successfully", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    @Override
    public void run() {

        Socket client_socket;
        SocketAddress client_address;
        Integer num;

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());

        if(attempt(CFG.MAX_SERVER_START_ATTEMPTS) == true) {

            LOGGER.log("ServerTask.run 1 > Server task started successfully", LogLevel.SUCCESS);

            while(super.isStopped() == false) {

                try {

                    client_socket = server_socket.accept();    // If thread is interrupted, it throws socket exception.
                    client_address = client_socket.getRemoteSocketAddress();
                    num = sockets_per_ip.get(client_address);

                    if(num != null) {

                        if(num < CFG.MAX_SOCKETS_PER_IP) {

                            sockets_per_ip.put(client_address, num + 1);
                            TaskManager.getInstance().launchNewConnectionTask(client_socket);
                        }

                        else {

                            // too many, close
                            client_socket.close();
                        }
                    }

                    else {

                        sockets_per_ip.put(client_address, 1);
                        TaskManager.getInstance().launchNewConnectionTask(client_socket);
                    }
                }

                catch(IOException exc) {

                    LOGGER.log(
                        
                        "ServerTask.run 2 > Could not accept the incoming socket, " +
                        exc.getClass().getSimpleName() + ": " + exc.getMessage(),
                        LogLevel.NOTE
                    );
                }
            }

            try {

                server_socket.close();
                LOGGER.log("ServerTask.run 3 > Server task stopped successfully", LogLevel.SUCCESS);
            }
            
            catch (IOException exc) {

                LOGGER.log(
                        
                    "ServerTask.run 4 > Could not close the server socket, IOException: " + exc.getMessage(),
                    LogLevel.ERROR
                );
            }
        }

        stop_completed.set(true);
    }

    public boolean isStoppingCompleted() {

        return(stop_completed.get());
    }

    //____________________________________________________________________________________________________________________________________

    // attemp to create the server socket with N retries
    private synchronized boolean attempt(int retries) {

        if(Thread.currentThread().isInterrupted() == false) {

            for(int i = 0; i < retries; i++) {

                try {

                    server_socket = new ServerSocket(CFG.SERVER_PORT);
                    server_socket.setSoTimeout(CFG.SOCKET_TIMEOUT);

                    return(true);
                }

                catch(Exception exc) {
    
                    LOGGER.log(

                        "Server.attempt 1 > Could not create server socket, " +
                        exc.getClass().getSimpleName() + ": " +
                        exc.getMessage(),
                        LogLevel.ERROR
                    );
                }

                try {

                    Thread.sleep(1000 * (i + 1));
                }

                catch(InterruptedException exc) {

                    LOGGER.log(
                        
                        "Server.attempt 2 > Interrupted while waiting on retries, InterruptedException: " +
                        exc.getMessage(),
                        LogLevel.INFO
                    );
                }
            }

            LOGGER.log(

                "Server.attempt 3 > Retries exhausted while attempting to start the server",
                LogLevel.ERROR
            );
        }

        return(false);
    }

    //____________________________________________________________________________________________________________________________________
}
