package io.github.clamentos.blackhole.framework.scaffolding.network.controller;

///
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.input.Request;

///..
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.output.Response;

///
/**
 * <h3>Servlet</h3>
 * Specifies that the implementing class can handle network requests and generate network responses.
*/
@FunctionalInterface
public interface Servlet {

    ///
    /**
     * Handles the provided network request.
     * @param request : The request to be handled.
     * @return The never {@code null} network response to be sent.
    */
    Response handle(Request request);

    ///
}
