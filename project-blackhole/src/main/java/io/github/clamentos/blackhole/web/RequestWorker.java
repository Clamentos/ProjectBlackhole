package io.github.clamentos.blackhole.web;

import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.LogPrinter;

import java.net.Socket;
import java.sql.Connection;
import java.util.concurrent.LinkedBlockingQueue;

public class RequestWorker extends Thread {
    
    private LinkedBlockingQueue<Socket> sockets;
    private Connection db_connection;

    public RequestWorker(LinkedBlockingQueue<Socket> sockets, Connection db_connection) {

        this.sockets = sockets;
        this.db_connection = db_connection;
    }

    @Override
    public void run() {

        while(true) {

            try {

                handle(sockets.take());
            }

            catch(InterruptedException exc) {

                LogPrinter.printToConsole(Thread.currentThread().getName() + " stopped", LogLevel.INFO);
                break;
            }
        }
    }

    private void handle(Socket socket) {

        //...
    }
}
