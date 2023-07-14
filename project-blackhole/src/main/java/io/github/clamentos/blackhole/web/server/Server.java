package io.github.clamentos.blackhole.web.server;

import io.github.clamentos.blackhole.common.utility.TaskLauncher;

public class Server {
    
    private static final Server INSTANCE = new Server();
    private ServerTask task;

    private Server() {

        task = new ServerTask();
    }

    public static Server getInstance() {

        return(INSTANCE);
    }

    public Thread start() {

        return(TaskLauncher.launch(task));
    }

    public void stop() {

        task.stop();
    }
}
