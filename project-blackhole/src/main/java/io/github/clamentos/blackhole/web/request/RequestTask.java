package io.github.clamentos.blackhole.web.request;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.framework.Task;

import java.io.BufferedOutputStream;
import java.io.IOException;

//________________________________________________________________________________________________________________________________________

public class RequestTask extends Task {

    private final Dispatcher DISPATCHER;

    private byte[] raw_request;
    private BufferedOutputStream out;

    //____________________________________________________________________________________________________________________________________

    public RequestTask(byte[] raw_request, BufferedOutputStream out, long id) {

        super(id);

        DISPATCHER = Dispatcher.getInstance();

        this.raw_request = raw_request;
        this.out = out;
    }

    //____________________________________________________________________________________________________________________________________
    
    @Override
    public void work() { // TODO: finish

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
