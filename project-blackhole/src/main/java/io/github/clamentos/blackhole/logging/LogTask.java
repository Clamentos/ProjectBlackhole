package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.common.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.framework.tasks.ContinuousTask;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>STEREOTYPE: Continuous task..</b></p>
 * This class is responsible for fetching the logs from the log queue and printing them.
*/
public final class LogTask extends ContinuousTask {

    private ConfigurationProvider configuration_provider;
    private LogPrinter log_printer;

    private BlockingQueue<Log> queue;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Instantiates a new {@link LogTask} object.
     * @param queue : The log queue from where to fetch the logs.
     * @param id : The unique task id.
     * @throws IllegalArgumentException If {@code queue} is {@code null}.
    */
    public LogTask(BlockingQueue<Log> queue, long id) throws IllegalArgumentException {

        super(id);

        log_printer = LogPrinter.getInstance();
        configuration_provider = ConfigurationProvider.getInstance();

        if(queue == null) {

            log_printer.log("LogTask.new > Could not instantiate, log queue was null", LogLevel.ERROR);
            throw new IllegalArgumentException();
        }

        this.queue = queue;
        log_printer.log("LogTask.new > Instantiated successfully", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * {@inheritDoc}
    */
    @Override
    public void setup() {

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
        log_printer.log("LogTask.setup > Log task started successfully", LogLevel.SUCCESS);
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * {@inheritDoc}
    */
    @Override
    public void work() {

        iteration();
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * {@inheritDoc}
    */
    @Override
    public void terminate() {

        // Loop as long as there are logs in the queue, then exit.
        while(queue.isEmpty() == false) {

            iteration();
        }

        log_printer.log("LogTask.terminate > Shut down successfull", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    // Fetches 1 log and prints it (thread safe).
    private void iteration() {

        Log log;

        boolean relax = true;
        int count = 0;

        // Poll aggressively with N retries.
        while(count < configuration_provider.MAX_LOG_QUEUE_POLLS) {

            log = queue.poll();

            if(log != null) {

                relax = false;
                log_printer.printLog(log);

                break;
            }

            count++;
        }

        // Block on queue when the retries are exhausted.
        if(relax == true) {

            try {

                // Poll doesn't generate InterrupredException when it times out, it simply returns null.
                log = queue.poll(configuration_provider.LOG_QUEUE_SAMPLE_TIME, TimeUnit.MILLISECONDS);

                if(log != null) {

                    log_printer.printLog(log);
                }
            }

            catch(InterruptedException exc) {

                super.stop();
            }
        }
    }

    //____________________________________________________________________________________________________________________________________
}
