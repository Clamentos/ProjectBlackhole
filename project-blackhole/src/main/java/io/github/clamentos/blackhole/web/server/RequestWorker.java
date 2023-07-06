package io.github.clamentos.blackhole.web.server;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.config.ConfigurationProvider;
import io.github.clamentos.blackhole.common.exceptions.Error;
import io.github.clamentos.blackhole.common.exceptions.ErrorWrapper;
import io.github.clamentos.blackhole.common.framework.Worker;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.web.dtos.Response;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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

        BufferedInputStream in;
        BufferedOutputStream out;
        byte[] data;
        
        try {

            socket.setSoTimeout(ConfigurationProvider.CONNECTION_TIMEOUT);
            in = new BufferedInputStream(socket.getInputStream(), ConfigurationProvider.STREAM_BUFFER_SIZE);
            out = new BufferedOutputStream(socket.getOutputStream(), ConfigurationProvider.STREAM_BUFFER_SIZE);

            data = read(in);

            if(data != null) {

                out.write(dispatcher.dispatch(data));
            }

            else {

                Response failure = Response.create(

                    "Could not properly read the request",
                    new ErrorWrapper(Error.BAD_FORMATTING)
                );

                out.write(failure.stream());
            }

            out.flush();
            in.close();
            out.close();
            socket.close();
        }

        catch(IOException exc) {

            LOGGER.log("RequestWorker.doWork > Could not dispatch the request, IOException: " + exc.getMessage(), LogLevel.WARNING);
        }
    }

    /**
     * {@inheritDoc}
    */
    @Override
    public void catchInterrupted(InterruptedException exc) {

        LOGGER.log("RequestWorker.doWork > Interrupted while waiting on queue, InterruptedException: " + exc.getMessage(), LogLevel.NOTE);
    }

    //____________________________________________________________________________________________________________________________________

    private byte[] read(BufferedInputStream in) {

        int data_length;
        int temp;
        byte[] data;

        try {

            data_length = 0;

            for(int i = 3; i >= 0; i--) {

                temp = in.read();

                if(temp == -1) {

                    return(null);
                }

                data_length = data_length | (temp << i);
            }

            data = in.readNBytes(data_length);

            if(data.length != data_length) {

                return(null);
            }

            return(data);
        }

        catch(IOException exc) {

            LOGGER.log("RequestWorker.doWork > Could not dispatch the request, IOException: " + exc.getMessage(), LogLevel.WARNING);
            return(null);
        }
    }

    //____________________________________________________________________________________________________________________________________
}
