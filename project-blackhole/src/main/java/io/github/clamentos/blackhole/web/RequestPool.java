package io.github.clamentos.blackhole.web;

//________________________________________________________________________________________________________________________________________

import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

//________________________________________________________________________________________________________________________________________

public class RequestPool {

    private RequestWorker[] request_workers;
    private LinkedBlockingQueue<Socket> requests;
    private Dispatcher dispatcher;

    //____________________________________________________________________________________________________________________________________

    public RequestPool(int num_workers) {

        request_workers = new RequestWorker[num_workers];
        requests = new LinkedBlockingQueue<>();
        dispatcher = new Dispatcher();

        for(int i = 0; i < request_workers.length; i++) {

            // TODO: db connection
            request_workers[i] = new RequestWorker(requests, null, dispatcher);
            request_workers[i].setName("Request Worker " + i);
            request_workers[i].start();
        }
    }

    //____________________________________________________________________________________________________________________________________

    public void add(Socket socket) {

        requests.add(socket);
    }

    //____________________________________________________________________________________________________________________________________
}
