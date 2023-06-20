package io.github.clamentos.blackhole.web.server;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.Worker;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;

import java.util.concurrent.BlockingQueue;

//________________________________________________________________________________________________________________________________________

/**
 * Worker thread that actually handles the socket connections.
*/
public class RequestWorker extends Worker<Socket> {

    private final Logger LOGGER;
    private Dispatcher dispatcher;

    //____________________________________________________________________________________________________________________________________

    /**
     * Instantiates a new log worker on the given sockets queue.
     * @param identifier : The worker identifier.
     * @param sockets_queue : The sockets queue on which the thread will consume and handle.
     */
    public RequestWorker(int identifier, BlockingQueue<Socket> sockets_queue) {

        super(identifier, sockets_queue);
        LOGGER = Logger.getInstance();
        dispatcher = Dispatcher.getInstance();
        LOGGER.log("Request worker started", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Method that dispatches the request.
     * @param socket : The socket to service.
    */
    @Override
    public void doWork(Socket socket) {

        DataInputStream in;
        DataOutputStream out;
        
        try {

            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            dispatcher.dispatch(in, out);
            socket.close();
        }

        catch(IOException exc) {

            LOGGER.log("Could not dispatch the request, IOException: " + exc.getMessage(), LogLevel.WARNING);
        }
    }

    /**
     * {@inheritDoc}
    */
    @Override
    public void catchInterrupted(InterruptedException exc) {

        LOGGER.log("Interrupted while waiting on queue, InterruptedException: " + exc.getMessage(), LogLevel.NOTE);
    }

    //____________________________________________________________________________________________________________________________________
}
