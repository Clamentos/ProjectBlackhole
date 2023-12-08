package io.github.clamentos.blackhole.framework.implementation.tasks;

///
import io.github.clamentos.blackhole.framework.implementation.configuration.ConfigurationProvider;

///..
import io.github.clamentos.blackhole.framework.implementation.logging.LogLevels;
import io.github.clamentos.blackhole.framework.implementation.logging.LogPrinter;
import io.github.clamentos.blackhole.framework.implementation.logging.LogTask;
import io.github.clamentos.blackhole.framework.implementation.logging.MetricsTask;

///..
import io.github.clamentos.blackhole.framework.implementation.network.tasks.RequestTask;
import io.github.clamentos.blackhole.framework.implementation.network.tasks.ServerTask;
import io.github.clamentos.blackhole.framework.implementation.network.tasks.TransferTask;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.ExceptionFormatter;

///.
import java.util.Iterator;
import java.util.Map;

///..
import java.util.concurrent.ConcurrentHashMap;

///
/**
 * <h3>Task manager</h3>
 * This class is dedicated to managing all the currently running tasks.
*/
public final class TaskManager {

    ///
    /** Singleton instance of {@code this} instantiated during class loading. */
    private static final TaskManager INSTANCE = new TaskManager();

    ///.
    private final int TASK_MANAGER_SLEEP_CHUNK_SIZE;

    ///..
    /** The service used to log notable events. */
    private final LogPrinter log_printer;

    ///..
    /**
     * The buffer holding the currently active transfer tasks.
     * @see TransferTask
    */
    private final Map<TransferTask, TransferTask> transfer_tasks;

    /**
     * The buffer holding the currently active request tasks.
     * @see RequestTask
    */
    private final Map<RequestTask, RequestTask> request_tasks;

    ///..
    /**
     * The currently active server task.
     * @see ServerTask
    */
    private ServerTask server_task;

    /**
     * The currently active metrics task.
     * @see MetricsTask
    */
    private MetricsTask metrics_task;

    /**
     * The currently active log task.
     * @see LogTask
    */
    private LogTask log_task;

    ///
    /**
     * <p>Instantiates a new {@code TaskManager} object.</p>
     * Since this class is a singleton, this constructor will only be called once.
    */
    private TaskManager() {

        log_printer = LogPrinter.getInstance();

        TASK_MANAGER_SLEEP_CHUNK_SIZE = ConfigurationProvider.getInstance().TASK_MANAGER_SLEEP_CHUNK_SIZE;

        transfer_tasks = new ConcurrentHashMap<>();
        request_tasks = new ConcurrentHashMap<>();
    }

    ///
    /** @return The {@link TaskManager} instance created during class loading. */
    public static TaskManager getInstance() {

        return(INSTANCE);
    }

    ///
    /**
     * Launches a new virtual thread with the provided parameters.
     * @param runnable : The runnable to be executed by the new thread.
     * @param name : The name of the new thread.
     * @throws IllegalArgumentException If {@code runnable} is {@code null}.
    */
    public Thread launchThread(Runnable runnable, String name) throws IllegalArgumentException {

        if(runnable == null) {

            throw new IllegalArgumentException("(TaskManager.launchThread) -> The argument \"runnable\" cannot be null");
        }

        Thread thread = Thread.ofVirtual().start(runnable);
        thread.setName(name);

        return(thread);
    }

    ///..
    /** @return An estimation of the total number of virtual threads currently active. */
    public int getVirtualThreadCount() {

        int count = 0;

        count += transfer_tasks.size();
        count += request_tasks.size();

        if(server_task != null) count++;
        if(metrics_task != null) count++;
        if(log_task != null) count++;

        return(count);
    }

