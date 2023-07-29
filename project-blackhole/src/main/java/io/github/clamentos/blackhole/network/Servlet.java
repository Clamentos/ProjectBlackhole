package io.github.clamentos.blackhole.network;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.network.request.Request;
import io.github.clamentos.blackhole.network.request.Response;
import io.github.clamentos.blackhole.network.request.components.Resources;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>STEREOTYPE: Interface.</b></p>
 * Simple interface that specifies basic functionality of a servlet.
*/
public interface Servlet {

    //____________________________________________________________________________________________________________________________________
    
    /** @return The handled {@link Resources} by {@code this} servlet. */
    Resources manages();

    /**
     * Handles the {@link Request}.
     * @param request : The input request.
     * @return The {@link Response} to be sent.
    */
    Response handle(Request request);

    //____________________________________________________________________________________________________________________________________
}
