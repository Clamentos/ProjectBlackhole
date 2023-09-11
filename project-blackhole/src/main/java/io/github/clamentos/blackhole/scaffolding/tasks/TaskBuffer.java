package io.github.clamentos.blackhole.scaffolding.tasks;

///
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

///
/**
 * <h3>Task buffer</h3>
 * This class is responsible for holding the currently assigned alive tasks.
 * @param <T> The type of the task to hold.
 * @apiNote This class is a <b>Generic holder</b>.
*/
public class TaskBuffer<T extends Runnable> {

    private AtomicLong current_id;
    private ConcurrentHashMap<Long, T> buffer;

    ///
    /** Instantiates a new {@link TaskBuffer} object. */
    public TaskBuffer() {

        buffer = new ConcurrentHashMap<>();
        current_id = new AtomicLong(0);
    }

    ///
    /**  @return The next unique task id. Overflows silently wrap around. */
    public long getNextId() {

        return(current_id.getAndIncrement());
    }

    /**
     * Puts the specified task in the buffer.
     * @param id : The task id.
     * @param task : The task itself.
     * @throws NullPointerException If {@code task} is null.
    */
    public void put(long id, T task) throws NullPointerException {

        buffer.put(id, task);
    }

    /**
     * Removes the target task from the buffer.
     * @param id : The task id.
    */
    public void remove(long id) {

        buffer.remove(id);
    }

    /** @return The {@link Collection} of currently buffered tasks. */
    public Collection<T> getBufferedValues() {

        return(buffer.values());
    }

    /** @return The buffer empty flag. */
    public boolean isEmpty() {

        return(buffer.isEmpty());
    }

    ///
}
