package io.github.clamentos.blackhole.web.connection;

import java.net.Socket;

public class ConnectionTask implements Runnable {

    private Socket client_socket;

    public ConnectionTask(Socket client_socket) {

        this.client_socket = client_socket;
    }
    
    // listen on the passed socket for new data
    // if new request comes in, spawn request task
    @Override
    public void run() {

        // Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());

        //...
    }
}
