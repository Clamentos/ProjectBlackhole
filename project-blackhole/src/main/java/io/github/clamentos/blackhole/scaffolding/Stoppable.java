package io.github.clamentos.blackhole.scaffolding;

///
/**
 * <h3>Stoppable interface</h3>
 * 
 * Simple interface that extends {@link Runnable}, specifying that the implementing class
 * is a runnable that can be stopped.
*/
public interface Stoppable extends Runnable {
    
    /** Signals {@code this} {@link Runnable} to start the halting procedure. */
    void stop();

    ///
}
