package io.github.clamentos.blackhole.network;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.exceptions.Failure;
import io.github.clamentos.blackhole.common.exceptions.Failures;
import io.github.clamentos.blackhole.network.request.Request;
import io.github.clamentos.blackhole.network.request.Response;
import io.github.clamentos.blackhole.network.request.components.Resources;
import io.github.clamentos.blackhole.servlets.TagServlet;

import java.util.HashMap;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>STEREOTYPE: Eager-loaded singleton.</b></p>
 * <p>Request dispatching service.</p>
 * This class simply dispatches the incoming raw request to the appropriate servlet
*/
public class Dispatcher {
    
    private static final Dispatcher INSTANCE = new Dispatcher();
    private HashMap<Resources, Servlet> servlet_mappings;

    //____________________________________________________________________________________________________________________________________

    // Thread safe.
    private Dispatcher() {

        TagServlet tag_servlet = TagServlet.getInstance();

        servlet_mappings = new HashMap<>();
        servlet_mappings.put(tag_servlet.manages(), tag_servlet);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * <p><b>This method is thread safe.</p></b>
     * @return The {@link Dispatcher} instance created during class loading.
    */
    public static Dispatcher getInstance() {

        return(INSTANCE);
    }

    //____________________________________________________________________________________________________________________________________

    // TODO: finish
    public byte[] dispatch(byte[] raw_request) {

        Response response;
        Request request = Request.deserialize(raw_request);
        Servlet servlet = servlet_mappings.get(request.resource());

        if(servlet == null) {

            //...
        }

        try {

            response = servlet.handle(request);
        }

        catch(UnsupportedOperationException exc) {

            response = new Response(new Failure(Failures.UNSUPPORTED_METHOD), exc.getMessage());
        }

        return(response.stream());
    }

    //____________________________________________________________________________________________________________________________________
}
