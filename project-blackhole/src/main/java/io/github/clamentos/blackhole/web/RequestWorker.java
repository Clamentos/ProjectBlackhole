package io.github.clamentos.blackhole.web;

import io.github.clamentos.blackhole.ConfigurationProvider;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.exceptions.GlobalExceptionHandler;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.LogPrinter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;

//________________________________________________________________________________________________________________________________________

public class RequestWorker extends Thread {

    private LinkedBlockingQueue<Socket> sockets;
    private Connection db_connection;
    private Dispatcher dispatcher;

    //____________________________________________________________________________________________________________________________________

    public RequestWorker(LinkedBlockingQueue<Socket> sockets, Servlet[] servlets) {

        Thread.currentThread().setUncaughtExceptionHandler(GlobalExceptionHandler.getInstance());
        
        this.sockets = sockets;
        dispatcher = new Dispatcher(servlets);
    }

    //____________________________________________________________________________________________________________________________________

    @Override
    public void run() {

        DataInputStream in;
        DataOutputStream out;
        Socket socket;

        try {

            db_connection = DriverManager.getConnection(

                ConfigurationProvider.DB_URL,
                ConfigurationProvider.DB_USERNAME,
                ConfigurationProvider.DB_PASWORD
            );
        }

        catch(SQLException exc) {

            //...
        }

        while(true) {

            try {

                socket = sockets.take();
                in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                dispatcher.dispatch(in, out, db_connection);
            }

            catch(InterruptedException exc) {

                LogPrinter.printToConsole(Thread.currentThread().getName() + " interrupted -> it will terminate", LogLevel.INFO);
                break;
            }

            catch(IOException exc) {

                //...
            }
        }
    }

    //____________________________________________________________________________________________________________________________________
}
