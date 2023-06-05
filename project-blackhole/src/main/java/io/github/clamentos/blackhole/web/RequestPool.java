package io.github.clamentos.blackhole.web;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.ConfigurationProvider;

import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

//________________________________________________________________________________________________________________________________________

/**
 * This class holds the request queue and request worker threads.
 * Stereotype : singleton
*/
public class RequestPool {

    private static volatile RequestPool INSTANCE;
    private static Object dummy_mutex = new Object();

    private RequestWorker[] request_workers;
    private LinkedBlockingQueue<Socket> requests;

    //____________________________________________________________________________________________________________________________________

    /**
     * Instantiates a new RequestPool with the given parameters.
     * @param num_workers : Number of worker threads.
     * @param max_queue_size : Maximum number of items allowed in the request pool.
     * @param servlets : List of servlets used by the workers to dispatch the request.
     */
    private RequestPool(int num_workers, int max_queue_size, Servlet[] servlets) {

        request_workers = new RequestWorker[num_workers];
        requests = new LinkedBlockingQueue<>(max_queue_size);

        for(int i = 0; i < request_workers.length; i++) {

            request_workers[i] = new RequestWorker(requests, servlets);
            request_workers[i].setName("Request Worker " + i);
            request_workers[i].start();
        }
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Get the RequestPool instance.
     * @return The RequestPool instance.
     */
    public static RequestPool getInstance() {

        RequestPool temp = INSTANCE;

        if(temp == null) {

            synchronized(dummy_mutex) {

                temp = INSTANCE;

                if(temp == null) {

                    temp = new RequestPool(

                        ConfigurationProvider.REQUEST_WORKERS,
                        ConfigurationProvider.MAX_REQUEST_QUEUE_SIZE,
                        ConfigurationProvider.SERVLETS
                    );
                }
            }
        }

        return(temp);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Adds a new request (the socket itself) to the queue.
     * This method will block the thread current if there is no space in the queue.
     * @param socket : the accepted socket
     */
    public void add(Socket socket) {

        requests.add(socket);
    }

    //____________________________________________________________________________________________________________________________________
}
