package io.github.clamentos.blackhole.common.framework;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Stoppable interface.</p>
 * Simple interface that extends {@link Runnable}, specifying
 * that the implementing class is a runnable that can be stopped.
*/
public interface Stoppable extends Runnable {
    
    /** Signal {@code this} {@link Runnable} to start the halting procedure. */
    void stop();

    //____________________________________________________________________________________________________________________________________
}
