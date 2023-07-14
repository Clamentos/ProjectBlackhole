package io.github.clamentos.blackhole.web.request;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class RequestTask implements Runnable {

    private final Dispatcher DISPATCHER;

    private byte[] raw_request;
    private BufferedOutputStream out;

    public RequestTask(byte[] raw_request, BufferedOutputStream out) {

        DISPATCHER = Dispatcher.getInstance();

        this.raw_request = raw_request;
        this.out = out;
    }
    
    @Override
    public void run() {

        try {

            out.write(DISPATCHER.dispatch(raw_request, out));
            out.flush();
        }

        catch(IOException exc) {

            //...
        }
    }
}
