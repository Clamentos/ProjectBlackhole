package io.github.clamentos.blackhole.common;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.exceptions.GlobalExceptionHandler;
import java.util.concurrent.BlockingQueue;

//________________________________________________________________________________________________________________________________________

/**
 * Abstract worker thread class that can be started and stopped.
 * This worker periodically waits for resources to be placed in the queue before doing work.
 * @param <T> : The type of resource that the queue holds.
*/
public abstract class Worker<T> extends Thread {
 
    private boolean running;
    private BlockingQueue<T> resource_queue;

    //____________________________________________________________________________________________________________________________________

    /**
     * Instantiates a new worker on the given resource queue.
     * @param resource_queue : The queue on which the thread will consume and do work.
     */
    public Worker(BlockingQueue<T> resource_queue) {

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
        this.resource_queue = resource_queue;
        running = false;
    }

    //____________________________________________________________________________________________________________________________________

    @Override
    public void run() {

        T elem;
        running = true;

        while(running == true) {

            try {

                elem = resource_queue.take();
                doWork(elem);
            }

            catch(InterruptedException exc) {

                catchInterrupted(exc);
            }
        }
    }

    /**
     * Get the current status of the worker.
     * @return {@code true} if running, {@code false} if not.
     */
    public boolean getRunning() {

        return(running);
    }

    /**
     * Sets the running flag of the worker.
     * This method does not guarantee that the worker will stop as it may still be blocked on the queue.
     * After calling {@code halt()} the worker must also be interrupted
     * by calling the {@code interrupt()} method, which will cause the {@link Worker} to check the running flag.
    */
    public void halt() {

        running = false;
    }

    /**
     * Method that does the actual processing.
     * After fetching the resource from the queue, the {@link Worker} will call this method.
     * @param resource : The resource.
     */
    public abstract void doWork(T resource);

    /**
     * Method to handle the potential {@link InterruptedException} thrown while waiting on the queue.
     * @param exc : the exception.
     */
    public abstract void catchInterrupted(InterruptedException exc);

    //____________________________________________________________________________________________________________________________________
}
