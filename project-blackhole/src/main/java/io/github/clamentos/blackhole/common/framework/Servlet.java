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
     * Get the resource_id mathing the servlet.
     * @return The mathing reasource
    */
    public Entities matches();

    /**
     * Handle the request.
     * @param request : The raw input request.
     * @return The response.
    */
    public Response handle(Request request);

    //____________________________________________________________________________________________________________________________________
}

/*
 * the protocol:
 * 
 * request shape:
 * 
 *     |resource(1)|method(1)|session_id(32) [optional]|
 *     |data_entry(?)|
 * 
 *     each data_entry is structured:
 * 
 *         |type(1)|length(4) [optional]|data(?)|
 * 
 *     type values:
 * 
 *         0: byte -> length = 1
 *         1: short -> length = 2
 *         2: int -> length = 4
 *         3: long -> length = 8
 *         4: float -> length = 4
 *         5: double -> length = 8
 *         6: string -> length = use length(4)
 *         7: raw -> length = use length(4)
 *         8: array -> length = number of elements. No data for this entry
 * 
 *  
 * 
 * response shape:
 * 
 *     |status(1)|data_entry(?)|
 * 
 *     status values:
 * 
 *         ...
 * 
 * 
 * 
 * resource values:
 * 
 *     0: system
 *     1: user
 *     2: resource
 *     3: tag
 *     ...
 * 
 * method values:
 * 
 *     0: create
 *     1: read
 *     2: update
 *     3: delete
 * 
 *     5: login (only when resource value == 0)
 *     ...
*/