package io.github.clamentos.blackhole.web.server;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.common.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.common.framework.ContinuousTask;
import io.github.clamentos.blackhole.common.utility.TaskManager;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.HashMap;

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
    private boolean attempt_succeeded;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Instantiate a new {@link ServerTask} with the given id.
     * @param id : The task id.
    */
    public ServerTask(long id) {

        super(id);

        LOGGER = Logger.getInstance();
        CFG = ConfigurationProvider.getInstance();

        sockets_per_ip = new HashMap<>();
        attempt_succeeded = false;

        LOGGER.log("ServerTask.new > Server task instantiated successfully", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    @Override
    public void setup() {

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());

        for(int i = 0; i < CFG.MAX_SERVER_START_ATTEMPTS; i++) {

            try {

                server_socket = new ServerSocket(CFG.SERVER_PORT);
                server_socket.setSoTimeout(CFG.SOCKET_TIMEOUT);

                attempt_succeeded = true;
                return;
            }

            catch(Exception exc) {
    
                LOGGER.log(

                    "Server.attempt 1 > Could not create server socket, " +
                    exc.getClass().getSimpleName() + ": " +
                    exc.getMessage() + " Retrying",
                    LogLevel.WARNING
                );
            }

            try {

                Thread.sleep(1000 * (i + 1));
            }

            catch(InterruptedException exc) {

                LOGGER.log(
                        
                    "Server.attempt 2 > Interrupted while waiting on retries, InterruptedException: " +
                    exc.getMessage() + " Ignoring",
                    LogLevel.INFO
                );
            }
        }

        LOGGER.log(

            "Server.attempt 3 > Retries exhausted while attempting to start the server. Aborting this task",
            LogLevel.ERROR
        );

        attempt_succeeded = false;
    }

    @Override
    public void work() {

        Socket client_socket;
        SocketAddress client_address;
        Integer num;

        if(attempt_succeeded == false) {

            return;
        }

        LOGGER.log("ServerTask.work 1 > Server task started successfully", LogLevel.SUCCESS);

        try {

            client_socket = server_socket.accept();    // If thread is interrupted -> SocketException.
            client_address = client_socket.getRemoteSocketAddress();
            num = sockets_per_ip.get(client_address);

            if(num != null) {

                if(num < CFG.MAX_SOCKETS_PER_IP) {

                    sockets_per_ip.put(client_address, num + 1);
                    TaskManager.getInstance().launchNewConnectionTask(client_socket);
                }

                else {

                    // Too many, refuse.
                    client_socket.close();
                }
            }

            else {

                sockets_per_ip.put(client_address, 1);
                TaskManager.getInstance().launchNewConnectionTask(client_socket);
            }
        }

        catch(IOException exc) {

            if(exc instanceof SocketException) {

                LOGGER.log(
                        
                    "ServerTask.work 2 > Server socket timed out",
                    LogLevel.INFO
                );
            }

            else {

                LOGGER.log(
                        
                    "ServerTask.run 3 > Could not accept the incoming socket, " +
                    exc.getClass().getSimpleName() + ": " + exc.getMessage() + " Skipping",
                    LogLevel.ERROR
                );
            }
        }
    }

    @Override
    public void terminate() {

        try {

            server_socket.close();
            LOGGER.log("ServerTask.terminate 1 > Server task stopped successfully", LogLevel.SUCCESS);
        }
            
        catch (IOException exc) {

            LOGGER.log(
                        
                "ServerTask.terminate 2 > Could not close the server socket, IOException: " + exc.getMessage(),
                LogLevel.ERROR
            );
        }
    }

    //____________________________________________________________________________________________________________________________________
}
