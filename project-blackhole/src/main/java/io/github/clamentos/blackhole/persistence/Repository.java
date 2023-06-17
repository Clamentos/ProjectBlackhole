package io.github.clamentos.blackhole.persistence;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.WorkerManager;
import io.github.clamentos.blackhole.common.config.ConfigurationProvider;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.persistence.query.QueryWrapper;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

//________________________________________________________________________________________________________________________________________

/**
 * Repository class responsible for managing the workers and inserting the queries into the queue.
*/
public class Repository extends WorkerManager<QueryWrapper, QueryWorker> {

    private static volatile Repository INSTANCE;
    private static ReentrantLock lock = new ReentrantLock();

    private final Logger LOGGER;

    //____________________________________________________________________________________________________________________________________

    private Repository(BlockingQueue<QueryWrapper> query_queue, QueryWorker[] query_workers) {

        super(query_queue, query_workers);
        LOGGER = Logger.getInstance();
        LOGGER.log("Repository instantiated and workers started", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Get the Repository instance.
     * If the instance doesn't exist, create it with the values configured in
     * {@link ConfigurationProvider} and start the workers.
     * @return The Repository instance.       
    */
    public static Repository getInstance() {

        Repository temp = INSTANCE;

        LinkedBlockingQueue<QueryWrapper> query_queue;
        QueryWorker[] query_workers;

        if(temp == null) {

            lock.lock();
            temp = INSTANCE;

            if(temp == null) {

                query_queue = new LinkedBlockingQueue<>();
                query_workers = new QueryWorker[ConfigurationProvider.DB_CONNECTIONS];

                for(QueryWorker worker : query_workers) {

                    worker = new QueryWorker(query_queue);
                    worker.start();
                }

                INSTANCE = temp = new Repository(query_queue, query_workers);
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

            super.getResourceQueue().put(query);

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

    //____________________________________________________________________________________________________________________________________
}
