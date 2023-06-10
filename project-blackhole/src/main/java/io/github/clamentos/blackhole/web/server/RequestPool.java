package io.github.clamentos.blackhole.web.server;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.config.ConfigurationProvider;

import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

//________________________________________________________________________________________________________________________________________

/**
 * Pooling class that holds the request queue and request worker threads.
*/
public class RequestPool {

    private static volatile RequestPool INSTANCE;
    private static Object dummy_mutex = new Object();

    private RequestWorker[] request_workers;
    private LinkedBlockingQueue<Socket> requests;

    //____________________________________________________________________________________________________________________________________

    private RequestPool() throws InstantiationException {

        request_workers = new RequestWorker[ConfigurationProvider.REQUEST_WORKERS];
        requests = new LinkedBlockingQueue<>(ConfigurationProvider.MAX_REQUEST_QUEUE_SIZE);

        for(int i = 0; i < request_workers.length; i++) {

            request_workers[i] = new RequestWorker(requests);
            request_workers[i].start();
        }
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Get the RequestPool instance.
     * @return The RequestPool instance.
     */
    public static RequestPool getInstance() throws InstantiationException {

        RequestPool temp = INSTANCE;

        if(temp == null) {

            synchronized(dummy_mutex) {

                temp = INSTANCE;

                if(temp == null) {

                    temp = new RequestPool();
                }
            }
        }

        return(temp);
    }

    /**
     * Adds a new request (the socket itself) to the queue.
     * This method will block the thread current if there is no space in the queue.
     * @param socket : the accepted socket
     * @throws InterruptedException if the thread was interrupted while waiting.
     */
    public void add(Socket socket) throws InterruptedException {

        requests.put(socket);
    }

    //____________________________________________________________________________________________________________________________________
}
