package io.github.clamentos.blackhole.web.request;

import java.io.BufferedOutputStream;
import io.github.clamentos.blackhole.logging.Logger;

public class Dispatcher {
    
    private static final Dispatcher INSTANCE = new Dispatcher();
    private final Logger LOGGER;

    //map...

    private Dispatcher() {

        LOGGER = Logger.getInstance();
    }

    public static Dispatcher getInstance() {

        return(INSTANCE);
    }

    public byte[] dispatch(byte[] raw_request, BufferedOutputStream out) {

        //...
        return(null);
    }
}
