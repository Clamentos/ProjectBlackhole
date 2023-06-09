package io.github.clamentos.blackhole.common.framework;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.web.dtos.Request;
import io.github.clamentos.blackhole.web.dtos.Response;
import io.github.clamentos.blackhole.web.dtos.components.Entities;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Servlet interface.</p>
 * <p>Servlets must implement this interface in order to be injected.</p>
*/
public interface Servlet {

    //____________________________________________________________________________________________________________________________________
    
    /**
     * Get the resource_id matching the servlet.
     * @return The (never null) matching reasource.
    */
    public Entities matches();

    /**
     * Handle the request.
     * @param request : The input request.
     * @return The (never null) response.
    */
    public Response handle(Request request);

    //____________________________________________________________________________________________________________________________________
}