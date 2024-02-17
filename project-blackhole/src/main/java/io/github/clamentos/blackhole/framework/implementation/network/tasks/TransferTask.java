package io.github.clamentos.blackhole.framework.implementation.network.tasks;

///
import io.github.clamentos.blackhole.framework.implementation.configuration.ConfigurationProvider;

///..
import io.github.clamentos.blackhole.framework.implementation.logging.LogLevels;
import io.github.clamentos.blackhole.framework.implementation.logging.Logger;
import io.github.clamentos.blackhole.framework.implementation.logging.MetricsTracker;

///..
import io.github.clamentos.blackhole.framework.implementation.network.ServerContext;

///..
import io.github.clamentos.blackhole.framework.implementation.network.transfer.TransferContext;

///..
import io.github.clamentos.blackhole.framework.implementation.tasks.ContinuousTask;
import io.github.clamentos.blackhole.framework.implementation.tasks.TaskManager;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.ExceptionFormatter;
import io.github.clamentos.blackhole.framework.implementation.utility.ResourceReleaser;

///..
import io.github.clamentos.blackhole.framework.scaffolding.ApplicationContext;

///.
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

///..
import java.net.Socket;

///..
import java.util.ArrayList;
import java.util.List;

///..
import java.util.concurrent.CountDownLatch;

///..
import java.util.concurrent.atomic.AtomicInteger;

///..
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

///
/**
 * <h3>Transfer Task</h3>
 * Responsible for launching request tasks and managing the client sockets.
 * @see ContinuousTask
 * @see RequestTask
 * @see ServerTask
*/
public final class TransferTask extends ContinuousTask {

    ///
    /** The service used to log notable events. */    
    private final Logger logger;

    /** The server context service that tracks and manages client sockets. */
    private final ServerContext server_context;

    /** The service used to track application metrics. */
    private final MetricsTracker metrics_service;

    ///..
    /** The application context containing the essential user-defined service providers. */
    private final ApplicationContext application_context;

    /** The client socket on which {@code this} task will operate on. */
    private final Socket client_socket;

    ///..
    /** The synchronization primitive to enforce mutual exclusion on the output stream of the socket. */
    private final Lock output_lock;

    /** The current number of active request tasks. */
    private final AtomicInteger active_request_task_count;

    /** The buffer holding the threads running the request tasks. */
    private final List<Thread> request_task_references;

    ///..
    /** The input stream of the socket. */
    private DataInputStream in;

    /** The output stream of the socket. */
    private DataOutputStream out;

    ///
    /**
     * Instantiates a new {@link TransferTask} object.
     * @param application_context : The application context to be propagated to the request tasks.
     * @param client_socket : The client socket to operate on.
     * @throws IllegalArgumentException If either {@code application_context} or {@code client_socket} are {@code null}.
     * @see ApplicationContext
    */
    public TransferTask(ApplicationContext application_context, Socket client_socket) throws IllegalArgumentException {

        if(application_context == null || client_socket == null) {

            throw new IllegalArgumentException("(TransferTask.new) -> The input arguments cannot be null");
        }

        logger = Logger.getInstance();
        server_context = ServerContext.getInstance();
        metrics_service = MetricsTracker.getInstance();

        this.application_context = application_context;
        this.client_socket = client_socket;

        output_lock = new ReentrantLock();
        active_request_task_count = new AtomicInteger(0);
        request_task_references = new ArrayList<>();

        logger.log("TransferTask.new >> Instantiated successfully", LogLevels.SUCCESS);
    }

    ///
    /** {@inheritDoc} */
    @Override
    public void initialize() {

        try {

            in = new DataInputStream(client_socket.getInputStream());
            out = new DataOutputStream(client_socket.getOutputStream());

            client_socket.setSoTimeout(ConfigurationProvider.getInstance().CLIENT_SOCKET_TIMEOUT);
        }

        catch(IOException exc) {

            logger.log(ExceptionFormatter.format("TransferTask.initialize >>", exc, ">> Aborting..."), LogLevels.ERROR);
        }

        server_context.increment(client_socket.getRemoteSocketAddress());
        metrics_service.incrementSocketsAccepted(1);
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void work() {

        try {

            if(active_request_task_count.get() > 0) {

                long payload_size = in.readLong();

                if(payload_size >= 0) {

                    CountDownLatch signal = new CountDownLatch(1);
                    TransferContext transfer_context = new TransferContext(in, out, output_lock, active_request_task_count);

                    request_task_references.add(TaskManager.getInstance().launchThread(

                        new RequestTask(application_context, transfer_context, signal, payload_size),
                        "RequestTask"
                    ));

                    signal.await();
                }

                else {

                    // Client wants to close the connection. Terminate.

                    // NOTE: The protocol demands that a request must start by specifying the length of its payload.
                    // A negative value is a special case that signals to the server the desire to gracefully close the connection.

                    super.stop();
                }
            }

            else {

                super.stop();
            }
        }

        catch(IOException | InterruptedException exc) {

            if(exc instanceof InterruptedException) {

                logger.log(

                    ExceptionFormatter.format("TransferTask.work >> Could not read from the socket", exc, ">> Retrying..."),
                    LogLevels.NOTE
                );
            }

            else {

                logger.log(

                    ExceptionFormatter.format("TransferTask.work >> Could not read from the socket", exc, ">> Aborting..."),
                    LogLevels.ERROR
                );

                super.stop();
            }
        }
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void terminate() {

        for(Thread task : request_task_references) {

            try {

                task.join();
            }

            catch(InterruptedException exc) {

                logger.log(

                    ExceptionFormatter.format("TransferTask.terminate >> Could not join child task", exc, ">> Skipping..."),
                    LogLevels.NOTE
                );
            }
        }

        server_context.decrement(client_socket.getRemoteSocketAddress());
        metrics_service.incrementSocketsClosed(1);

        if(ResourceReleaser.release(logger, "TransferTask.terminate", client_socket)) {

            logger.log("TransferTask.terminate >> Shut down successfull", LogLevels.SUCCESS);
        }

        else {

            logger.log("TransferTask.terminate >> Shut down with errors: Could not close the client socket", LogLevels.ERROR);
        }
    }

    ///
}
