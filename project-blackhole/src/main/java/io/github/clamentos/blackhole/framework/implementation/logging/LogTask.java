package io.github.clamentos.blackhole.framework.implementation.logging;

///
import io.github.clamentos.blackhole.framework.implementation.configuration.ConfigurationProvider;

///..
import io.github.clamentos.blackhole.framework.implementation.tasks.ContinuousTask;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.ExceptionFormatter;

///.
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

///
/**
 * <h3>Log Task</h3>
 * Fetches the logs from the log queue and prints them.
 * @see ContinuousTask
 * @see Logger
*/
public final class LogTask extends ContinuousTask {

    ///
    /** The service used to actually write the log. */
    private final LogPrinter log_printer;

    ///..
    /** The queue from which to get the log objects. */
    private final BlockingQueue<Log> queue;

    ///
    /**
     * Instantiates a new {@link LogTask} object.
     * @param queue : The log queue from where to fetch the logs.
     * @throws IllegalArgumentException If {@code queue} is {@code null}.
     * @see ContinuousTask
     * @see Logger
    */
    public LogTask(BlockingQueue<Log> queue) throws IllegalArgumentException {

        super();

        if(queue == null) {

            throw new IllegalArgumentException("(LogTask.new) -> The input argument cannot be null");
        }

        log_printer = LogPrinter.getInstance();
        this.queue = queue;

        log_printer.logToFile("LogTask.new >> Instantiated successfully", LogLevels.SUCCESS);
    }

    ///
    /** {@inheritDoc} */
    @Override
    public void initialize() {

        // Do nothing.
        // The point of this method is to perform heavy one-time operations allowing the constructor to be quick.
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void work() {

        iteration();
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void terminate() {

        while(queue.isEmpty() == false) {

            iteration();
        }

        log_printer.logToFile("LogTask.terminate >> Shut down successfull", LogLevels.SUCCESS);
    }

    ///.
    /** Fetches one log from the queue and prints it. */
    private void iteration() {

        Log log = null;
        int busy_wait_attempts = 0;

        while(busy_wait_attempts < ConfigurationProvider.getInstance().MAX_LOG_QUEUE_POLL_ATTEMPTS) {

            log = queue.poll();

            if(log != null) {

                log_printer.printToFile(log);
                return;
            }

            busy_wait_attempts++;
        }

        try {

            log = queue.poll(ConfigurationProvider.getInstance().LOG_QUEUE_POLL_TIMEOUT, TimeUnit.MILLISECONDS);
        }

        catch(InterruptedException exc) {

            log_printer.logToFile(

                ExceptionFormatter.format("LogTask.iteration >> Could not fetch from the queue", exc, ">> Retrying..."),
                LogLevels.NOTE
            );
        }

        if(log != null) {

            log_printer.printToFile(log);
        }
    }

    ///
}
