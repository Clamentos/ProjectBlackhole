package io.github.clamentos.blackhole.persistence;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.Worker;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;

import java.util.concurrent.BlockingQueue;

//________________________________________________________________________________________________________________________________________

public class QueryWorker extends Worker<QueryWrapper> {

    private final Logger LOGGER;
    // TODO: connection!

    //____________________________________________________________________________________________________________________________________

    public QueryWorker(BlockingQueue<QueryWrapper> query_queue) {

        super(query_queue);
        LOGGER = Logger.getInstance();
    }

    //____________________________________________________________________________________________________________________________________
    
    @Override
    public void doWork(QueryWrapper statement) {

        // exe statement
        // put result in map
    }

    /**
     * {@inheritDoc}
    */
    @Override
    public void catchInterrupted(InterruptedException exc) {

        LOGGER.log("Interrupted while waiting on queue, InterruptedException: " + exc.getMessage(), LogLevel.INFO);
    }

    //____________________________________________________________________________________________________________________________________
}
