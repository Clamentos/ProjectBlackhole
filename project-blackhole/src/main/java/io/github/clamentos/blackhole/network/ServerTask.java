package io.github.clamentos.blackhole.network;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.common.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.framework.tasks.ContinuousTask;
import io.github.clamentos.blackhole.framework.tasks.TaskManager;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import java.util.HashMap;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Continuous task.</b></p>
 * This class is responsible for accepting the incoming client sockets.
*/
public final class ServerTask extends ContinuousTask {

    private Logger logger;
    private ConfigurationProvider configuration_provider;

    private ServerSocket server_socket;
    private HashMap<SocketAddress, Integer> sockets_per_ip;    // TODO: use session ids as keys...
    private int accept_retries;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Instantiates a new {@link ServerTask}.
     * @param id : The unique task id.
    */
    public ServerTask(long id) {

        super(id);

        logger = Logger.getInstance();
        configuration_provider = ConfigurationProvider.getInstance();

        sockets_per_ip = new HashMap<>();
        accept_retries = 0;

        logger.log("ServerTask.new > Instantiated successfully", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * {@inheritDoc}
    */
    @Override
    public void setup() {

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());

        for(int i = 0; i < configuration_provider.MAX_SERVER_START_ATTEMPTS; i++) {

            try {

                server_socket = new ServerSocket(configuration_provider.SERVER_PORT);
                server_socket.setSoTimeout(configuration_provider.SERVER_SOCKET_SAMPLE_TIME);
                logger.log("ServerTask.work > Server task started successfully", LogLevel.SUCCESS);

                return;
            }

            catch(Exception exc) {
    
                logger.log(

                    "Server.attempt > Could not create server socket, " +
                    exc.getClass().getSimpleName() + ": " +
                    exc.getMessage() + " Retrying",
                    LogLevel.WARNING
                );
            }

            try {

                Thread.sleep(1000 * (i + 1));
            }

            catch(InterruptedException exc) {

                logger.log(
                        
                    "Server.attempt > Interrupted while waiting on retries, InterruptedException: " +
                    exc.getMessage() + " Ignoring",
                    LogLevel.INFO
                );
            }
        }

        logger.log(

            "Server.attempt > Retries exhausted while attempting to start the server. Aborting this task",
            LogLevel.ERROR
        );

        super.stop();
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * {@inheritDoc}
    */
    @Override
    public void work() {

        Socket client_socket;
        SocketAddress client_address;
        Integer num;

        try {

            client_socket = server_socket.accept();    // If thread is interrupted -> SocketException.
            client_address = client_socket.getRemoteSocketAddress();
            num = sockets_per_ip.get(client_address);

            if(num != null) {

                if(num < configuration_provider.MAX_CLIENTS_PER_IP) {

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

            if(exc instanceof SocketTimeoutException) {

                if(accept_retries > configuration_provider.MAX_SERVER_SOCKET_SAMPLES) {

                    accept_retries = 0;

                    logger.log(
                        
                        "ServerTask.work > Server socket timed out",
                        LogLevel.INFO
                    );
                }

                else {

                    accept_retries++;
                }
            }

            else {

                logger.log(
                        
                    "ServerTask.run > Could not accept the incoming socket, " +
                    exc.getClass().getSimpleName() + ": " + exc.getMessage() + " Skipping",
                    LogLevel.ERROR
                );
            }
        }
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * {@inheritDoc}
    */
    @Override
    public void terminate() {

        try {

            server_socket.close();
            logger.log("ServerTask.terminate > Server task stopped successfully", LogLevel.SUCCESS);
        }
            
        catch (IOException exc) {

            logger.log(
                        
                "ServerTask.terminate > Could not close the server socket, IOException: " +
                exc.getMessage(),
                LogLevel.ERROR
            );
        }
    }

    //____________________________________________________________________________________________________________________________________
}
