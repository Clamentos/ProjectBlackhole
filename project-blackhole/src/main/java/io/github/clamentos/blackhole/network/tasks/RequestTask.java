package io.github.clamentos.blackhole.network.tasks;

///
import io.github.clamentos.blackhole.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.network.servlets.Dispatcher;
import io.github.clamentos.blackhole.scaffolding.tasks.Task;

import java.io.BufferedOutputStream;
import java.io.IOException;

///
/**
 * <h3>Client request handling task</h3>
 * This class is responsible for actually processing a single request.
 * @apiNote This class is a <b>short-lived (non stoppable) runnable task</b>.
*/
public final class RequestTask extends Task {

    private Logger logger;
    private Dispatcher dispatcher;

    private byte[] raw_request;
    private BufferedOutputStream out;
    private int request_counter;

    ///
    /**
     * Instantiates a new {@link RequestTask} object.
     * @param raw_request : The raw request bytes.
     * @param out : The output stream of the socket.
     * @param id : The unique task id.
    */
    public RequestTask(byte[] raw_request, BufferedOutputStream out, long id, int request_counter) {

        super(id);

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
        logger = Logger.getInstance();
        dispatcher = Dispatcher.getInstance();

        this.raw_request = raw_request;
        this.out = out;
        this.request_counter = request_counter;
    }

    ///
    /** {@inheritDoc} */
    @Override
    public void work() {

        try {

            out.write(dispatcher.dispatch(raw_request, request_counter));
        }

        catch(IOException exc) {

            logger.log(
                
                "RequestTask.work > Could not write the response to the output stream, IOException: " +
                exc.getMessage(),
                LogLevel.ERROR
            );
        }
    }

    ///
}
