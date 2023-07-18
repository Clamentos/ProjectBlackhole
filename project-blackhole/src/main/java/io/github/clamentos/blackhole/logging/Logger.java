// OK
package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.common.utility.TaskManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Eager-loaded singleton.</b></p>
 * <p>Logger.</p>
 * This class inserts the produced logs into the log queue.
*/
public class Logger {
    
    private static final Logger INSTANCE = new Logger();

    private ConfigurationProvider configuration_provider;

    private List<LinkedBlockingQueue<Log>> queues;

    //____________________________________________________________________________________________________________________________________

    private Logger() {

        configuration_provider = ConfigurationProvider.getInstance();

        queues = new ArrayList<>();

        for(int i = 0; i < configuration_provider.NUM_LOG_TASKS; i++) {

            queues.add(new LinkedBlockingQueue<>());
            TaskManager.getInstance().launchNewLogTask(queues.get(i));
        }
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Get the {@link LogFileManager} instance created during class loading.
     * @return The {@link ConfigurationProvider} instance.
    */
    public static Logger getInstance() {

        return(INSTANCE);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * <p>Insert the log into the log queue.</p>
     * This method will block the calling thread up to {@link Constants#QUEUE_TIMEOUT} milliseconds.
     * If the insert times out, this method will retry up to {@link Constants#MAX_QUEUE_POLLS} times.
     * If that fails too, this method will simply return.
     * @param message : The message to log.
     * @param severity : The severity of the log event.
    */
    public void log(String message, LogLevel severity) {

        Log log;
        int i;
        int queue_index;

        if(configuration_provider.MIN_LOG_LEVEL.compareTo(severity) <= 0) {

            log = new Log(message, severity);
            i = 0;

            while(i < configuration_provider.MAX_LOG_QUEUE_POLLS) {

                try {

                    queue_index = (int)(log.id() % queues.size());

                    if(queues.get(queue_index).offer(log, configuration_provider.LOG_QUEUE_TIMEOUT, TimeUnit.MILLISECONDS) == true) {

                        return;
                    }

                    else {

                        LogPrinter.printToConsole(new Log(

                            "Logger.log 1 > Could not insert into the log queue, timed out",
                            LogLevel.ERROR
                        ));
                    }
                }

                catch(InterruptedException exc) {

                    LogPrinter.printToConsole(new Log(

                        "Logger.log 2 > Could not insert into the log queue, InterruptedException: " +
                        exc.getMessage() + " Retrying",
                        LogLevel.WARNING
                    ));
                }

                i++;
            }

            LogPrinter.printToConsole(new Log(

                "Logger.log 3 > Could not insert into the log queue, polls exhausted",
                LogLevel.ERROR
            ));
        }
    }

    //____________________________________________________________________________________________________________________________________
}
