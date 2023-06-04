package io.github.clamentos.blackhole.web;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.LogPrinter;

import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.util.concurrent.LinkedBlockingQueue;

//________________________________________________________________________________________________________________________________________

public class RequestWorker extends Thread {

    private LinkedBlockingQueue<Socket> sockets;
    private Connection db_connection;
    private Dispatcher dispatcher;

    //____________________________________________________________________________________________________________________________________

    public RequestWorker(LinkedBlockingQueue<Socket> sockets, Connection db_connection, Dispatcher dispatcher) {

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
        
        this.sockets = sockets;
        this.db_connection = db_connection;
        this.dispatcher = dispatcher;
    }

    //____________________________________________________________________________________________________________________________________

    @Override
    public void run() {

        while(true) {

            try {

                dispatcher.dispatch(sockets.take().getInputStream(), db_connection);
            }

            catch(InterruptedException exc) {

                LogPrinter.printToConsole(Thread.currentThread().getName() + " stopped", LogLevel.INFO);
                break;
            }

            catch(IOException exc) {

                //...
            }
        }
    }

    //____________________________________________________________________________________________________________________________________
}
