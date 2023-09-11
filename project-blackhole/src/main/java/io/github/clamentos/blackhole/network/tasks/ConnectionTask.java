package io.github.clamentos.blackhole.network.tasks;

///
import io.github.clamentos.blackhole.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.exceptions.Failures;
import io.github.clamentos.blackhole.exceptions.FailuresWrapper;
import io.github.clamentos.blackhole.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.network.transfer.Response;
import io.github.clamentos.blackhole.scaffolding.tasks.ContinuousTask;
import io.github.clamentos.blackhole.scaffolding.tasks.TaskManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import java.util.NoSuchElementException;

///
/**
 * <h3>Client connection managing task</h3>
 * 
 * <p>This class is responsible for handling the client socket. It will listen for incoming requests and
 * spawn a new request task for each one of them.</p>
 * 
 * NOTE:
 * <ol>
 *     <li>The TCP connection must exist and must be alive.</li>
 *     <li>The sockets have a fixed number of requests that the client can transmit. If this limit is
 *         exceeded, they will be immediately closed.</li>
 *     <li>The client cannot transmit bytes at more than some milliseconds apart. This violation will cause
 *         the socket to be immediately closed.</li>
 * </ol>
 * 
 * @see {@link Socket}
 * @see {@link RequestTask}
 * @see {@link ConfigurationProvider#MAX_REQUESTS_PER_CLIENT}
 * @see {@link ConfigurationProvider#MIN_CLIENT_SPEED}
 * @apiNote This class is a <b>continuous runnable task</b>.
*/
public final class ConnectionTask extends ContinuousTask {

    private Logger logger;
    
    private final int STREAM_BUFFER_SIZE;
    private final int MAX_REQUESTS_PER_CLIENT;
    private final int CLIENT_SOCKET_SAMPLE_TIME;
    private final int MIN_CLIENT_SPEED;
    private final int MAX_CLIENT_SOCKET_SAMPLES;

    private BufferedInputStream in;
    private BufferedOutputStream out;
    private Socket client_socket;
    private int request_counter;

    ///
    /**
     * Instantiates a new {@link ConnectionTask} object.
     * @param client_socket : The client {@link Socket}.
     * @param id : The unique task id.
     * @throws IllegalArgumentException If {@code client_socket} is {@code null}.
    */
    public ConnectionTask(Socket client_socket, long id) throws IllegalArgumentException {

        super(id);

        logger = Logger.getInstance();

        STREAM_BUFFER_SIZE = ConfigurationProvider.getInstance().STREAM_BUFFER_SIZE;
        MAX_REQUESTS_PER_CLIENT = ConfigurationProvider.getInstance().MAX_REQUESTS_PER_CLIENT;
        CLIENT_SOCKET_SAMPLE_TIME = ConfigurationProvider.getInstance().CLIENT_SOCKET_SAMPLE_TIME;
        MIN_CLIENT_SPEED = ConfigurationProvider.getInstance().MIN_CLIENT_SPEED;
        MAX_CLIENT_SOCKET_SAMPLES = ConfigurationProvider.getInstance().MAX_CLIENT_SOCKET_SAMPLES;

        if(client_socket == null) {

            logger.log("ConnectionTask.new > Instantion failed: null client socket", LogLevel.ERROR);
            throw new IllegalArgumentException("Client socket cannot be null");
        }

        this.client_socket = client_socket;
        request_counter = 0;

        logger.log("ConnectionTask.new > Instantiated successfully", LogLevel.SUCCESS);
    }

