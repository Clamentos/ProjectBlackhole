package io.github.clamentos.blackhole.common.utility;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.logging.Log;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.LogTask;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.web.request.RequestTask;
import io.github.clamentos.blackhole.web.server.ConnectionTask;
import io.github.clamentos.blackhole.web.server.ServerTask;

import java.io.BufferedOutputStream;

import java.net.Socket;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>NOTE: preview features MUST be enabled.</p></b>
 * <p><b>Eager-loaded singleton.</b></p>
 * <p>Task manager.</p>
 * Utility class to launch and manage tasks with virtual threads.
*/
public class TaskManager {

    private static final TaskManager INSTANCE = new TaskManager();

    private final Logger LOGGER;

    private TaskBuffer<ServerTask> server_tasks_buffer;
    private TaskBuffer<ConnectionTask> connection_tasks_buffer;
    private TaskBuffer<RequestTask> request_tasks_buffer;
    private TaskBuffer<LogTask> log_tasks_buffer;

    //____________________________________________________________________________________________________________________________________

    private TaskManager() {

        LOGGER = Logger.getInstance();

        server_tasks_buffer = new TaskBuffer<>();
        connection_tasks_buffer = new TaskBuffer<>();
        request_tasks_buffer = new TaskBuffer<>();
        log_tasks_buffer = new TaskBuffer<>();
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
     * Create a new {@link ServerTask} and places it into a managing buffer.
     * Also launch a new virtual thread with the created task.
    */
    public void launchServerTask() {

        long id = server_tasks_buffer.getNextId();
        ServerTask task = new ServerTask(id);
        server_tasks_buffer.put(id, task);
        Thread.ofVirtual().start(task);
    }
    
    /**
     * <p><b>This method is thread safe.</p></b>
     * Create a new {@link ConnectionTask} and places it into a managing buffer.
     * Also launch a new virtual thread with the created task.
     * @param socket : The {@link Socket} used by the created task.
    */
    public void launchNewConnectionTask(Socket socket) {

        long id = connection_tasks_buffer.getNextId();
        ConnectionTask task = new ConnectionTask(socket, id);
        connection_tasks_buffer.put(id, task);
        Thread.ofVirtual().start(task);
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * Create a new {@link RequestTask} and places it into a managing buffer.
     * Also launch a new virtual thread with the created task.
     * @param raw_request : The request needed by the task.
     * @param out : The output buffer needed by the task.
    */
    public void launchNewRequestTask(byte[] raw_request, BufferedOutputStream out) {

        long id = request_tasks_buffer.getNextId();
        RequestTask task = new RequestTask(raw_request, out, id);
        request_tasks_buffer.put(id, task);
        Thread.ofVirtual().start(task);
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * Create a new {@link LogTask} and places it into a managing buffer.
     * Also launch a new virtual thread with the created task.
     * @param queue : The log queue used by the task.
    */
    public void launchNewLogTask(LinkedBlockingQueue<Log> queue) {

        long id = log_tasks_buffer.getNextId();
        LogTask task = new LogTask(queue, id);
        log_tasks_buffer.put(id, task);
        Thread.ofVirtual().start(task);
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * <p>Removes the {@link ConnectionTask} from the managing buffer.</p>
     * NOTE: this method should be called by the task itself
     * as a last operation just before terminating.
     * @param id : The id of the task to be removed.
     * @throw IllegalArgumentException If {@code task} is not {@link ServerTask},
     *        {@link ConnectionTask}, {@link RequestTask} or {@link LogTask}.
    */
    public void removeTask(long id, Runnable task) throws IllegalArgumentException {

        switch(task) {

            case ServerTask t -> server_tasks_buffer.remove(id);
            case ConnectionTask t -> connection_tasks_buffer.remove(id);
            case RequestTask t -> request_tasks_buffer.remove(id);
            case LogTask t -> log_tasks_buffer.remove(id);

            default -> throw new IllegalArgumentException();
        }
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * Shuts down all tasks.
    */
    public synchronized void shutdown() {

        Collection<ServerTask> t0 = server_tasks_buffer.getBufferedValues();

        for(ServerTask t : t0) {

            t.stop();
        }

        Collection<ConnectionTask> t1 = connection_tasks_buffer.getBufferedValues();

        for(ConnectionTask t : t1) {

            t.stop();
        }

        waitForEmptyness(connection_tasks_buffer.isEmpty());
        waitForEmptyness(request_tasks_buffer.isEmpty());

        Collection<LogTask> t3 = log_tasks_buffer.getBufferedValues();

        for(LogTask t : t3) {

            t.stop();
        }

        waitForEmptyness(log_tasks_buffer.isEmpty());
    }

    //____________________________________________________________________________________________________________________________________

    private void waitForEmptyness(boolean empty) {

        while(empty == false) {

            try {

                Thread.sleep(500);
            }

            catch(InterruptedException exc) {

                LOGGER.log(
                    
                    "TaskManager.waitForEmptyness > Interrupted while waiting",
                    LogLevel.WARNING
                );
            }
        }
    }

    //____________________________________________________________________________________________________________________________________
}
