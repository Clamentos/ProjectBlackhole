package io.github.clamentos.blackhole.web.connection;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.common.configuration.Constants;
import io.github.clamentos.blackhole.common.exceptions.Failure;
import io.github.clamentos.blackhole.common.exceptions.Failures;
import io.github.clamentos.blackhole.common.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.common.utility.TaskLauncher;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.web.request.RequestTask;
import io.github.clamentos.blackhole.web.request.Response;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Connection task.</p>
 * <p>This class is responsible for handling the client {@link Socket}.</p>
 * <p>This task will listen for incoming requests and spawn a {@link RequestTask} for each one
 * of them.</p>
 * <b>NOTE:</b>
 * <ol>
 *     <li>The TCP connection must exist and must be alive.</li>
 *     <li>The sockets have a fixed number of requests that the client can transmit.
 *         If this limit is exceeded, they will be closed.
 *         See {@link Constants#MAX_REQUESTS_PER_SOCKET}.</li>
 *     <li>The client cannot transmit bytes at more than {@link Constants#MIN_CLIENT_SPEED}
 *         milliseconds apart. This violation will cause the socket to be closed.</li>
 * </ol>
*/
public class ConnectionTask implements Runnable {

    private final Logger LOGGER;
    private final ConfigurationProvider CONFIGS;

    private final int SOCKET_TIMEOUT;
    private final int MAX_REQUESTS_PER_SOCKET;
    private final int MIN_CLIENT_SPEED;
    private final int STREAM_BUFFER_SIZE;

    private Socket client_socket;
    private int request_counter;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Instantiates a new {@link ConnectionTask} with the given client {@link Socket}.
     * @param client_socket : The client {@link Socket}.
     * @throws NullPointerException If {@code client_socket} is {@code null}.
    */
    public ConnectionTask(Socket client_socket) throws NullPointerException {

        if(client_socket == null) throw new NullPointerException();

        LOGGER = Logger.getInstance();
        CONFIGS = ConfigurationProvider.getInstance();

        SOCKET_TIMEOUT = CONFIGS.getConstant(Constants.SOCKET_TIMEOUT, Integer.class);
        MAX_REQUESTS_PER_SOCKET = CONFIGS.getConstant(Constants.MAX_REQUESTS_PER_SOCKET, Integer.class);
        MIN_CLIENT_SPEED = CONFIGS.getConstant(Constants.MIN_CLIENT_SPEED, Integer.class);
        STREAM_BUFFER_SIZE = CONFIGS.getConstant(Constants.STREAM_BUFFER_SIZE, Integer.class);

        this.client_socket = client_socket;
        request_counter = 0;
    }

    //____________________________________________________________________________________________________________________________________

    @Override
    public void run() {

        BufferedInputStream in;
        BufferedOutputStream out;
        byte[] data;
        int data_length;
        int temp;

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());

        // check if the socket is OK, otherwise quit
        if(client_socket.isClosed() || client_socket.isConnected() == false) {

            closeSocket(client_socket); // just to be sure...
            return;
        }

        try { // get the socket streams, otherwise quit

            in = new BufferedInputStream(client_socket.getInputStream(), STREAM_BUFFER_SIZE);
            out = new BufferedOutputStream(client_socket.getOutputStream(), STREAM_BUFFER_SIZE);
        }

        catch(IOException exc) {

            LOGGER.log(
                
                "ConnectionTask.run 1 > Could not get the socket streams, IOException: " +
                exc.getMessage(),
                LogLevel.ERROR
            );

            closeSocket(client_socket);
            return;
        }

        // client sockets can only transmit a fixed number of request before expiring.
        while(request_counter < MAX_REQUESTS_PER_SOCKET) {

            try {

                client_socket.setSoTimeout(SOCKET_TIMEOUT);             // set a big timeout
                data_length = 0;

                // fetch the message length
                for(int i = 3; i >= 0; i--) {

                    temp = in.read();
                    client_socket.setSoTimeout(MIN_CLIENT_SPEED);       // set a small timeout

                    if(temp == -1) {

                        // end of stream reached, somebody closed it
                        // send a "shutting down" response and quit

                        out.write(Response.create(

                            "End of stream detected, closing the connection",
                            new Failure(Failures.END_OF_STREAM)
                        
                        ).stream());
                        out.flush();

                        return;
                    }

                    data_length = data_length | (temp << i);
                }

                if(data_length < 0) {

                    // the client signalled to close the connection

                    closeSocket(client_socket);
                    return;
                }

                data = in.readNBytes(data_length);

                if(data.length != data_length) {

                    // the readNBytes() wasn't able to fetch the specified number of bytes
                    // send a "bad request" response

                    out.write(Response.create(

                        "Request of length: " + data.length +
                        " doesn't match with the specified message length: " + data_length,
                        new Failure(Failures.BAD_FORMATTING)
                        
                    ).stream());
                    out.flush();
                }

                TaskLauncher.launch(new RequestTask(data, out));
            }

            // if any exception happen, catch them (they are all subclasses of IOException)
            catch(IOException exc) {

                if(exc instanceof SocketException) {

                    LOGGER.log(
                    
                        "ConnectionTask.run 2 > Could not set the socket timeout, SocketException: " +
                        exc.getMessage(),
                        LogLevel.ERROR
                    );

                    closeSocket(client_socket);
                    return;
                }

                if(exc instanceof SocketTimeoutException) {

                    LOGGER.log(
                    
                        "ConnectionTask.run 3 > Socket read timeout, SocketTimeoutException: " +
                        exc.getMessage(),
                        LogLevel.ERROR
                    );

                    closeSocket(client_socket);
                    return;
                }

                LOGGER.log(
                    
                    "ConnectionTask.run 4 > Could not dispatch the request, IOException: " +
                    exc.getMessage(),
                    LogLevel.WARNING
                );
            }

            request_counter++;
        }

        try {

            out.write(Response.create(

                "Requests exhausted for this socket",
                new Failure(Failures.TOO_MANY_REQUESTS)
                            
            ).stream());
            out.flush();
        }

        catch(IOException exc) {

            LOGGER.log(
                    
                "ConnectionTask.run 3 > Could not write the response, IOException: " +
                exc.getMessage(),
                LogLevel.WARNING
            );
        }
    }

    //____________________________________________________________________________________________________________________________________

    private void closeSocket(Socket socket) {

        try {

            socket.close();
        }

        catch(IOException exc) {

            LOGGER.log(
                    
                "ConnectionTask.closeSocket > Could not close the socket, IOException: " +
                exc.getMessage(),
                LogLevel.ERROR
            );
        }
    }

    //____________________________________________________________________________________________________________________________________
}
