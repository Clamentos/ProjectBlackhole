package io.github.clamentos.blackhole.framework.implementation.tasks;

///
import io.github.clamentos.blackhole.framework.implementation.configuration.ConfigurationProvider;

///..
import io.github.clamentos.blackhole.framework.implementation.logging.LogPrinter;
import io.github.clamentos.blackhole.framework.implementation.logging.LogTask;
import io.github.clamentos.blackhole.framework.implementation.logging.MetricsTask;

///..
import io.github.clamentos.blackhole.framework.implementation.logging.exportable.LogLevels;

///..
import io.github.clamentos.blackhole.framework.implementation.network.tasks.RequestTask;
import io.github.clamentos.blackhole.framework.implementation.network.tasks.ServerTask;
import io.github.clamentos.blackhole.framework.implementation.network.tasks.TransferTask;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.exportable.ExceptionFormatter;

///.
import java.util.Iterator;
import java.util.Map;

///..
import java.util.concurrent.ConcurrentHashMap;

///..
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

///
/**
 * <h3>Task Manager</h3>
 * Manages all the currently running tasks.
*/
public final class TaskManager {

    ///
    /** Singleton instance of {@code this} instantiated during class loading. */
    private static final TaskManager INSTANCE = new TaskManager();

    ///.
    /** The sleep chunk size in milliseconds, used while waiting task termination. */
    private final int TASK_MANAGER_SLEEP_CHUNK_SIZE;

    ///..
    /** The service used to log notable events. */
    private final LogPrinter log_printer;

    ///..
    /** Synchronization locks for "singleton" type tasks. These locks are used when instantiating said tasks. */
    private final Lock[] add_locks;

    /** Synchronization locks for "singleton" type tasks. These locks are used when terminating said tasks. */
    private final Lock[] remove_locks;

    ///..
    /** The buffer holding the currently active transfer tasks. */
    private final Map<TransferTask, TransferTask> transfer_tasks;

    /** The buffer holding the currently active request tasks. */
    private final Map<RequestTask, RequestTask> request_tasks;

    ///..
    /** The thread that executed the {@code .start(...)} method of {@code ApplicationStarter}. */
    private volatile Thread main;
    
    /** The currently active server task. */
    private volatile ServerTask server_task;

    /** The currently active metrics task. */
    private volatile MetricsTask metrics_task;

    /** The currently active log task. */
    private volatile LogTask log_task;

    ///
    /**
     * Instantiates a new {@code TaskManager} object.
     * @apiNote Since this class is a singleton, this constructor will only be called once.
    */
    private TaskManager() {

        log_printer = LogPrinter.getInstance();

        TASK_MANAGER_SLEEP_CHUNK_SIZE = ConfigurationProvider.getInstance().TASK_MANAGER_SLEEP_CHUNK_SIZE;

        add_locks = new Lock[3];
        remove_locks = new Lock[3];

        for(int i = 0; i < 3; i++) {

            add_locks[i] = new ReentrantLock();
            remove_locks[i] = new ReentrantLock();
        }

        transfer_tasks = new ConcurrentHashMap<>();
        request_tasks = new ConcurrentHashMap<>();
    }

    ///
    /** @return The {@link TaskManager} instance created during class loading. */
    public static TaskManager getInstance() {

        return(INSTANCE);
    }

    ///
    /** @return {@code true} if the calling thread is the main thread, {@code false} otherwise. */
    public boolean isMain() {

        return(Thread.currentThread() == main);
    }

