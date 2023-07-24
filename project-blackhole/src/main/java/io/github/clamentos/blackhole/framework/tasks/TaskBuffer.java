package io.github.clamentos.blackhole.framework.tasks;

//________________________________________________________________________________________________________________________________________

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Task buffer.</p>
 * This class is responsible for holding the current alive tasks.
 * @param <T> The type of the task to hold.
*/
public class TaskBuffer<T extends Runnable> {

    private ConcurrentHashMap<Long, T> buffer;
    private AtomicLong id;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Instantiate a new {@link TaskBuffer}.
    */
    public TaskBuffer() {

        buffer = new ConcurrentHashMap<>();
        id = new AtomicLong(0);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Get the next unique task id.
     * @return The next unique task id. Overflows don't cause any exception
     *         and simply wrap around.
    */
    public long getNextId() {

        return(id.getAndIncrement());
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * Put the specified task in the buffer.
     * @param id : The task id.
     * @param task : The task itself.
    */
    public void put(long id, T task) {

        buffer.put(id, task);
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * Remove the target task from the buffer.
     * @param id : The task id.
    */
    public void remove(long id) {

        buffer.remove(id);
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * Get the current set of tasks in the buffer.
     * @return The buffered tasks.
    */
    public Collection<T> getBufferedValues() {

        return(buffer.values());
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * Check if the buffer is empty or not.
     * @return The buffer empty flag.
    */
    public boolean isEmpty() {

        return(buffer.isEmpty());
    }

    //____________________________________________________________________________________________________________________________________
}
