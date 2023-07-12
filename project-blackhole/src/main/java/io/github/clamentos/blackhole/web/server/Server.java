package io.github.clamentos.blackhole.web.server;

import io.github.clamentos.blackhole.common.configuration.Constants;
import io.github.clamentos.blackhole.common.utility.TaskLauncher;
import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;

public class Server {
    
    private static final Server INSTANCE = new Server();
    private final ConfigurationProvider CONFIGS = ConfigurationProvider.getInstance();

    private final int SERVER_PORT;
    private final int SOCKET_TIMEOUT;
    private final int MAX_START_RETRIES;

    private ServerTask task;

    private Server() {

        SERVER_PORT = CONFIGS.getConstant(Constants.SERVER_PORT, Integer.class);
        SOCKET_TIMEOUT = CONFIGS.getConstant(Constants.SOCKET_TIMEOUT, Integer.class);
        MAX_START_RETRIES = CONFIGS.getConstant(Constants.MAX_START_RETRIES, Integer.class);

        task = new ServerTask(SERVER_PORT, SOCKET_TIMEOUT, MAX_START_RETRIES);
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
