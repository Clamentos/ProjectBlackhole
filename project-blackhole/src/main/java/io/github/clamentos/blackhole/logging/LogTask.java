package io.github.clamentos.blackhole.logging;

///
import io.github.clamentos.blackhole.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.scaffolding.tasks.ContinuousTask;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

///
/**
 * <h3>Logging task</h3>
 * This class is responsible for fetching the logs from the log queue and printing them.
 * @apiNote This class is a <b>continuous runnable task</b>.
*/
public final class LogTask extends ContinuousTask {

    ///
    private LogPrinter log_printer;

    private final int MAX_LOG_QUEUE_POLLS;
    private final int LOG_QUEUE_SAMPLE_TIME;

    private BlockingQueue<Log> queue;

    ///
    /**
     * Instantiates a new {@link LogTask} object.
     * @param queue : The log queue from where to fetch the logs.
     * @param id : The unique task id.
     * @throws IllegalArgumentException If {@code queue} is {@code null}.
    */
    public LogTask(BlockingQueue<Log> queue, long id) throws IllegalArgumentException {

        super(id);

        log_printer = LogPrinter.getInstance();
        
        MAX_LOG_QUEUE_POLLS = ConfigurationProvider.getInstance().MAX_LOG_QUEUE_POLLS;
        LOG_QUEUE_SAMPLE_TIME = ConfigurationProvider.getInstance().LOG_QUEUE_SAMPLE_TIME;

        if(queue == null) {

            log_printer.log("LogTask.new > Could not instantiate, log queue was null", LogLevel.ERROR);
            throw new IllegalArgumentException();
        }

        this.queue = queue;
        log_printer.log("LogTask.new > Instantiated successfully", LogLevel.SUCCESS);
    }

    ///
    /** {@inheritDoc} */
    @Override
    public void setup() {

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
        log_printer.log("LogTask.setup > Started successfully", LogLevel.SUCCESS);
    }

    /** {@inheritDoc} */
    @Override
    public void work() {

        iteration();
    }

    /** {@inheritDoc} */
    @Override
    public void terminate() {

        // Finish logging the leftovers, then exit.
        while(queue.isEmpty() == false) {

            iteration();
        }

        log_printer.log("LogTask.terminate > Shut down successfull", LogLevel.SUCCESS);
    }

    ///
    // Fetches 1 log and prints it (thread safe).
    private void iteration() {

        Log log;

        boolean relax = true;
        int count = 0;

        // Poll aggressively with some retries.
        while(count < MAX_LOG_QUEUE_POLLS) {

            log = queue.poll();

            if(log != null) {

                relax = false;
                log_printer.printLog(log);

                break;
            }

            count++;
        }

        // Block on the queue when the retries have been exhausted.
        if(relax == true) {

            try {

                // Poll doesn't generate InterrupredException when it times out, it simply returns null.
                log = queue.poll(LOG_QUEUE_SAMPLE_TIME, TimeUnit.MILLISECONDS);

                if(log != null) {

                    log_printer.printLog(log);
                }
            }

            catch(InterruptedException exc) {

                log_printer.log(

                    "LogTask.iteration > Interrupted while trying to fetch from the queue, retrying",
                    LogLevel.WARNING
                );

                super.stop();
            }
        }
    }

    ///
}
