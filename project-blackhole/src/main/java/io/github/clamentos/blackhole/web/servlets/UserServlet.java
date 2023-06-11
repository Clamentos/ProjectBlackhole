package io.github.clamentos.blackhole.web.servlets;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.util.concurrent.locks.ReentrantLock;

//________________________________________________________________________________________________________________________________________

public class UserServlet implements Servlet {

    private static volatile UserServlet INSTANCE;
    private static ReentrantLock lock = new ReentrantLock();

    private final Logger LOGGER;

    //____________________________________________________________________________________________________________________________________

    private UserServlet() {

        LOGGER = Logger.getInstance();
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Get the UserServlet instance.
     * If the instance doesn't exist, create it.
     * @return The UserServlet instance.
     */
    public static UserServlet getInstance() {

        UserServlet temp = INSTANCE;

        if(temp == null) {

            lock.lock();
            temp = INSTANCE;

            if(temp == null) {

                INSTANCE = temp = new UserServlet();
            }

            lock.unlock();
        }

        return(temp);
    }
    
    @Override
    public byte matches() {

        return(1);
    }

    @Override
    public void handle(DataInputStream input_stream, DataOutputStream output_stream) {

        try {

            byte request_method = input_stream.readByte();
            
            switch(request_method) {

                //...
            }
        }

        catch(Exception exc) {

            LOGGER.log("UserServlet", LogLevel.WARNING);
        }
    }

    //____________________________________________________________________________________________________________________________________
}
