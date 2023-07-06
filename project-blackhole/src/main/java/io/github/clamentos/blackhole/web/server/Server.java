package io.github.clamentos.blackhole.web.server;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.config.ConfigurationProvider;
import io.github.clamentos.blackhole.common.framework.WorkerManager;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;

import java.io.IOException;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Server class that listens and accepts socket requests.</p>
 * Once accepted, the sockets will be placed into a queue to be processed.
*/
public class Server extends WorkerManager<Socket, RequestWorker> {

    private static volatile Server INSTANCE;
    private static ReentrantLock lock = new ReentrantLock();

    private final Logger LOGGER;
    private boolean server_running;
    private ServerSocket server_socket;

    //____________________________________________________________________________________________________________________________________

    private Server(BlockingQueue<Socket> socket_queue, RequestWorker[] request_workers) {

        super(socket_queue, request_workers);
        LOGGER = Logger.getInstance();
        server_running = false;
        LOGGER.log("Web server instantiated and workers started", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Get the Server instance.
     * If the instance doesn't exist, create it with the values configured in
     * {@link ConfigurationProvider} and start the workers.
     * @return The Server instance.
    */
    public static Server getInstance() {

        Server temp = INSTANCE;

        LinkedBlockingQueue<Socket> socket_queue;
        RequestWorker[] request_workers;

        if(temp == null) {

            lock.lock();
            temp = INSTANCE;

            if(temp == null) {

                socket_queue = new LinkedBlockingQueue<>(ConfigurationProvider.MAX_REQUEST_QUEUE_SIZE);
                request_workers = new RequestWorker[ConfigurationProvider.NUM_REQUEST_WORKERS];

                for(int i = 0; i < request_workers.length; i++) {

                    request_workers[i] = new RequestWorker(i, socket_queue);
                    request_workers[i].start();
                }

                INSTANCE = temp = new Server(socket_queue, request_workers);
            }

            lock.unlock();
        }

        return(temp);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * <p>Starts the server and listens for requests.</p>
     * <p>If the server is already running, this method will return without doing anything.</p>
     * <p>If this method successfully starts the server, it will occupy the thread indefinetly.</p>
     * <p>If this method fails in starting the server, it will simply return.</p>
    */
    public void start() {

        if(attempt(ConfigurationProvider.MAX_SERVER_START_RETRIES) == true) {

            LOGGER.log("Web server started", LogLevel.SUCCESS);

            while(true) {

                try {

                    Socket s = server_socket.accept();
                    super.getResourceQueue().put(s);
                }

                catch(IOException exc) {

                    LOGGER.log(

                        "Server.start > Could not accept socket, " +
                        exc.getClass().getSimpleName() +
                        exc.getMessage(),
                        LogLevel.NOTE
                    );
                }

                catch(InterruptedException exc) {

                    if(server_running == false) {

                        LOGGER.log("Web server stopped", LogLevel.NOTE);
                        break;
                    }
                    
                    LOGGER.log(
                        
                        "Server.start > Interrupted while waiting on queue, InterruptedException: " +
                        exc.getMessage(),
                        LogLevel.NOTE
                    );
                }
            }
        }
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Stops the server only. No {@link RequestWorker} will be affected.
     * This method only sets the {@code server_running} flag to {@code false}.
     * In order to free the server thread from the loop, a signal,
     * via the {@code Thread.interrupt()} method, must be sent.
    */
    public synchronized void stopServer() {

        server_running = false;
    }

    //____________________________________________________________________________________________________________________________________

    // attemp to create the server socket with N retries
    private synchronized boolean attempt(int retries) {

        if(server_running == false) {

            for(int i = 0; i < retries; i++) {

                try {

                    server_socket = new ServerSocket(ConfigurationProvider.SERVER_PORT);
                    server_socket.setSoTimeout(ConfigurationProvider.CONNECTION_TIMEOUT);
                    server_running = true;

                    return(true);
                }

                catch(Exception exc) {
    
                    LOGGER.log(

                        "Server.attempt > Could not create server socket, " +
                        exc.getClass().getSimpleName() + ": " +
                        exc.getMessage(),
                        LogLevel.ERROR
                    );
                }

                try {

                    Thread.sleep(1000);
                }

                catch(InterruptedException exc) {

                    LOGGER.log(
                        
                        "Server.attempt > Interrupted while waiting on retries, InterruptedException: " +
                        exc.getMessage(),
                        LogLevel.INFO
                    );
                }
            }

            LOGGER.log(

                "Server.attempt > Retries exhausted while attempting to start the server",
                LogLevel.ERROR
            );
        }

        return(false);
    }

    //____________________________________________________________________________________________________________________________________

    // potential replacement for the current blocking
    public void testNIO() {

        Selector mux;
        ServerSocketChannel server_socket;
        Iterator<SelectionKey> keys;

        try {

            mux = Selector.open();

            server_socket = ServerSocketChannel.open();
            server_socket.configureBlocking(false);
            server_socket.register(mux, SelectionKey.OP_ACCEPT);
            server_socket.bind(new InetSocketAddress("127.0.0.1", 8080));   // do the retries

            while(true) {

                mux.select();
                keys = mux.selectedKeys().iterator();

                while(keys.hasNext()) {

                    SelectionKey selected = keys.next();

                    if(selected.isAcceptable() == true) {

                        SocketChannel client = server_socket.accept();
                        client.configureBlocking(false);
                        client.register(mux, SelectionKey.OP_READ);
                        client.socket().setSoTimeout(ConfigurationProvider.CONNECTION_TIMEOUT);
                    }

                    if(selected.isReadable() == true) {

                        // put into worker queue
                        //...
                    }

                    keys.remove();
                }
            }
        }

        catch(Exception exc) {

            LOGGER.log(

                "Server.testNIO > Could not serve channel, " +
                exc.getClass().getSimpleName() + ": " +
                exc.getMessage(),
                LogLevel.ERROR
            );
        }
    }
}