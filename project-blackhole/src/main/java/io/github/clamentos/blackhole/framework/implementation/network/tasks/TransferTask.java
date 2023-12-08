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
import io.github.clamentos.blackhole.framework.implementation.utility.StreamUtils;

///..
import io.github.clamentos.blackhole.framework.scaffolding.ApplicationContext;

///.
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

///..
import java.net.Socket;

///..
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

///..
import java.util.concurrent.CountDownLatch;

///..
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

///..
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

///
/**
 * <h3>Transfer task</h3>
 * Responsible for launching request tasks and managing the client sockets.
 * @see ContinuousTask
 * @see RequestTask
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

    /** The flag that indicates if the socket was closed due to a timeout. */
    private final AtomicBoolean timed_out;

    /** The buffer holding the threads running the request tasks. */
    private final List<Thread> request_task_references;

    ///..
    /** The input stream of the socket. */
    private InputStream in;

    /** The output stream of the socket. */
    private OutputStream out;

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
        timed_out = new AtomicBoolean(false);
        request_task_references = new ArrayList<>();

        logger.log("TransferTask.new >> Instantiated successfully", LogLevels.SUCCESS);
    }

    ///
    /** {@inheritDoc} */
    @Override
    public void initialize() {

        try {

            in = client_socket.getInputStream();
            out = client_socket.getOutputStream();

            client_socket.setSoTimeout(ConfigurationProvider.getInstance().CLIENT_SOCKET_TIMEOUT);
        }

        catch(IOException exc) {

            logger.log(ExceptionFormatter.format("TransferTask.initialize >> ", exc, " >> Aborting..."), LogLevels.ERROR);
        }

        // Update the socket counters, metrics and socket properties.
        server_context.increment(client_socket.getRemoteSocketAddress());
        metrics_service.incrementSocketsAccepted(1);
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void work() {

        try {

            // Read the next message length and check if it's ok.
            long data_length = StreamUtils.readNumber(in, 8);

            if(data_length >= 0) {

                // Launch a new request task.
                CountDownLatch signal = new CountDownLatch(1);
                TransferContext transfer_context = new TransferContext(in, out, output_lock, active_request_task_count);

                request_task_references.add(TaskManager.getInstance().launchThread(new RequestTask(

                    application_context, transfer_context, signal, data_length

                ), "RequestTask"));

                // Wait for the task to finish using the input stream.
                signal.await();
            }

            else {

                // Client wants to close the connection. Terminate.
                super.stop();
                return;
            }
        }

        // Only ignore if interrupted, otherwise terminate.
        catch(IOException | NoSuchElementException | InterruptedException exc) {

            if(exc instanceof IOException || exc instanceof NoSuchElementException) {

                if(timed_out.get() == false || exc instanceof NoSuchElementException) {

                    logger.log(ExceptionFormatter.format("TransferTask.work >> ", exc, " >> Aborting..."), LogLevels.ERROR);
                    super.stop();

                    return;
                }
            }

            if(exc instanceof InterruptedException) {

                logger.log(ExceptionFormatter.format("TransferTask.work >> ", exc, " >> Ignoring..."), LogLevels.NOTE);
            }
        }
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void terminate() {

        // Wait for all request tasks to terminate and then quit.
        for(Thread task : request_task_references) {

            try {

                task.join();
            }
            
            catch(InterruptedException exc) {

                logger.log(ExceptionFormatter.format("TransferTask.terminate >> ", exc, " >> Ignoring..."), LogLevels.NOTE);
            }
        }

        // Update the socket counters and metrics.
        server_context.decrement(client_socket.getRemoteSocketAddress());
        metrics_service.incrementSocketsClosed(1);

        // Close the client socket.
        if(ResourceReleaser.release(logger, "TransferTask.terminate", client_socket)) {

            logger.log("TransferTask.terminate >> Shut down successfull", LogLevels.SUCCESS);
        }

        else {

            logger.log("TransferTask.terminate >> Could not close the client socket", LogLevels.ERROR);
        }
    }

    ///
}
