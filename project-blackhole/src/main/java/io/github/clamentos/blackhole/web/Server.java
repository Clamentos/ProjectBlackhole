package io.github.clamentos.blackhole.web;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    
    private final int PORT;
    private RequestPool request_pool;
    private ServerSocket server_socket;

    public Server(int port, RequestPool request_pool) {

        PORT = port;
        this.request_pool = request_pool;
    }

    public void start() throws IOException {

        server_socket = new ServerSocket(PORT);

        while(true) {

            request_pool.add(server_socket.accept());
        }
    }
}
