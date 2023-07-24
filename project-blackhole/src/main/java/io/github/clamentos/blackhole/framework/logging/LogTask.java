// OK
package io.github.clamentos.blackhole.framework.logging;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.common.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.framework.tasks.ContinuousTask;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Stoppable Runnable.</b></p>
 * <p>Log task.</p>
 * This class is responsible for fetching the logs from the log queue and printing them.
*/
public final class LogTask extends ContinuousTask {

    private ConfigurationProvider configuration_provider;
    private LogPrinter log_printer;

    private LinkedBlockingQueue<Log> queue;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Instantiate a new {@link LogTask} with the given queue.
     * @param queue : The log queue from where to fetch the logs.
     * @throws NullPointerException If {@code queue} is {@code null}.
    */
    public LogTask(LinkedBlockingQueue<Log> queue, long id) throws NullPointerException {

        super(id);

        if(queue == null) throw new NullPointerException();

        configuration_provider = ConfigurationProvider.getInstance();
        log_printer = LogPrinter.getInstance();

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

    // Fetch 1 log and print it (thread safe).
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
