package io.github.clamentos.blackhole.framework.tasks;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.logging.Log;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.LogPrinter;
import io.github.clamentos.blackhole.logging.LogTask;
import io.github.clamentos.blackhole.network.ConnectionTask;
import io.github.clamentos.blackhole.network.RequestTask;
import io.github.clamentos.blackhole.network.ServerTask;

import java.io.BufferedOutputStream;

import java.net.Socket;

import java.util.concurrent.BlockingQueue;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>NOTE: preview features MUST be enabled.</p></b>
 * <p><b>STEREOTYPE: Eager-loaded singleton.</b></p>
 * <p>Task manager.</p>
 * Utility class to launch and manage tasks with virtual threads.
*/
public final class TaskManager {

    private static final TaskManager INSTANCE = new TaskManager();

    private LogPrinter log_printer;

    private TaskBuffer<ServerTask> server_tasks_buffer;
    private TaskBuffer<ConnectionTask> connection_tasks_buffer;
    private TaskBuffer<RequestTask> request_tasks_buffer;
    private TaskBuffer<LogTask> log_tasks_buffer;

    //____________________________________________________________________________________________________________________________________

    // Thread safe.
    private TaskManager() {

        log_printer = LogPrinter.getInstance();

        server_tasks_buffer = new TaskBuffer<>();
        connection_tasks_buffer = new TaskBuffer<>();
        request_tasks_buffer = new TaskBuffer<>();
        log_tasks_buffer = new TaskBuffer<>();

        log_printer.log("TaskManager.new > Instantiated successfully", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * @return The {@link TaskManager} instance created during class loading.
    */
    public static TaskManager getInstance() {

        return(INSTANCE);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Creates a new {@link ServerTask} and places it into the managing buffer.
     * The method will also start a new virtual thread on the created task.
    */
    public void launchServerTask() {

        long id = server_tasks_buffer.getNextId();
        ServerTask task = new ServerTask(id);
        server_tasks_buffer.put(id, task);
        Thread.ofVirtual().start(task);
    }
    
    /**
     * <p><b>This method is thread safe.</p></b>
     * Creates a new {@link ConnectionTask} and places it into the managing buffer.
     * This method will also start a new virtual thread on the created task.
     * @param socket : The {@link Socket} used by the connection task.
    */
    public void launchNewConnectionTask(Socket socket) {

        long id = connection_tasks_buffer.getNextId();
        ConnectionTask task = new ConnectionTask(socket, id);
        connection_tasks_buffer.put(id, task);
        Thread.ofVirtual().start(task);
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * Creates a new {@link RequestTask} and places it into the managing buffer.
     * This method will also starts a new virtual thread on the created task.
     * @param raw_request : The request needed by the request task.
     * @param out : The output stream needed by the request task.
    */
    public void launchNewRequestTask(byte[] raw_request, BufferedOutputStream out) {

        long id = request_tasks_buffer.getNextId();
        RequestTask task = new RequestTask(raw_request, out, id);
        request_tasks_buffer.put(id, task);
        Thread.ofVirtual().start(task);
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * Creates a new {@link LogTask} and places it into the managing buffer.
     * This method will also launch a new virtual thread on the created task.
     * @param queue : The log queue used by the log task.
    */
    public void launchNewLogTask(BlockingQueue<Log> queue) {

        long id = log_tasks_buffer.getNextId();
        LogTask task = new LogTask(queue, id);
        log_tasks_buffer.put(id, task);
        Thread.ofVirtual().start(task);
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * <p>Removes a task from the managing buffer.</p>
     * @param id : The id of the task to be removed.
     * @throw IllegalArgumentException If {@code task} is not {@link ServerTask},
     *        {@link ConnectionTask}, {@link RequestTask} or {@link LogTask}.
    */
    protected void removeTask(long id, Runnable task) throws IllegalArgumentException {

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
     * Shuts down all tasks in proper order.
    */
    public synchronized void shutdown() {

        for(ServerTask t : server_tasks_buffer.getBufferedValues()) {

            t.stop();
        }

        waitForEmptyness(server_tasks_buffer);

        for(ConnectionTask t : connection_tasks_buffer.getBufferedValues()) {

            t.stop();
        }

        waitForEmptyness(connection_tasks_buffer);
        waitForEmptyness(request_tasks_buffer);

        for(LogTask t : log_tasks_buffer.getBufferedValues()) {

            t.stop();
        }

        waitForEmptyness(log_tasks_buffer);
    }

    //____________________________________________________________________________________________________________________________________

    // Waits untill the specified buffer is empty. Thread safe.
    private void waitForEmptyness(TaskBuffer<?> tasks) {

        while(tasks.isEmpty() == false) {

            try {

                Thread.sleep(500);
            }

            catch(InterruptedException exc) {

                log_printer.log(
                    
                    "TaskManager.waitForEmptyness > Interrupted while waiting",
                    LogLevel.WARNING
                );
            }
        }
    }

    //____________________________________________________________________________________________________________________________________
}
