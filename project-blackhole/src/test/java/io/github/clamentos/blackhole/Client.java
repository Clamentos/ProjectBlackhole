package io.github.clamentos.blackhole;

import java.io.IOException;
import java.net.Socket;

import io.github.clamentos.blackhole.framework.implementation.network.transfer.NetworkResponse;

// Simple client
public class Client {
    
    private Socket socket;
    private String address;
    private int port;
    private int requests_available;

    public Client(String address, int port, int requests_available) {

        this.address = address;
        this.port = port;
        this.requests_available = requests_available;
    }

    //...

    private Socket getSocket() throws IOException {

        if(socket == null || socket.isClosed()) {

            return(new Socket(address, port));
        }

        if(requests_available == 1) {

            // send "close connection request" (no response is returned)
            socket.getOutputStream().write(new byte[]{0,0,0,0});
            socket.close();
            requests_available--;

            return(new Socket(address, port));
        }

        return(socket);
    }
}
