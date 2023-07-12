package io.github.clamentos.blackhole.web.connection;

import java.net.Socket;

import io.github.clamentos.blackhole.common.exceptions.GlobalExceptionHandler;

public class ConnectionTask implements Runnable {

    private final int SOCKET_TIMEOUT;

    private Socket client_socket;
    private long socket_id;

    public ConnectionTask(Socket client_socket, long socket_id, int socket_timeout) {

        this.SOCKET_TIMEOUT = socket_timeout;

        this.client_socket = client_socket;
        this.socket_id = socket_id;
    }
    
    // listen on the passed socket for new data
    // if new request comes in, spawn request task
    @Override
    public void run() {

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());

        try {

            client_socket.setSoTimeout(SOCKET_TIMEOUT);

            // wait for incoming data
            // read the data
            // spawn 1 RequestTask per fetched request
        }

        catch(Exception exc) {

            //...
        }
    }
}
