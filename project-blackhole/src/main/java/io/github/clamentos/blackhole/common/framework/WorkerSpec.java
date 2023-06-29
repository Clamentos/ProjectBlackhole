package io.github.clamentos.blackhole.common.framework;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Worker interface.</p>
 * Simple interface to define basic managing methods for any worker thread.
*/
public interface WorkerSpec {

    //____________________________________________________________________________________________________________________________________
    
    /**
     * Get the current worker identifier.
     * @return the identifier.
    */
    int getIdentifier();

    /**
     * Get the current status of the worker.
     * @return {@code true} if running, {@code false} if not.
    */
    boolean getRunning();

    /**
     * <p>Sets the running flag of the worker.</p>
    */
    void halt();

    //____________________________________________________________________________________________________________________________________
}
