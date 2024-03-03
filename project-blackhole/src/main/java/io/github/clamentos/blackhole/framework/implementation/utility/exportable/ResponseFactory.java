package io.github.clamentos.blackhole.framework.implementation.utility.exportable;

///
import io.github.clamentos.blackhole.framework.implementation.network.transfer.NetworkRequest;
import io.github.clamentos.blackhole.framework.implementation.network.transfer.NetworkResponse;

///..
import io.github.clamentos.blackhole.framework.implementation.network.transfer.components.ResponseHeaders;

///..
import io.github.clamentos.blackhole.framework.scaffolding.network.serialization.Streamable;

///..
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.input.Request;

///..
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.output.Response;
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.output.ResponseStatuses;

///
/**
 * <h3>Response Factory</h3>
 * Utility class to instantiate framework-default-type network responses.
*/
public final class ResponseFactory {

    ///
    /**
     * Instantiates a framework-default-type network response.
     * @param request : The associated request.
     * @param status : The respose status.
     * @param flags : The response flags.
     * @param cache_timestamp : The response cache timestamp.
     * @param payload : The response payload.
     * @return The never {@code null} response.
    */
    public static Response build(Request request, ResponseStatuses status, byte flags, long cache_timestamp, Streamable payload) {

        return(

            new NetworkResponse(

                new ResponseHeaders(

                    payload.getSize() + 2,
                    ((NetworkRequest)request).headers().id(),
                    flags,
                    cache_timestamp,
                    status
                ),

                payload
            )
        );
    }

    ///
}
