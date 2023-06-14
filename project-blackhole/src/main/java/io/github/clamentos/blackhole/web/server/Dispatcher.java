package io.github.clamentos.blackhole.web.server;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.config.ConfigurationProvider;
import io.github.clamentos.blackhole.common.config.Container;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.web.dtos.DtoParser;
import io.github.clamentos.blackhole.web.dtos.Response;
import io.github.clamentos.blackhole.web.dtos.ResponseStatus;
import io.github.clamentos.blackhole.web.servlets.Servlet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

//________________________________________________________________________________________________________________________________________

/**
 * This class is responsible for propagating the request to the appropriate servlet.
*/
public class Dispatcher {

    private static volatile Dispatcher INSTANCE;
    private static ReentrantLock lock = new ReentrantLock();

    private final Logger LOGGER;
    private HashMap<Byte, Servlet> servlets;

    //____________________________________________________________________________________________________________________________________

    private Dispatcher() {

        LOGGER = Logger.getInstance();
        servlets = new HashMap<>();
        Servlet[] temp = Container.servlets;

        for(Servlet servlet : temp) {

            servlets.put(servlet.matches(), servlet);
        }

        LOGGER.log("Dispatcher instantiated", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</b></p>
     * Get the Dispatcher instance.
     * If the instance doesn't exist, create it with the values configured in
     * {@link ConfigurationProvider}
     * @return The Dispatcher instance.
     */
    public static Dispatcher getInstance() {

        Dispatcher temp = INSTANCE;

        if(temp == null) {

            lock.lock();
            temp = INSTANCE;

            if(temp == null) {

                INSTANCE = temp = new Dispatcher();
            }

            lock.unlock();
        }

        return(temp);
    }

    /**
     * <p><b>This method is thread safe.</b></p>
     * Dispatch the request to the proper servlet.
     * @param input_stream : The stream for reading data.
     * @param output_stream : The stream for writing data.
    */
    public void dispatch(DataInputStream input_stream, DataOutputStream output_stream) {

        Response response;
        
        try {

            response = servlets.get(input_stream.readByte()).handle(DtoParser.parseRequest(input_stream));
            output_stream.write(response.toBytes());
            output_stream.flush();
        }

        catch(IOException exc) {

            LOGGER.log("Could not read from socket stream, IOException: " + exc.getMessage(), LogLevel.WARNING);
        }

        catch(NullPointerException exc) {

            try {

                output_stream.write(ResponseStatus.ERROR.toBytes());
            }

            catch(IOException exc2) {

                LOGGER.log("Could not write to socket stream, IOException: " + exc2.getMessage(), LogLevel.WARNING);
            }
        }
    }

    //____________________________________________________________________________________________________________________________________
}