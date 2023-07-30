package io.github.clamentos.blackhole.framework.tasks;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.framework.Stoppable;
import java.util.concurrent.atomic.AtomicBoolean;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Abstract behavioural class.</b></p>
 * This class implements the {@link Stoppable} interface and enforces common behaviour
 * for any {@link Runnable} that executes for a very long (potentially infinite) time.
*/
public abstract class ContinuousTask implements Stoppable {
    
    private final long ID;
    private AtomicBoolean stop;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * @param id : The task id.
     * Instantiates a new {@link ContinuousTask} object.
    */
    public ContinuousTask(long id) {

        ID = id;
        stop = new AtomicBoolean(false);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * @return The stopped status flag.
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
     * <p><b>This method is thread safe. However, the called abstract methods might not.</p></b>
     * <p>Main execution method.</p>
     * This method will perform the following:
     * <blockquote><pre>
     *setup();
     * 
     *while(stop.get() == false) {
     *     
     *    work();
     *}
     * 
     *terminate();
     *TaskManager.getInstance().removeTask(id, this);
     * </pre></blockquote>
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