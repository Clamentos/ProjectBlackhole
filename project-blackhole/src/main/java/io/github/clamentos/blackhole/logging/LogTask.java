package io.github.clamentos.blackhole.logging;

import java.util.concurrent.LinkedBlockingQueue;

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.common.configuration.Constants;

public class LogTask implements Runnable {

    private final ConfigurationProvider CONFIGS =  ConfigurationProvider.getInstance();

    private final int MAX_QUEUE_POLLS;

    private LinkedBlockingQueue<Log> queue;
    private boolean running;

    public LogTask(LinkedBlockingQueue<Log> queue) {

        MAX_QUEUE_POLLS = CONFIGS.getConstant(Constants.MAX_QUEUE_POLLS, Integer.class);
        this.queue = queue;
        running = false;
    }
    
    @Override
    public void run() {

        boolean do_block;
        Log log;

        running = true;

        while(running == true) {

            do_block = true;

            try {

                // attemp to fetch from the queue aggressively
                for(int i = 0; i < MAX_QUEUE_POLLS; i++) {

                    log = queue.poll();

                    if(log != null) {

                        do_block = false;
                        doWork(log);

                        break;
                    }
                }

                // failed to fetch aggressively, fallback on blocking mode
                if(do_block == true) {

                    doWork(queue.take());
                }
            }

            catch(InterruptedException exc) {

                //...
            }
        }
    }

    private void doWork(Log log) {

        if(log.log_level().getToFile() == true) {

            LogPrinter.printToFile(log);
        }

        else {

            LogPrinter.printToConsole(log);
        }
    }
}
