package io.github.clamentos.blackhole.framework.implementation.network.tasks;

///
import io.github.clamentos.blackhole.framework.implementation.configuration.ConfigurationProvider;

///..
import io.github.clamentos.blackhole.framework.implementation.logging.Logger;

///..
import io.github.clamentos.blackhole.framework.implementation.logging.exportable.LogLevels;
import io.github.clamentos.blackhole.framework.implementation.logging.exportable.MetricsTracker;

///..
import io.github.clamentos.blackhole.framework.implementation.network.ServerContext;

///..
import io.github.clamentos.blackhole.framework.implementation.tasks.ContinuousTask;
import io.github.clamentos.blackhole.framework.implementation.tasks.TaskManager;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.ResourceReleaserInternal;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.exportable.ExceptionFormatter;

///..
import io.github.clamentos.blackhole.framework.scaffolding.ApplicationContext;

///.
import java.io.IOException;

///..
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

///
/**
 * <h3>Server Task</h3>
 * Responsible for accepting or rejecting incoming client TCP connections.
*/
public final class ServerTask extends ContinuousTask {

    ///
    /** The service used to log notable events. */
    private final Logger logger;

    /** The server context service that tracks and manages client sockets. */
    private final ServerContext server_context;

    /** The service used to track application metrics. */
    private final MetricsTracker metrics_service;

    ///..
    /** The application context containing the essential user defined service providers. */
    private final ApplicationContext application_context;

    /** The currently active server socket. */
    private final ServerSocket server_socket;

    ///
    /**
     * Instantiates a new {@link ServerTask} object.
     * @param application_context : The application context that will be provided to subsequent tasks.
     * @throws IOException If any IO error occurs while creating the server socket.
    */
    public ServerTask(ApplicationContext application_context) throws IOException {

        super();

        logger = Logger.getInstance();
        server_context = ServerContext.getInstance();
        metrics_service = MetricsTracker.getInstance();

        this.application_context = application_context;

        server_socket = new ServerSocket(

            ConfigurationProvider.getInstance().SERVER_PORT,
            ConfigurationProvider.getInstance().MAX_INCOMING_CONNECTIONS
        );

        server_socket.setSoTimeout(ConfigurationProvider.getInstance().SERVER_SOCKET_TIMEOUT);
        logger.log("ServerTask.new => Instantiated successfully", LogLevels.SUCCESS);
    }

    ///
    /** {@inheritDoc} */
    @Override
    public void initialize() {

        // Do nothing.
        // The point of this method is to perform heavy one-time operations allowing the constructor to be quick.
    }

    ///.
    /** {@inheritDoc} */
    @Override
    public void work() {

        Socket client_socket;

        try {

            client_socket = server_socket.accept();
        }

        catch(IOException exc) {

            if(exc instanceof SocketTimeoutException == false) {

                logger.log(

                    ExceptionFormatter.format("ServerTask.work => Could not accept the connection", exc, "Skipping..."),
                    LogLevels.WARNING
                );
            }

            return;
        }

        if(server_context.isClientSocketAllowed(client_socket.getRemoteSocketAddress())) {

            TaskManager.getInstance().launchThread(new TransferTask(application_context, client_socket), "ConnectionTask");
        }

        else {

            metrics_service.incrementSocketsRefused(1);

            if(ResourceReleaserInternal.release(logger, "ServerTask", "work", client_socket) == true) {

                metrics_service.incrementSocketsClosed(1);
            }
        }
    }

    ///.
    /** {@inheritDoc} */
    @Override
    public void terminate() {

        if(ResourceReleaserInternal.release(logger, "ServerTask", "terminate", server_socket)) {

            logger.log("ServerTask.terminate => Shut down successfull", LogLevels.SUCCESS);
        }

        else {

            logger.log("ServerTask.terminate => Shut down with errors: Could not close the server socket", LogLevels.ERROR);
        }
    }

    ///
}
