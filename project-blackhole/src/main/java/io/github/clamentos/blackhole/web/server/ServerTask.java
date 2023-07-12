package io.github.clamentos.blackhole.web.server;

public class ServerTask implements Runnable {

    private final int SERVER_PORT;
    private boolean running;

    protected ServerTask(int server_port) {

        SERVER_PORT = server_port;
        running = false;
    }

    @Override
    public void run() {

        // checks...

        running = true;

        while(running == true) {

            //...
        }
    }

    public void stop() {

        running = false;
    }
}
