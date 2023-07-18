// OK
package io.github.clamentos.blackhole.common.framework;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.utility.TaskManager;
import java.util.concurrent.atomic.AtomicBoolean;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Abstract contunuous task.</p>
 * This class implements the {@link Stoppable} interface and condenses
 * common code and operations for any {@link Runnable} that executes for a very long
 * (and potentially infinite) time.
*/
public abstract class ContinuousTask implements Stoppable {
    
    private AtomicBoolean stop;
    private final long ID;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * @param id : The task id.
     * Instantiate a new {@link ContinuousTask}.
    */
    public ContinuousTask(long id) {

        stop = new AtomicBoolean(false);
        ID = id;
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Check if {@code this} {@link ContinuousTask} is stopped or not.
     * @return The stopped flag.
    */
    public boolean isStopped() {

        return(stop.get());
    }

    //____________________________________________________________________________________________________________________________________

    /** Method to perform the setup operations before entering the loop. */
    public abstract void setup();

    /** Method to perform the main operations while in the continuous loop. */
    public abstract void work();

    /** Method to perform cleanup operations after the the loop. */
    public abstract void terminate();

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.
     * However, the called abstract methods might not.</p></b>
     * <p>Main execution method.</p>
     * <p>This method will perform the following:</p>
     * <pre>
     *     setup();
     *     while(stop.get() == false) {
     *         work();
     *     }
     *     terminate();
     *     TaskManager.getInstance().removeTask(id, this);
     * </pre>
    */
    @Override
    public void run() {

        setup();

        while(stop.get() == false) {

            work();
        }

        terminate();
        TaskManager.getInstance().removeTask(ID, this);
    }

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