    ///.
    /**
     * Adds the provided runnable to the internal tracking buffers.
     * @param runnable : The runnable to run.
     * @throws IllegalArgumentException If {@code runnable} is {@code null} or is not a
     * server task, metrics task, log task, transfer task or request task.
     * @see ServerTask
     * @see MetricsTask
     * @see LogTask
     * @see TransferTask
     * @see RequestTask
    */
    protected void add(Runnable runnable) throws IllegalArgumentException {

        switch(runnable) {

            case ServerTask st -> {

                synchronized(server_task) {

                    if(server_task == null) {

                        server_task = st;
                    }
                }
            }

            case MetricsTask mt -> {

                synchronized(metrics_task) {

                    if(metrics_task == null) {

                        metrics_task = mt;
                    }
                }
            }

            case LogTask lt -> {

                synchronized(log_task) {

                    if(log_task == null) {

                        log_task = lt;
                    }
                }
            }

            case TransferTask tt -> transfer_tasks.put(tt, tt);
            case RequestTask rt -> request_tasks.put(rt, rt);

            case null -> throw new IllegalArgumentException("(TaskManager.add) -> The argument \"runnable\" cannot be null");

            default -> throw new IllegalArgumentException(

                "(TaskManager.add) -> Unknown runnable type: " + runnable.getClass().getSimpleName()
            );
        }
    }

    ///..
    /**
     * Removes the provided runnable to the internal tracking buffers.
     * @param runnable : The runnable to remove.
     * @throws IllegalArgumentException If {@code runnable} is {@code null} or is not a
     * server task, metrics task, log task, transfer task or request task.
     * @see ServerTask
     * @see MetricsTask
     * @see LogTask
     * @see TransferTask
     * @see RequestTask
    */
    protected void remove(Runnable runnable) throws IllegalArgumentException {
        
        switch(runnable) {

            case ServerTask st -> {

                synchronized(server_task) {

                    server_task = null;
                }
            }

            case MetricsTask mt -> {

                synchronized(metrics_task) {

                    metrics_task = null;
                }
            }

            case LogTask lt -> {

                synchronized(log_task) {

                    log_task = null;
                }
            }

            case TransferTask tt -> transfer_tasks.remove(tt);
            case RequestTask rt -> request_tasks.remove(rt);

            case null -> throw new IllegalArgumentException("(TaskManager.remove) -> The argument \"runnable\" cannot be null");

            default -> throw new IllegalArgumentException(

                "(TaskManager.remove) -> Unknown runnable type: " + runnable.getClass().getSimpleName()
            );
        }
    }

    ///..
    /**
     * Stops all tasks in the managing buffers in the following order:
     * <ol>
     *     <li>{@link ServerTask}.</li>
     *     <li>{@link TransferTask}.</li>
     *     <li>{@code RequestTask}.</li>
     *     <li>{@code MetricsTask}.</li>
     *     <li>{@code LogTask}.</li>
     * </ol>
    */
    protected void shutdown() {

        // Server task.
        if(server_task != null) server_task.stop();
        waitForNull(server_task);

        // Input transfer tasks.
        terminate(transfer_tasks.values().iterator());
        waitForEmpty(transfer_tasks);

        // Request tasks.
        waitForEmpty(request_tasks);

        // Metrics task.
        if(metrics_task != null) metrics_task.stop();
        waitForNull(metrics_task);

        // Log task.
        if(log_task != null) log_task.stop();
        waitForNull(log_task);
    }

    ///.
    // Waits for the element to become null, waiting in 500 ms chunks.
    private void waitForNull(Object object) {

        while(true) {

            if(object == null) {

                return;
            }

            try {

                Thread.sleep(TASK_MANAGER_SLEEP_CHUNK_SIZE);
            }

            catch(InterruptedException exc) {

                log_printer.logToFile(ExceptionFormatter.format("TaskManager.waitForNull >> ", exc, " >> Ignoring..."), LogLevels.NOTE);
            }
        }
    }

    ///..
    // Waits for the map to become empty, waiting in 500 ms chunks.
    private void waitForEmpty(Map<? extends Runnable, ? extends Runnable> map) {

        while(true) {

            if(map.size() == 0) {

                return;
            }

            try {

                Thread.sleep(TASK_MANAGER_SLEEP_CHUNK_SIZE);
            }

            catch(InterruptedException exc) {

                log_printer.logToFile(ExceptionFormatter.format("TaskManager.waitForEmpty >> ", exc, " >> Ignoring..."), LogLevels.NOTE);
            }
        }
    }

    ///..
    // Iterate the buffer and terminate the continuous tasks.
    private void terminate(Iterator<? extends ContinuousTask> iterator) {

        while(iterator.hasNext()) {

            ContinuousTask task = iterator.next();

            if(task != null) {

                task.stop();
            }
        }
    }

    ///
}
