package io.github.clamentos.blackhole.scaffolding.tasks;

///
/**
 * <h3>Abstract ephimeral task</h3>
 * 
 * This class implements the {@link Runnable} interface and enforces common behaviour
 * for any {@link Runnable} that executes for a relatively brief amount of time.
*/
public abstract class Task implements Runnable {

    private final long ID;

    ///
    /**
     * Instantiates a new {@link Task} object.
     * @param id : The task id.
    */
    public Task(long id) {

        ID = id;
    }

    ///
    /** Method to perform the operations. */
    public abstract void work();

    ///
    /**
     * <p>Main execution method.</p>
     * 
     * This method will perform the following:
     * <blockquote><pre>
     *work();
     *TaskManager.getInstance().removeTask(id, this);
     * </pre></blockquote>
    */
    @Override
    public void run() {

        work();
        TaskManager.getInstance().removeTask(ID, this);
    }

    ///
    /** @return The task id. */
    public long getId() {

        return(ID);
    }

    ///
}
