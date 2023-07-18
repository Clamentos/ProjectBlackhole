// OK
package io.github.clamentos.blackhole.common.framework;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.utility.TaskManager;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Abstract non continuous task.</p>
 * This class implements the {@link Runnable} interface and condenses
 * common code and operations for any {@link Runnable} that executes for
 * a relatively brief amount of time.
*/
public abstract class Task implements Runnable {

    private final long ID;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * @param id : The task id.
     * Instantiate a new {@link Task}.
    */
    public Task(long id) {

        ID = id;
    }

    //____________________________________________________________________________________________________________________________________

    /** Method to perform the operations. */
    public abstract void work();

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.
     * However, the called abstract methods might not.</p></b>
     * <p>Main execution method.</p>
     * <p>This method will perform the following:</p>
     * <pre>
     *     work();
     *     TaskManager.getInstance().removeTask(id, this);
     * </pre>
    */
    @Override
    public void run() {

        work();
        TaskManager.getInstance().removeTask(ID, this);
    }

    //____________________________________________________________________________________________________________________________________
}
