package io.github.clamentos.blackhole.common.framework;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.config.ConfigurationProvider;
import io.github.clamentos.blackhole.common.exceptions.GlobalExceptionHandler;

import java.util.concurrent.BlockingQueue;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Abstract worker thread class that can be started and stopped.</p>
 * <p>This worker blocks and waits for resources to be placed in the queue before doing work.</p>
 * @param <R> R : The type of resource that the queue holds.
*/
public abstract class Worker<R> extends Thread implements WorkerSpec {
 
    private boolean running;
    private int identifier;
    private BlockingQueue<R> resource_queue;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Instantiates a new worker with the given resource queue.
     * @param identifier : The worker identifier.
     * @param resource_queue : The queue on which the thread will consume and do work.
    */
    public Worker(int identifier, BlockingQueue<R> resource_queue) {

        running = false;
        this.identifier = identifier;
        this.resource_queue = resource_queue;
    }

    //____________________________________________________________________________________________________________________________________

    @Override
    public void run() {

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());

        boolean do_block;
        R elem;
        running = true;

        while(running == true) {

            do_block = true;

            try {

                // attemp to fetch from the queue aggressively
                for(int i = 0; i < ConfigurationProvider.QUEUE_POLL_LIMIT; i++) {

                    elem = resource_queue.poll();

                    if(elem != null) {

                        do_block = false;
                        doWork(elem);

                        break;
                    }
                }

                // failed to fetch aggressively, fallback on blocking mode
                if(do_block == true) {

                    doWork(resource_queue.take());
                }
            }

            catch(InterruptedException exc) {

                catchInterrupted(exc);
            }
        }
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * {@inheritDoc}
    */
    @Override
    public int getIdentifier() {

        return(identifier);
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * {@inheritDoc}
    */
    @Override
    public boolean getRunning() {

        return(running);
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * {@inheritDoc}
     * This method does not guarantee that the worker will stop as it may still be blocked on the queue.
     * After calling {@link Worker#halt} the worker must also be interrupted
     * by calling the {@link Worker#interrupt} method,
     * which will cause the {@link Worker} to check the running flag.
    */
    @Override
    public void halt() {

        running = false;
    }

    /**
     * <p>Method that does the actual processing.</p>
     * <p>After fetching the resource from the queue, the {@link Worker} will call this method.</p>
     * @param resource : The resource.
    */
    public abstract void doWork(R resource);

    /**
     * Method to handle the potential {@link InterruptedException} thrown while waiting on the queue.
     * @param exc : The InterruptedException.
    */
    public abstract void catchInterrupted(InterruptedException exc);

    //____________________________________________________________________________________________________________________________________
}
