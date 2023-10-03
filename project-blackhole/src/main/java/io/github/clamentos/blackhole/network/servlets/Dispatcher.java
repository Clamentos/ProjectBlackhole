package io.github.clamentos.blackhole.network.servlets;

///
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.network.transfer.Request;
import io.github.clamentos.blackhole.network.transfer.Response;
import io.github.clamentos.blackhole.network.transfer.components.Resources;
import io.github.clamentos.blackhole.scaffolding.Servlet;

import java.util.EnumMap;

///
/**
 * <h3>Request dispatcher</h3>
 * This class simply dispatches the incoming raw request to the appropriate mapped servlet.
*/
public class Dispatcher {
    
    private static final Dispatcher INSTANCE = new Dispatcher();
    private Logger logger;

    private EnumMap<Resources, Servlet> servlet_mappings;

    ///
    // Instantiate all the servlets and put them into a map depending on which resource they handle.
    private Dispatcher() {

        logger = Logger.getInstance();
        servlet_mappings = new EnumMap<>(Resources.class);

        ResourceServlet resource_servlet = ResourceServlet.getInstance();
        SystemServlet system_servlet = SystemServlet.getInstance();
        TagServlet tag_servlet = TagServlet.getInstance();
        TypeServlet type_servlet = TypeServlet.getInstance();
        UserServlet user_servlet = UserServlet.getInstance();

        servlet_mappings.put(resource_servlet.manages(), resource_servlet);
        servlet_mappings.put(system_servlet.manages(), system_servlet);
        servlet_mappings.put(tag_servlet.manages(), tag_servlet);
        servlet_mappings.put(type_servlet.manages(), type_servlet);
        servlet_mappings.put(user_servlet.manages(), user_servlet);
    }

    ///
    /** @return The {@link Dispatcher} instance created during class loading. */
    public static Dispatcher getInstance() {

        return(INSTANCE);
    }

    ///
    /**
     * Deserializes and dispatches the request to the target servlet.
     * 
     * @param raw_request : The raw input request bytes.
     * @return The raw response bytes.
    */
    public byte[] dispatch(byte[] raw_request, int request_counter, long task_id) {

        try {

            Request request = Request.deserialize(raw_request);
            Servlet servlet = servlet_mappings.get(request.resource());

            return(servlet.handle(request, request_counter, task_id).stream());
        }

        catch(IllegalArgumentException | IndexOutOfBoundsException exc) {

            logger.log(
                
                "Dispatcher.dispatch > Could not dispatch, " + exc.getClass().getSimpleName() + ": " +
                exc.getMessage() + " Responding with an error response",
                LogLevel.ERROR
            );

            return(new Response(exc.getCause(), request_counter, exc.getMessage()).stream());
        }
    }

    ///
}
