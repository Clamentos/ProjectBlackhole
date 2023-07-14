package io.github.clamentos.blackhole.common.utility;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>NOTE: preview features MUST be enabled.</p></b>
 * <p>Task launcher.</p>
 * Simple utility class to launch tasks with virtual threads.
*/

@SuppressWarnings("preview")
public class TaskLauncher {

    //____________________________________________________________________________________________________________________________________
    
    /**
     * <p><b>This method is thread safe.</p></b>
     * Spawn a new virtual thread and assign it the specified task.
     * @param task : The task to run.
     * @return The {@link Thread} that is executing the task.
     * @throws IllegalArgumentException If {@code task} is {@code null}.
    */
    public static Thread launch(Runnable task) throws IllegalArgumentException {

        if(task == null) throw new IllegalArgumentException();
        return(Thread.ofVirtual().start(task));
    }

    //____________________________________________________________________________________________________________________________________
}
