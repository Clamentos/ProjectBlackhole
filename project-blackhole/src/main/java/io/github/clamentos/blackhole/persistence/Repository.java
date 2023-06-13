package io.github.clamentos.blackhole.persistence;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.config.ConfigurationProvider;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

//________________________________________________________________________________________________________________________________________

/**
 * Repository class responsible for managing the workers and inserting the queries into the queue.
*/
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

    /**
     * <p><b>This method is thread safe.</b></p>
     * Inserts the query into the queue for processing.
     * @param query : The query to be added.
     * @param wait : Wait for the query to complete.
     */
    public void execute(QueryWrapper query, boolean wait) {

        try {

            query_queue.put(query);

            while(wait == true) {

                if(query.getStatus() == true || query.getStatus() == false) {

                    break;
                }
            }
        }

        catch(InterruptedException exc) {

            LOGGER.log("Interrupted while waiting on queue, InterruptedException: " + exc.getMessage(), LogLevel.NOTE);
        }
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Starts all the inactive {@link QueryWorker}.
    */
    public synchronized void startWorkers() {

        for(QueryWorker worker : query_workers) {

            if(worker.getRunning() == false) {

                worker.start();
            }
        }
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Stops all the active {@link QueryWorker}.
     * @param wait : Waits for the workers to drain the query queue before stopping it.
     *               If set to false, it will stop the workers as soon as they finish
     *               processing the current query.
    */
    public synchronized void stopWorkers(boolean wait) {

        if(wait == true) {

            while(true) {

                if(query_queue.size() == 0) {

                    stopWorkers();
                    break;
                }
            }
        }

        else {

            stopWorkers();
        }
    }

    //____________________________________________________________________________________________________________________________________

    private void stopWorkers() {

        for(QueryWorker worker : query_workers) {

            worker.halt();
            worker.interrupt();
        }
    }

    //____________________________________________________________________________________________________________________________________
}
