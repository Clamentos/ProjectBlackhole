package io.github.clamentos.blackhole.framework.tasks;

//________________________________________________________________________________________________________________________________________

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>Generic class.</b></p>
 * <p>Task buffer.</p>
 * This class is responsible for holding the current alive tasks.
 * @param <T> The type of the task to hold.
*/
public class TaskBuffer<T extends Runnable> {

    private AtomicLong current_id;
    private ConcurrentHashMap<Long, T> buffer;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Instantiates a new {@link TaskBuffer} object.
    */
    public TaskBuffer() {

        buffer = new ConcurrentHashMap<>();
        current_id = new AtomicLong(0);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * @return The next unique task id. Overflows don't cause any exception and simply wrap around.
    */
    public long getNextId() {

        return(current_id.getAndIncrement());
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * Puts the specified task in the buffer.
     * @param id : The task id.
     * @param task : The task itself.
    */
    public void put(long id, T task) {

        buffer.put(id, task);
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * Removes the target task from the buffer.
     * @param id : The task id.
    */
    public void remove(long id) {

        buffer.remove(id);
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * @return The {@link Collection} of currently buffered tasks.
    */
    public Collection<T> getBufferedValues() {

        return(buffer.values());
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * @return The buffer empty flag.
    */
    public boolean isEmpty() {

        return(buffer.isEmpty());
    }

    //____________________________________________________________________________________________________________________________________
}
