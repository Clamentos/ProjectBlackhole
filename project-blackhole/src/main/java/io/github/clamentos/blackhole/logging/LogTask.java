package io.github.clamentos.blackhole.logging;

import java.util.HashMap;

//________________________________________________________________________________________________________________________________________

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.common.configuration.Constants;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Stoppable Runnable.</b></p>
 * <p>Log task.</p>
 * This class is responsible for fetching the logs from the log queue and printing them.
*/
public class LogTask implements Runnable {

    private final ConfigurationProvider CONFIGS =  ConfigurationProvider.getInstance();

    private final int MAX_QUEUE_POLLS;
    private final HashMap<LogLevel, Boolean> TO_FILE_MAP;

    private LinkedBlockingQueue<Log> queue;
    private AtomicBoolean running;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Instantiates a new {@link LogTask} with the given queue.
     * @param queue : The log queue from where to fetch the logs.
     * @throws NullPointerException If {@code queue} is {@code null}.
    */
    public LogTask(LinkedBlockingQueue<Log> queue) throws NullPointerException {

        if(queue == null) throw new NullPointerException();

        MAX_QUEUE_POLLS = CONFIGS.getConstant(Constants.MAX_QUEUE_POLLS, Integer.class);
        this.queue = queue;

        TO_FILE_MAP = new HashMap<>();

        TO_FILE_MAP.put(LogLevel.DEBUG, CONFIGS.getConstant(Constants.DEBUG_LEVEL_TO_FILE, Boolean.class));
        TO_FILE_MAP.put(LogLevel.INFO, CONFIGS.getConstant(Constants.INFO_LEVEL_TO_FILE, Boolean.class));
        TO_FILE_MAP.put(LogLevel.SUCCESS, CONFIGS.getConstant(Constants.SUCCESS_LEVEL_TO_FILE, Boolean.class));
        TO_FILE_MAP.put(LogLevel.NOTE, CONFIGS.getConstant(Constants.NOTE_LEVEL_TO_FILE, Boolean.class));
        TO_FILE_MAP.put(LogLevel.WARNING, CONFIGS.getConstant(Constants.WARNING_LEVEL_TO_FILE, Boolean.class));
        TO_FILE_MAP.put(LogLevel.ERROR, CONFIGS.getConstant(Constants.ERROR_LEVEL_TO_FILE, Boolean.class));

        running = new AtomicBoolean(false);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Stop the task.
    */
    public void stop() {

        running.set(false);
    }

    //____________________________________________________________________________________________________________________________________
    
    @Override
    public void run() {

        boolean relax;
        int count;
        Log log;

        running.set(true);

        while(running.get() == true) {

            relax = true;
            count = 0;

            while(count < MAX_QUEUE_POLLS) {

                log = queue.poll();

                if(log != null) {

                    relax = false;
                    doWork(log);

                    break;
                }

                count++;
            }

            if(relax == true) {

                while(true) {

                    try {

                        log = queue.poll(2, TimeUnit.SECONDS);
                        doWork(log);
                    }

                    catch(InterruptedException exc) {

                        if(running.get() == true) {

                            LogPrinter.printToConsole(new Log(

                                "LogTask.run > shutting down." +
                                exc.getMessage(),
                                LogLevel.INFO
                            ));
                            
                            return;
                        }
                    }
                }
            }
        }
    }

    //____________________________________________________________________________________________________________________________________

    private void doWork(Log log) {

        if(TO_FILE_MAP.get(log.log_level()) == true) {

            LogPrinter.printToFile(log);
        }

        else {

            LogPrinter.printToConsole(log);
        }
    }

    //____________________________________________________________________________________________________________________________________
}
