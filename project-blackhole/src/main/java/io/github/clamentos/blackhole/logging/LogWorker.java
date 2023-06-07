package io.github.clamentos.blackhole.logging;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.exceptions.GlobalExceptionHandler;

import java.util.concurrent.LinkedBlockingQueue;

//________________________________________________________________________________________________________________________________________

/**
 * Worker thread that actually does the logging.
*/
public class LogWorker extends Thread {

    private LinkedBlockingQueue<Log> log_queue;

    //____________________________________________________________________________________________________________________________________

    /**
     * Instantiates a new log worker on the given log queue.
     * @param name : The worker name, used for identification in logs.
     * @param log_queue : The log queue on which the thread will consume and log.
     */
    public LogWorker(String name, LinkedBlockingQueue<Log> log_queue) {

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
        Thread.currentThread().setName(name);
        this.log_queue = log_queue;
    }

    //____________________________________________________________________________________________________________________________________

    @Override
    public void run() {

        Log log;

        while(true) {

            try {

                log = log_queue.take();

                if(log.log_file() == null) {

                    LogPrinter.printToConsole(log.message(), log.log_level());
                }

                else {

                    LogPrinter.printToFile(log.message(), log.log_level(), log.log_file().getFileWriter());
                }
            }

            catch(InterruptedException exc) {

                LogPrinter.printToConsole("Interrupted while waiting on the queue, InterruptedException: " + exc.getMessage(), LogLevel.INFO);
            }
        }
    }

    //____________________________________________________________________________________________________________________________________
}