package io.github.clamentos.blackhole.common;

//________________________________________________________________________________________________________________________________________

import java.util.concurrent.BlockingQueue;

//________________________________________________________________________________________________________________________________________

/**
 * Abstract worker manager class that can start and stop the specified workers.
 * @param <R> R : The type of resource that the queue holds.
 * @param <W> W : The type of worker to be managed.
*/
public abstract class WorkerManager<R, W extends Worker<R>> {

    private BlockingQueue<R> resource_queue;
    private W[] workers;

    //____________________________________________________________________________________________________________________________________

    public WorkerManager(BlockingQueue<R> resource_queue, W[] workers) {

        this.resource_queue = resource_queue;
        this.workers = workers;
    }

    //____________________________________________________________________________________________________________________________________

    public BlockingQueue<R> getResourceQueue() {

        return(resource_queue);
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Starts all the inactive {@link Worker}.
    */
    public synchronized void startWorkers() {

        for(W worker : workers) {

            if(worker.getRunning() == false) {

                worker.start();
            }
        }
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Stops all the active {@link Worker}.
     * @param wait : Waits for the workers to drain the {@code resource_queue} before stopping them.
     *               If set to false, it will stop the workers as soon as they finish
     *               processing the current resource.
    */
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
