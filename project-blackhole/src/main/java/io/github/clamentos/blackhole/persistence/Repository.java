package io.github.clamentos.blackhole.persistence;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.config.ConfigurationProvider;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

//________________________________________________________________________________________________________________________________________

public class Repository {

    private static volatile Repository INSTANCE;
    private static ReentrantLock lock = new ReentrantLock();

    private final Logger LOGGER;

    private LinkedBlockingQueue<QueryWrapper> query_queue;
    private QueryWorker[] query_workers;

    //____________________________________________________________________________________________________________________________________

    private Repository() throws InstantiationException {

        LOGGER = Logger.getInstance();
        
        query_queue = new LinkedBlockingQueue<>();
        query_workers = new QueryWorker[ConfigurationProvider.DB_CONNECTIONS];

        for(QueryWorker worker : query_workers) {

            worker = new QueryWorker(query_queue);
            worker.start();
        }

        LOGGER.log("Repository instantiated and workers started", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Get the Repository instance.
     * If the instance doesn't exist, create it with the values configured in
     * {@link ConfigurationProvider} and start the workers.
     * @return The Repository instance.
     * @throw InstantiationException if any of the workers cannot connect to the database.
    */
    public static Repository getInstance() throws InstantiationException {

        Repository temp = INSTANCE;

        if(temp == null) {

            lock.lock();
            temp = INSTANCE;

            if(temp == null) {

                INSTANCE = temp = new Repository();
            }

            lock.unlock();
        }

        return(temp);
    }

    //____________________________________________________________________________________________________________________________________

    // TODO: start workers
    // TODO: stop workers

    /**
     * <p><b>This method is thread safe.</b></p>
     * Inserts the query into the queue for processing.
     * @param query : The query to be added.
     */
    public void execute(QueryWrapper query) {

        try {

            query_queue.put(query);
        }

        catch(InterruptedException exc) {

            LOGGER.log("Interrupted while waiting on queue, InterruptedException: " + exc.getMessage(), LogLevel.NOTE);
        }
    }

    //____________________________________________________________________________________________________________________________________
}
