package io.github.clamentos.blackhole.common.framework;

//________________________________________________________________________________________________________________________________________

import java.util.concurrent.atomic.AtomicBoolean;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Abstract contunuous task.</p>
 * This class implements the {@link Stoppable} and condenses
 * common code and operations for any {@link Runnable}
 * that executes for a very long (and potentially infinite) time.
*/
public abstract class ContinuousTask implements Stoppable {
    
    private AtomicBoolean stop;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Instantiates a new {@link ContinuousTask}.
    */
    public ContinuousTask() {

        stop = new AtomicBoolean(false);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Checks if {@code this} {@link ContinuousTask} is stopped or not.
     * @return The stopped flag.
    */
    public boolean isStopped() {

        return(stop.get());
    }

    //____________________________________________________________________________________________________________________________________

    @Override
    public abstract void run();

    /**
     * <p><b>This method is thread safe.</p></b>
     * Sets the stop flag of {@code this} {@link ContinuousTask}.
    */
    @Override
    public void stop() {

        stop.set(true);
    }

    //____________________________________________________________________________________________________________________________________
}

/*
 * run() {
 * 
 *     setup();    // abstract
 *     
 *     while(stop.get() == false) {
 * 
 *         work();    // abstract
 *     }
 * 
 *     cleanup();    // abstract
 * }
*/