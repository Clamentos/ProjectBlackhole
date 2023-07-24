// OK
package io.github.clamentos.blackhole.framework.web;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.common.exceptions.Failure;
import io.github.clamentos.blackhole.common.exceptions.Failures;
import io.github.clamentos.blackhole.common.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.framework.logging.LogLevel;
import io.github.clamentos.blackhole.framework.logging.Logger;
import io.github.clamentos.blackhole.framework.tasks.ContinuousTask;
import io.github.clamentos.blackhole.framework.tasks.TaskManager;
import io.github.clamentos.blackhole.framework.web.request.Response;

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
 * <p>This task will listen for incoming requests and spawn a {@link RequestTask}
 * for each oneof them.</p>
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
public final class ConnectionTask extends ContinuousTask {

    private Logger logger;
    private ConfigurationProvider configuration_provider;

    private BufferedInputStream in;
    private BufferedOutputStream out;
    private Socket client_socket;
    private int request_counter;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Instantiate a new {@link ConnectionTask} with the given client {@link Socket}.
     * @param client_socket : The client {@link Socket}.
     * @throws NullPointerException If {@code client_socket} is {@code null}.
    */
    public ConnectionTask(Socket client_socket, long id) throws NullPointerException {

        super(id);

        if(client_socket == null) throw new NullPointerException();

        logger = Logger.getInstance();
        configuration_provider = ConfigurationProvider.getInstance();

        this.client_socket = client_socket;

        request_counter = 0;
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * {@inheritDoc}
    */
    @Override
    public void setup() {

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
        checkSocket(client_socket);

        try {

            in = new BufferedInputStream(
                
                client_socket.getInputStream(),
                configuration_provider.STREAM_BUFFER_SIZE
            );
            
            out = new BufferedOutputStream(
                
                client_socket.getOutputStream(),
                configuration_provider.STREAM_BUFFER_SIZE
            );
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

    /**
     * <p><b>This method is thread safe.</p></b>
     * {@inheritDoc}
    */
    @Override
    public void work() {

        byte[] data;
        int data_length;
        int temp;
        int retries;

        if(request_counter < configuration_provider.MAX_REQUESTS_PER_CLIENT) {

            retries = 0;
            checkSocket(client_socket);
            
            try {

                data_length = 0;

                client_socket.setSoTimeout(configuration_provider.CLIENT_SOCKET_SAMPLE_TIME);
                temp = in.read();

                if(temp == -1) {

                    respond("End of stream detected, closing the connection", Failures.END_OF_STREAM);
                    closeSocket(client_socket);
                    super.stop();
                    
                    return;
                }

                data_length = data_length | (temp << 3);
                client_socket.setSoTimeout(configuration_provider.MIN_CLIENT_SPEED);

                for(int i = 2; i >= 0; i--) {

                    temp = in.read();

                    if(temp == -1) {

                        respond("End of stream detected, closing the connection", Failures.END_OF_STREAM);
                        closeSocket(client_socket);
                        super.stop();

                        return;
                    }

                    data_length = data_length | (temp << i);
                }

                if(data_length < 0) {

                    // The client signalled to close the connection.
                    closeSocket(client_socket);
                    super.stop();

                    return;
                }

                data = in.readNBytes(data_length);

                if(data.length != data_length) {

                    // the readNBytes() wasn't able to fetch the specified number of bytes
                    // send a "bad request" response

                    respond(

                        "Request of length: " + data.length +
                        " doesn't match with the specified message length: " + data_length,
                        Failures.BAD_FORMATTING
                    );
                }

                else {

                    TaskManager.getInstance().launchNewRequestTask(data, out);
                }
            }

            catch(Exception exc) {

                switch(exc) {

                    case IllegalStateException exc1 -> {

                        logger.log(

                            "ConnectionTask.run > Client socket was in an illegal state, closing the connection and aborting this task",
                            LogLevel.ERROR
                        );

                        super.stop();
                    }

                    case SocketException exc1 -> {

                        logger.log(
                    
                            "ConnectionTask.run > Could not set the timeout to the client socket, closing the connection and aborting this task",
                            LogLevel.ERROR
                        );

                        super.stop();
                    }

                    case SocketTimeoutException exc1 -> {

                        if(retries > configuration_provider.MAX_CLIENT_SOCKET_SAMPLES) {

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

                    default -> {

                        logger.log(
                    
                            "ConnectionTask.run > Unexpected " + exc.getClass().getSimpleName() + ": " +
                            exc.getMessage() + ", closing the connection and aborting this task",
                            LogLevel.ERROR
                        );

                        super.stop();
                    }
                }
            }

            request_counter++;
        }
    }

    /**
     * <p><b>This method is thread safe.</p></b>
     * {@inheritDoc}
    */
    @Override
    public void terminate() {

        closeSocket(client_socket);
        logger.log("ConnectionTask.terminate > Shut down successfull", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    // Thread safe.
    private void checkSocket(Socket socket) throws IllegalStateException {

        if(client_socket.isClosed() || client_socket.isConnected() == false) {

            throw new IllegalStateException();
        }
    }

    // Thread safe.
    private void closeSocket(Socket socket) {

        try {

            socket.close();
        }

        catch(IOException exc) {

            logger.log(
                    
                "ConnectionTask.closeSocket > Could not close the socket, IOException: " +
                exc.getMessage(),
                LogLevel.ERROR
            );
        }
    }

    // Thread safe.
    private void respond(String message, Failures failure) throws IOException {

        out.write(Response.create(

            message,
            new Failure(failure)
                            
        ).stream());
    }

    //____________________________________________________________________________________________________________________________________
}