    ///..
    /**
     * Launches a new virtual thread with the provided parameters.
     * @param runnable : The runnable to be executed by the new thread.
     * @param name : The name of the new thread.
     * @throws IllegalArgumentException If {@code runnable} is {@code null}.
    */
    public Thread launchThread(Runnable runnable, String name) throws IllegalArgumentException {

        if(runnable == null) {

            throw new IllegalArgumentException("TaskManager.launchThread -> The input argument \"runnable\" cannot be null");
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
     * Registers the main thread.
     * @param main : The main thread to register.
     * @throws IllegalStateException If the main thread is already registered.
    */
    protected void registerMain(Thread main) throws IllegalStateException {

        if(this.main != null) {
        
            throw new IllegalStateException("TaskManager.registerMain -> Main already registered");
        }

        this.main = main;
    }

    ///..
    /**
     * Adds the provided runnable to the internal tracking buffers.
     * @param runnable : The runnable to run.
     * @throws IllegalArgumentException If {@code runnable} is {@code null} or is not a
     * {@code ServerTask}, {@code MetricsTask}, {@code LogTask}, {@code TransferTask} or {@code RequestTask}.
    */
    protected void add(Runnable runnable) throws IllegalArgumentException {

        switch(runnable) {

            case ServerTask st -> {

                add_locks[0].lock();

                if(server_task == null) {

                    server_task = st;
                }

                add_locks[0].unlock();
            }

            case MetricsTask mt -> {

                add_locks[1].lock();

                if(metrics_task == null) {

                    metrics_task = mt;
                }

                add_locks[1].unlock();
            }

            case LogTask lt -> {

                add_locks[2].lock();

                if(log_task == null) {

                    log_task = lt;
                }

                add_locks[2].unlock();
            }

            case TransferTask tt -> transfer_tasks.put(tt, tt);
            case RequestTask rt -> request_tasks.put(rt, rt);

            case null -> throw new IllegalArgumentException("TaskManager.add -> The input argument \"runnable\" cannot be null");

            default -> throw new IllegalArgumentException(

                "TaskManager.add -> Unknown runnable type: " + runnable.getClass().getSimpleName()
            );
        }
    }

    ///..
    /**
     * Removes the provided runnable to the internal tracking buffers.
     * @param runnable : The runnable to remove.
     * @throws IllegalArgumentException If {@code runnable} is {@code null} or is not a
     * {@code ServerTask}, {@code MetricsTask}, {@code LogTask}, {@code TransferTask} or {@code RequestTask}.
    */
    protected void remove(Runnable runnable) throws IllegalArgumentException {

        switch(runnable) {

            case ServerTask st -> {

                remove_locks[0].lock();
                server_task = null;
                remove_locks[0].unlock();
            }

            case MetricsTask mt -> {

                remove_locks[1].lock();
                metrics_task = null;
                remove_locks[1].unlock();
            }

            case LogTask lt -> {

                remove_locks[2].lock();
                log_task = null;
                remove_locks[2].unlock();
            }

            case TransferTask tt -> transfer_tasks.remove(tt);
            case RequestTask rt -> request_tasks.remove(rt);

            case null -> throw new IllegalArgumentException("TaskManager.remove -> The input argument \"runnable\" cannot be null");

            default -> throw new IllegalArgumentException(

                "TaskManager.remove -> Unknown runnable type: " + runnable.getClass().getSimpleName()
            );
        }
    }

    ///..
    /**
     * Stops all tasks in the managing buffers in the following order:
     * <ol>
     *     <li>{@code ServerTask}.</li>
     *     <li>{@code TransferTask}.</li>
     *     <li>{@code RequestTask}.</li>
     *     <li>{@code MetricsTask}.</li>
     *     <li>{@code LogTask}.</li>
     * </ol>
    */
    protected void shutdown() {

        if(server_task != null) server_task.stop();
        waitForStopped(server_task);

        terminate(transfer_tasks.values().iterator());
        waitForEmpty(transfer_tasks);

        waitForEmpty(request_tasks);

        if(metrics_task != null) metrics_task.stop();
        waitForStopped(metrics_task);

        if(log_task != null) log_task.stop();
        waitForStopped(log_task);
    }

    ///.
    /**
     * Waits for the provided task to be stopped.
     * @param task : The task to wait for.
    */
    private void waitForStopped(ContinuousTask task) {

        while(true) {

            if(task == null || task.isStopped()) {

                return;
            }

            try {

                Thread.sleep(TASK_MANAGER_SLEEP_CHUNK_SIZE);
            }

            catch(InterruptedException exc) {

                log_printer.logToFile(

                    ExceptionFormatter.format("TaskManager.waitForStopped => Interrupted while waiting", exc, "Ignoring..."), LogLevels.NOTE
                );
            }
        }
    }

    ///..
    /**
     * Waits for the provided map to become empty.
     * @param map : The map to wait for.
    */
    private void waitForEmpty(Map<? extends Runnable, ? extends Runnable> map) {

        while(true) {

            if(map.size() == 0) {

                return;
            }

            try {

                Thread.sleep(TASK_MANAGER_SLEEP_CHUNK_SIZE);
            }

            catch(InterruptedException exc) {

                log_printer.logToFile(

                    ExceptionFormatter.format("TaskManager.waitForEmpty => Interrupted while waiting", exc, "Ignoring..."), LogLevels.NOTE
                );
            }
        }
    }

    ///..
    /**
     * Iterates the buffer and terminates the continuous tasks.
     * @param iterator : The iterator to iterate on.
    */
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
