package io.github.clamentos.blackhole.web.server;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.config.ConfigurationProvider;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.web.dtos.ResponseStatus;
import io.github.clamentos.blackhole.web.servlets.Servlet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.sql.Connection;

import java.util.HashMap;

//________________________________________________________________________________________________________________________________________

/**
 * This class is responsible for propagating the request to the appropriate servlet.
*/
public class Dispatcher {

    private static volatile Dispatcher INSTANCE;
    private static Object dummy_mutex = new Object();

    private final Logger LOGGER;
    private HashMap<Byte, Servlet> servlets;

    //____________________________________________________________________________________________________________________________________

    private Dispatcher() {

        LOGGER = Logger.getInstance();
        servlets = new HashMap<>();
        Servlet[] temp = ConfigurationProvider.SERVLETS;

        for(Servlet servlet : temp) {

            servlets.put(servlet.matches(), servlet);
        }
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Get the Dispatcher instance.
     * @return The Dispatcher instance.
     */
    public static Dispatcher getInstance() {

        Dispatcher temp = INSTANCE;

        if(temp == null) {

            synchronized(dummy_mutex) {

                temp = INSTANCE;

                if(temp == null) {

                    temp = new Dispatcher();
                }
            }
        }

        return(temp);
    }

    public void dispatch(DataInputStream input_stream, DataOutputStream output_stream,  Connection db_connection) {

        byte resource_id;

        try {

            resource_id = input_stream.readByte();
            servlets.get(resource_id).handle(input_stream, output_stream, db_connection);
        }

        catch(IOException exc) {

            LOGGER.log("Could not read from socket stream, IOException: " + exc.getMessage(), LogLevel.WARNING);
        }

        catch(NullPointerException exc) {

            try {

                output_stream.write(ResponseStatus.ERROR.streamify());
            }

            catch(IOException exc2) {

                LOGGER.log("Could not write to socket stream, IOException: " + exc2.getMessage(), LogLevel.WARNING);
            }
        }
    }

    //____________________________________________________________________________________________________________________________________
}