package io.github.clamentos.blackhole.web.server;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.config.ConfigurationProvider;
import io.github.clamentos.blackhole.common.config.Container;
import io.github.clamentos.blackhole.common.framework.Servlet;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.web.dtos.Request;
import io.github.clamentos.blackhole.web.dtos.Response;
import io.github.clamentos.blackhole.web.dtos.components.Entities;
import io.github.clamentos.blackhole.web.dtos.components.ResponseStatus;

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
    private HashMap<Entities, Servlet> servlets;

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
     * @param raw_request : The raw request from the input stream.
     * @return The raw response bytes.
    */
    public byte[] dispatch(byte[] raw_request) {

        Request request;

        try {

            request = new Request(raw_request);
            return(servlets.get(request.getEntityType()).handle(request).toBytes());
        }

        catch(IllegalArgumentException | ArrayIndexOutOfBoundsException exc) {

            LOGGER.log("Request was bad, " + exc.getClass().getSimpleName() + ": " + exc.getMessage(), LogLevel.NOTE);
            return(new Response(ResponseStatus.ERROR, null).toBytes());
        }
    }

    //____________________________________________________________________________________________________________________________________
}