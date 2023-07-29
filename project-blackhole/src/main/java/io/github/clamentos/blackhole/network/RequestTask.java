package io.github.clamentos.blackhole.network;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.framework.tasks.Task;

import java.io.BufferedOutputStream;
import java.io.IOException;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>STEREOTYPE: Short-lived task.</b></p>
 * This class is responsible for actually processing a single request.
*/
public final class RequestTask extends Task {

    private Dispatcher dispatcher;

    private byte[] raw_request;
    private BufferedOutputStream out;

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * Instantiates a new {@link RequestTask} object.
     * @param raw_request : The raw request bytes.
     * @param out : The output stream of the socket.
     * @param id : The unique task id.
    */
    public RequestTask(byte[] raw_request, BufferedOutputStream out, long id) {

        super(id);

        dispatcher = Dispatcher.getInstance();

        this.raw_request = raw_request;
        this.out = out;
    }

    //____________________________________________________________________________________________________________________________________
    
    /**
     * <p><b>This method is thread safe.</p></b>
     * {@inheritDoc}
    */
    @Override
    public void work() { // TODO: finish

        // NOTE: always check if socket is "OK", because it can be closed at ANY time...
        //       either by the client or by the ConnectionTask that launched this during shutdown

        try {

            out.write(dispatcher.dispatch(raw_request));
        }

        catch(IOException exc) {

            //...
        }
    }

    //____________________________________________________________________________________________________________________________________
}
