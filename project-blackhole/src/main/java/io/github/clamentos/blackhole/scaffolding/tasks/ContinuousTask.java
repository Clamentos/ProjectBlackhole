package io.github.clamentos.blackhole.scaffolding.tasks;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.scaffolding.Stoppable;
import java.util.concurrent.atomic.AtomicBoolean;

//________________________________________________________________________________________________________________________________________

/**
 * <h3>Abstract continuous task</h3>
 * 
 * This class implements the {@link Stoppable} interface and enforces common behaviour
 * for any {@link Runnable} that executes for a very long (potentially infinite) time.
 * 
 * @apiNote This class is an <b>Abstract behavioural class</b>.
*/
public abstract class ContinuousTask implements Stoppable {
    
    private final long ID;
    private AtomicBoolean stop;

    //____________________________________________________________________________________________________________________________________

    /**
     * Instantiates a new {@link ContinuousTask} object.
     * @param id : The task id.
    */
    public ContinuousTask(long id) {

        ID = id;
        stop = new AtomicBoolean(false);
    }

    //____________________________________________________________________________________________________________________________________

    /** @return The stopped status flag. */
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

    /** Sets the stop flag of {@code this}. */
    @Override
    public void stop() {

        stop.set(true);
    }

    //____________________________________________________________________________________________________________________________________
}