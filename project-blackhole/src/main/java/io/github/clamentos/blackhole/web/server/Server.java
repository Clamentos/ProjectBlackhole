package io.github.clamentos.blackhole.web.server;

import io.github.clamentos.blackhole.TaskLauncher;
import io.github.clamentos.blackhole.common.configuration.Constants;
import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;

public class Server {
    
    private static final Server INSTANCE = new Server();

    private final int SERVER_PORT;
    //...

    private ServerTask task;

    private Server() {

        SERVER_PORT = ConfigurationProvider.getInstance().getConstant(Constants.SERVER_PORT, Integer.class);
        task = new ServerTask(SERVER_PORT);
    }

    public static Server getInstance() {

        return(INSTANCE);
    }

    public void start() {

        TaskLauncher.launch(task);
    }

    public void stop() {

        task.stop();
    }
}
