package io.github.clamentos.blackhole.common.utility;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.logging.Log;
import io.github.clamentos.blackhole.logging.LogTask;
import io.github.clamentos.blackhole.web.connection.ConnectionTask;
import io.github.clamentos.blackhole.web.request.RequestTask;

import java.io.BufferedOutputStream;

import java.net.Socket;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>NOTE: preview features MUST be enabled.</p></b>
 * <p>Task manager.</p>
 * Utility class to launch and manage tasks with virtual threads.
*/
public class TaskManager {

    private static final TaskManager INSTANCE = new TaskManager();

    private ConcurrentHashMap<Long, ConnectionTask> connection_task_mappings;
    private ConcurrentHashMap<Long, RequestTask> request_task_mappings;
    private ConcurrentHashMap<Long, LogTask> log_task_mappings;

    private AtomicLong connection_task_id_generator;
    private AtomicLong request_task_id_generator;
    private AtomicLong log_task_id_generator;

    //____________________________________________________________________________________________________________________________________

    private TaskManager() {

        connection_task_mappings = new ConcurrentHashMap<>();
        request_task_mappings = new ConcurrentHashMap<>();
        log_task_mappings = new ConcurrentHashMap<>();

        connection_task_id_generator = new AtomicLong(0);
        request_task_id_generator = new AtomicLong(0);
        log_task_id_generator = new AtomicLong(0);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Get the {@link TaskManager} instance created during class loading.
     * @return The {@link TaskManager} instance.
    */
    public static TaskManager getInstance() {

        return(INSTANCE);
    }

    //____________________________________________________________________________________________________________________________________
    
    /**
     * <p><b>This method is thread safe.</p></b>
     * Creates a new {@link ConnectionTask} and places it into a managing buffer.
     * @param socket : The {@link Socket} used by the created task.
    */
    public void launchNewConnectionTask(Socket socket) {

        long id = connection_task_id_generator.getAndIncrement();
        ConnectionTask task = new ConnectionTask(socket, id);

        connection_task_mappings.put(id, task);
        Thread.ofVirtual().start(task);
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * <p>Removes the {@link ConnectionTask} from the managing buffer.</p>
     * NOTE: this method should be called by the task itself
     * as a last operation just before terminating.
     * @param id : The id of the task to be removed.
    */
    public void removeConnectionTask(long id) {

        connection_task_mappings.remove(id);
    }

    public void launchNewRequestTask(byte[] raw_request, BufferedOutputStream out) {

        long id = request_task_id_generator.getAndIncrement();
        RequestTask task = new RequestTask(raw_request, out, id);
        request_task_mappings.put(id, task);
        Thread.ofVirtual().start(task);
    }

    public void removeRequestTask(long id) {

        request_task_mappings.remove(id);
    }

    public void launchNewLogTask(LinkedBlockingQueue<Log> queue) {

        long id = log_task_id_generator.getAndIncrement();
        LogTask task = new LogTask(queue, id);
        log_task_mappings.put(id, task);
        Thread.ofVirtual().start(task);
    }

    public void removeLogTask(long id) {

        log_task_mappings.remove(id);
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * Shuts down all tasks and joins all the threads
     * that have ever been created with this class.
    */
    public synchronized void shutdown() {

        // At this point we know for sure that: ServerTask is stopped COMPLETELY.
        // It is safe to get the Collection<ConnectionTask>
        Collection<ConnectionTask> t1 = connection_task_mappings.values();

        for(ConnectionTask t : t1) {

            t.stop();
        }

        waitForEmptyness(connection_task_mappings);

        // At this point we know for sure that: all ConnectionTask are stopped COMPLETELY.
        // It is safe to get the Collection<RequestTask>
        Collection<RequestTask> t2 = request_task_mappings.values();

        for(RequestTask t : t2) {

            t.stop();
        }

        waitForEmptyness(request_task_mappings);

        // At this point we know for sure that: all tasks are stopped COMPLETELY (except the logging ones).
        // It is safe to get the Collection<LogTask>
        Collection<LogTask> t3 = log_task_mappings.values();

        for(LogTask t : t3) {

            t.stop();
        }

        waitForEmptyness(log_task_mappings);
    }

    //____________________________________________________________________________________________________________________________________

    private void waitForEmptyness(ConcurrentHashMap<?, ?> map) {

        while(map.isEmpty() == false) {

            try {

                Thread.sleep(500);
            }

            catch(InterruptedException exc) {

                //...
            }
        }
    }

    //____________________________________________________________________________________________________________________________________
}
