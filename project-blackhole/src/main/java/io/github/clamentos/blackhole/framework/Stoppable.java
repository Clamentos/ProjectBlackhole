package io.github.clamentos.blackhole.framework;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Interface.</b></p>
 * <p>Stoppable runnable.</p>
 * Simple interface that extends {@link Runnable}, specifying that the implementing class
 * is a runnable that can be stopped.
*/
public interface Stoppable extends Runnable {
    
    /** Signals {@code this} {@link Runnable} to start the halting procedure. */
    void stop();

    //____________________________________________________________________________________________________________________________________
}
