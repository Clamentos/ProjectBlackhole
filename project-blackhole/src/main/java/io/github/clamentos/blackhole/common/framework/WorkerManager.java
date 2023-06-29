package io.github.clamentos.blackhole.common.framework;

//________________________________________________________________________________________________________________________________________

import java.util.concurrent.BlockingQueue;

//________________________________________________________________________________________________________________________________________

/**
 * Abstract worker manager class that can start and stop the specified workers.
 * @param <R> R : The type of resource that the queue holds.
 * @param <W> W : The type of worker to be managed.
*/
public abstract class WorkerManager<R, W extends Worker<R>> implements WorkerManagerSpec<R> {

    private BlockingQueue<R> resource_queue;
    private W[] workers;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Instantiates a new worker manager
     * with the given resource queue with the given {@link Worker} array.
     * @param resource_queue : The queue on which the workers will consume and do work.
     * @param workers : The workers to manage.
    */
    public WorkerManager(BlockingQueue<R> resource_queue, W[] workers) {

        this.resource_queue = resource_queue;
        this.workers = workers;
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * {@inheritDoc}
    */
    @Override
    public BlockingQueue<R> getResourceQueue() {

        return(resource_queue);
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * {@inheritDoc}
    */
    @Override
    public synchronized void startWorkers() {

        for(W worker : workers) {

            if(worker.getRunning() == false) {

                worker.start();
            }
        }
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * {@inheritDoc}
    */
    @Override
    public synchronized void stopWorkers(boolean wait) {

        if(wait == true) {

            while(true) {

                if(resource_queue.size() == 0) {

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

        for(W worker : workers) {

            worker.halt();
            worker.interrupt();
        }
    }

    //____________________________________________________________________________________________________________________________________
}
