package io.github.clamentos.blackhole.common.framework;

//________________________________________________________________________________________________________________________________________

import java.util.concurrent.BlockingQueue;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Worker manager interface.</p>
 * Simple interface to define basic managing methods for any worker manager.
 * @param <R> R : The type of resource that the queue holds.
*/
public interface WorkerManagerSpec<R> {
    
    /**
     * Get the resource queue.
     * @return The resource queue.
    */
    BlockingQueue<R> getResourceQueue();

    /**
     * Starts all the inactive {@link Worker}.
    */
    void startWorkers();

    /**
     * Stops all the active {@link Worker}.
     * @param wait : Waits for the workers to drain the {@link Worker#resource_queue} before stopping them.
     *               If set to {@code false}, it will stop the workers as soon as they finish
     *               processing the current resource.
    */
    void stopWorkers(boolean wait);
}
