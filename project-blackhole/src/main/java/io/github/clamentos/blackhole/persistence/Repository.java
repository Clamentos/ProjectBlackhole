package io.github.clamentos.blackhole.persistence;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.config.ConfigurationProvider;
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

    private Repository() {

        LOGGER = Logger.getInstance();
        
        query_queue = new LinkedBlockingQueue<>();
        query_workers = new QueryWorker[ConfigurationProvider.DB_CONNECTIONS];

        for(QueryWorker worker : query_workers) {

            worker = new QueryWorker(query_queue);
            worker.start();
        }
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Get the Repository instance (create if necessary).
     * @return The Repository instance.
     */
    public static Repository getInstance() {

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

    public void execute(QueryWrapper query) {

        try {

            query_queue.put(query);
        }

        catch(InterruptedException exc) {

            //...
        }
    }

    //____________________________________________________________________________________________________________________________________
}
