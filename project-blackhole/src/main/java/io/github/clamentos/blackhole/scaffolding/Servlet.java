package io.github.clamentos.blackhole.scaffolding;

///
import io.github.clamentos.blackhole.network.transfer.Request;
import io.github.clamentos.blackhole.network.transfer.Response;
import io.github.clamentos.blackhole.network.transfer.components.Resources;

///
/**
 * <h3>Servlet interface</h3>
 * Simple interface that specifies basic functionality of a servlet.
*/
public interface Servlet {

    ///
    /** @return The handled {@link Resources} by {@code this} servlet. */
    Resources manages();

    /**
     * Handles the {@link Request}.
     * @param request : The input request.
     * @return The {@link Response} to be sent.
    */
    Response handle(Request request, int request_counter);

    ///
}