    ///
    /** {@inheritDoc} */
    @Override
    public void setup() {

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
        checkSocket(client_socket);

        try {

            in = new BufferedInputStream(client_socket.getInputStream(), STREAM_BUFFER_SIZE);
            out = new BufferedOutputStream(client_socket.getOutputStream(), STREAM_BUFFER_SIZE);
            logger.log("ConnectionTask.setup > Started successfully", LogLevel.SUCCESS);
        }

        catch(IOException exc) {

            logger.log(
                    
                "ConnectionTask.setup > Could not aquire the socket streams IOException: " +
                exc.getMessage() + " Aborting this task",
                LogLevel.ERROR
            );

            super.stop();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void work() {

        byte[] data;
        int data_length;
        int retries;

        // Client is allowed only a certain number of requests per socket.
        if(request_counter < MAX_REQUESTS_PER_CLIENT) {

            retries = 0;
            checkSocket(client_socket);
            
            try {

                data_length = readMessageLength();

                if(data_length == 0) {

                    // The client signalled to close the connection.
                    super.stop();
                    return;
                }

                if(data_length > 0) {

                    data = in.readNBytes(data_length);

                    if(data.length != data_length) {

                        /* The readNBytes() wasn't able to fetch the specified number of bytes,
                        send a "bad request" response. */

                        out.write(new Response(
                            
                            new FailuresWrapper(Failures.BAD_FORMATTING),
                            MAX_REQUESTS_PER_CLIENT - request_counter,
                            "Request of length: " + data.length +
                            " doesn't match with the specified message length: " + data_length

                        ).stream());
                    }

                    else {

                        TaskManager.getInstance().launchNewRequestTask(data, out, request_counter);
                    }
                }

                else {

                    // TODO: Streaming mode.
                    // No need to create a new task since the socket stream will be unavailable
                    // for the entire request... Just handle it in-place here.

                    // this case is denoted by a negative message length value
                }

                request_counter++;
            }

            catch(Exception exc) {

                switch(exc) {

                    case IllegalStateException exc1 -> {

                        logger.log(

                            "ConnectionTask.run > Client socket was in an illegal state: " +
                            exc1.getMessage() + ". Closing the connection and terminating this task",
                            LogLevel.ERROR
                        );

                        super.stop();
                    }

                    case NoSuchElementException exc1 -> {

                        logger.log(
                        
                            "ConnectionTask.run > End of stream detected, closing the connection and terminating this task",
                            LogLevel.WARNING
                        );

                        super.stop();
                    }

                    // Thrown by the underlying methods if a protocol (TCP) error happens.
                    case SocketException exc1 -> {

                        logger.log(

                            "ConnectionTask.run > Could not use the socket, SocketException: " +
                            exc1.getMessage(),
                            LogLevel.ERROR
                        );

                        super.stop();
                    }

                    case SocketTimeoutException exc1 -> {

                        if(retries > MAX_CLIENT_SOCKET_SAMPLES) {

                            logger.log(
                        
                                "ConnectionTask.run > Timed out while reading from the client socket, closing the connection and terminating this task",
                                LogLevel.WARNING
                            );

                            super.stop();
                        }

                        else {

                            retries++;
                        }
                    }

                    case IOException exc1 -> {

                        logger.log(

                            "ConnectionTask.run > Could not process, " + exc1.getClass().getSimpleName() +
                            ": " + exc1.getMessage() +
                            ", closing the connection and terminating this task",
                            LogLevel.ERROR
                        );

                        super.stop();
                    }

                    default -> {

                        logger.log(
                    
                            "ConnectionTask.run > Unexpected " + exc.getClass().getSimpleName() + ": " +
                            exc.getMessage() + ", closing the connection and terminating this task",
                            LogLevel.ERROR
                        );

                        super.stop();
                    }
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void terminate() {

        try {

            client_socket.close();
        }

        catch(IOException exc) {

            logger.log(
                    
                "ConnectionTask.closeSocket > Could not close the socket, IOException: " +
                exc.getMessage(),
                LogLevel.ERROR
            );
        }

        logger.log("ConnectionTask.terminate > Shut down successfull", LogLevel.SUCCESS);
    }

    ///
    // Utility methods.

    private void checkSocket(Socket socket) throws IllegalStateException {

        boolean is_closed = client_socket.isClosed();
        boolean is_connected = client_socket.isConnected();

        if(is_closed || is_connected == false) {

            throw new IllegalStateException("closed = " + is_closed + ", connected = " + is_connected);
        }
    }

    private void checkEndOfStream(int value) throws NoSuchElementException, IOException {

        if(value == -1) {

            out.write(new Response(
                
                new FailuresWrapper(Failures.END_OF_STREAM),
                MAX_REQUESTS_PER_CLIENT - request_counter,
                "End of stream detected, closing the connection"
            
            ).stream());

            throw new NoSuchElementException();
        }
    }

    private int readMessageLength() throws NoSuchElementException, IOException {

        int temp;
        int data_length = 0;

        client_socket.setSoTimeout(CLIENT_SOCKET_SAMPLE_TIME);
        temp = in.read();
        checkEndOfStream(temp);
        data_length = data_length | (temp << 3);
        client_socket.setSoTimeout(MIN_CLIENT_SPEED);

        for(int i = 2; i >= 0; i--) {

            temp = in.read();
            checkEndOfStream(temp);
            data_length = data_length | (temp << i);
        }

        return(data_length);
    }

    ///
}
