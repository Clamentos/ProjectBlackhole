package io.github.clamentos.blackhole.web.request;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.framework.ContinuousTask;

import java.io.BufferedOutputStream;
import java.io.IOException;

//________________________________________________________________________________________________________________________________________

public class RequestTask extends ContinuousTask {

    private final Dispatcher DISPATCHER;

    private byte[] raw_request;
    private BufferedOutputStream out;
    private long id;

    //____________________________________________________________________________________________________________________________________

    public RequestTask(byte[] raw_request, BufferedOutputStream out, long id) {

        super();

        DISPATCHER = Dispatcher.getInstance();

        this.raw_request = raw_request;
        this.out = out;
        this.id = id;
    }

    //____________________________________________________________________________________________________________________________________
    
    @Override
    public void run() { // TODO: finish

        // NOTE: always check if socket is "OK", because it can be closed at ANY time...
        //       either by the client or by the COnnectionTask that launched this during shutdown

        try {

            out.write(DISPATCHER.dispatch(raw_request, out));
            out.flush();
        }

        catch(IOException exc) {

            //...
        }
    }

    //____________________________________________________________________________________________________________________________________
}
