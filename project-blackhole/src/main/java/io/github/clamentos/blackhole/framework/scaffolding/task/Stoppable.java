package io.github.clamentos.blackhole.framework.scaffolding.task;

///
/**
 * <h3>Stoppable</h3>
 * Specifies that the implementing class is a runnable that can be stopped.
*/
public interface Stoppable extends Runnable {

    ///
    /** Signals {@code this} runnable to start the halting procedure. */
    void stop();

    ///
}
