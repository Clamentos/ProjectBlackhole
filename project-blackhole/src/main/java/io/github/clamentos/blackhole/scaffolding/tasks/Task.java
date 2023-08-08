package io.github.clamentos.blackhole.scaffolding.tasks;

//________________________________________________________________________________________________________________________________________

/**
 * <h3>Abstract ephimeral task</h3>
 * 
 * This class implements the {@link Runnable} interface and enforces common behaviour
 * for any {@link Runnable} that executes for a relatively brief amount of time.
 * 
 * @apiNote This class is an <b>Abstract behavioural class</b>.
*/
public abstract class Task implements Runnable {

    private final long ID;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * @param id : The task id.
     * Instantiates a new {@link Task} object.
    */
    public Task(long id) {

        ID = id;
    }

    //____________________________________________________________________________________________________________________________________

    /** Method to perform the operations. */
    public abstract void work();

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe. However, the called abstract methods might not.</p></b>
     * <p>Main execution method.</p>
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

    //____________________________________________________________________________________________________________________________________
}
