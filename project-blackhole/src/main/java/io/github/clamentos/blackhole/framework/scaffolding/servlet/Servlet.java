package io.github.clamentos.blackhole.framework.scaffolding.servlet;

///
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Request;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Response;

///
/**
 * <h3>Servlet</h3>
 * Specifies that the implementing class can handle network requests and generate network responses.
 * @see Request
 * @see Response
*/
@FunctionalInterface
public interface Servlet {

    ///
    /**
     * Handles the provided network request.
     * @param request : The input request to be handled.
     * @return The never {@code null} network response to be sent.
     * @see Request
     * @see Response
    */
    Response handle(Request request);

    ///
}
