package io.github.clamentos.blackhole.web;

//________________________________________________________________________________________________________________________________________

import java.io.IOException;
import java.net.ServerSocket;

//________________________________________________________________________________________________________________________________________

public class Server {
    
    private final int PORT;
    private RequestPool request_pool;
    private ServerSocket server_socket;

    //____________________________________________________________________________________________________________________________________

    public Server(int port, RequestPool request_pool) {

        PORT = port;
        this.request_pool = request_pool;
    }

    //____________________________________________________________________________________________________________________________________

    public void start() throws IOException {

        server_socket = new ServerSocket(PORT);

        while(true) {

            request_pool.add(server_socket.accept());
        }
    }

    public void stop() {

        // TODO: ...
    }

    //____________________________________________________________________________________________________________________________________
}
