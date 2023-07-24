package io.github.clamentos.blackhole.framework.web;

//________________________________________________________________________________________________________________________________________

import java.util.HashMap;

import io.github.clamentos.blackhole.framework.logging.Logger;
import io.github.clamentos.blackhole.framework.web.request.Request;
import io.github.clamentos.blackhole.framework.web.request.components.Resources;

//________________________________________________________________________________________________________________________________________

public class Dispatcher {
    
    private static final Dispatcher INSTANCE = new Dispatcher();
    private Logger logger;

    private HashMap<Resources, Servlet> servlet_mappings;

    //____________________________________________________________________________________________________________________________________

    private Dispatcher() {

        logger = Logger.getInstance();
        // TODO: instantiate all the available servlets
    }

    //____________________________________________________________________________________________________________________________________

    public static Dispatcher getInstance() {

        return(INSTANCE);
    }

    //____________________________________________________________________________________________________________________________________

    public byte[] dispatch(byte[] raw_request) {

        Servlet servlet = servlet_mappings.get(Resources.newInstance(raw_request[4]));

        if(servlet == null) {

            // this is bad
            // respond with bad things
        }

        return(servlet.handle(Request.deserialize(raw_request)).stream());
    }

    //____________________________________________________________________________________________________________________________________
}
