package io.github.clamentos.blackhole.web.servlets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.sql.Connection;

import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;

public class UserServlet implements Servlet {

    private static volatile UserServlet INSTANCE;
    private static Object dummy_mutex = new Object();
    private final Logger LOGGER;

    private UserServlet() {

        LOGGER = Logger.getInstance();
    }

    /**
     * Get the UserServlet instance.
     * If the instance doesn't exist, create it.
     * @return The UserServlet instance.
     */
    public static UserServlet getInstance() {

        UserServlet temp = INSTANCE;

        if(temp == null) {

            synchronized(dummy_mutex) {

                temp = INSTANCE;

                if(temp == null) {

                    temp = new UserServlet();
                    INSTANCE = temp;
                }
            }
        }

        return(temp);
    }
    
    @Override
    public byte matches() {

        return(1);
    }

    @Override
    public void handle(DataInputStream input_stream, DataOutputStream output_stream, Connection db_connection) {

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
}
