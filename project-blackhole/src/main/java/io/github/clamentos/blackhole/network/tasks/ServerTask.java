package io.github.clamentos.blackhole.network.tasks;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.scaffolding.tasks.ContinuousTask;
import io.github.clamentos.blackhole.scaffolding.tasks.TaskManager;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import java.util.HashMap;

//________________________________________________________________________________________________________________________________________

/**
 * <h3>Client socket accepting task</h3>
 * This class is responsible for accepting the incoming client sockets.
 * @apiNote This class is a <b>continuous runnable task</b>.
*/
public final class ServerTask extends ContinuousTask {

    private Logger logger;

    private final int MAX_SERVER_START_ATTEMPTS;
    private final int SERVER_PORT;
    private final int SERVER_SOCKET_SAMPLE_TIME;
    private final int MAX_CLIENTS_PER_IP;
    private final int MAX_SERVER_SOCKET_SAMPLES;

    private ServerSocket server_socket;
    private HashMap<SocketAddress, Integer> sockets_per_ip;
    private int accept_retries;

    //____________________________________________________________________________________________________________________________________

    /**
     * Instantiates a new {@link ServerTask} object.
     * @param id : The unique task id.
    */
    public ServerTask(long id) {

        super(id);

        logger = Logger.getInstance();

        MAX_SERVER_START_ATTEMPTS = ConfigurationProvider.getInstance().MAX_SERVER_START_ATTEMPTS;
        SERVER_PORT = ConfigurationProvider.getInstance().SERVER_PORT;
        SERVER_SOCKET_SAMPLE_TIME = ConfigurationProvider.getInstance().SERVER_SOCKET_SAMPLE_TIME;
        MAX_CLIENTS_PER_IP = ConfigurationProvider.getInstance().MAX_CLIENTS_PER_IP;
        MAX_SERVER_SOCKET_SAMPLES = ConfigurationProvider.getInstance().MAX_SERVER_SOCKET_SAMPLES;

        sockets_per_ip = new HashMap<>();
        accept_retries = 0;

        logger.log("ServerTask.new > Instantiated successfully", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    /** {@inheritDoc} */
    @Override
    public void setup() {

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());

        for(int i = 0; i < MAX_SERVER_START_ATTEMPTS; i++) {

            try {

                server_socket = new ServerSocket(SERVER_PORT);
                server_socket.setSoTimeout(SERVER_SOCKET_SAMPLE_TIME);
                logger.log("ServerTask.work > Started successfully", LogLevel.SUCCESS);

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
                    LogLevel.WARNING
                );
            }
        }

        logger.log(

            "Server.attempt > Retries exhausted while attempting to start the server. Aborting this task",
            LogLevel.ERROR
        );

        super.stop();
    }

    /** {@inheritDoc} */
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

                if(num < MAX_CLIENTS_PER_IP) {

                    sockets_per_ip.put(client_address, num + 1);
                    TaskManager.getInstance().launchNewConnectionTask(client_socket);
                }

                else { // Too many, refuse.

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

                if(accept_retries > MAX_SERVER_SOCKET_SAMPLES) {

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

    /** {@inheritDoc} */
    @Override
    public void terminate() {

        try {

            server_socket.close();
            logger.log("ServerTask.terminate > Shut down successfull", LogLevel.SUCCESS);
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
