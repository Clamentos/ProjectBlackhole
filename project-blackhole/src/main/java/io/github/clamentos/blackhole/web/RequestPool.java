package io.github.clamentos.blackhole.web;

import io.github.clamentos.blackhole.exceptions.GlobalExceptionHandler;

import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class RequestPool {
    
    private RequestWorker[] request_workers;
    private LinkedBlockingQueue<Socket> requests;

    public RequestPool(int num_workers) {

        request_workers = new RequestWorker[num_workers];
        requests = new LinkedBlockingQueue<>();

        for(int i = 0; i < request_workers.length; i++) {

            // TODO: db connection
            request_workers[i] = new RequestWorker(requests, null);
            request_workers[i].setName("Request Worker " + i);
            request_workers[i].setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
            request_workers[i].start();
        }
    }

    public void add(Socket socket) {

        synchronized(requests) {

            requests.add(socket);
            requests.notify();          // <<<<< it may create exception
        }
    }
}
