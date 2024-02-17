package io.github.clamentos.blackhole.framework.scaffolding.transfer.network;

///
import io.github.clamentos.blackhole.framework.implementation.network.transfer.NetworkResponse;

///..
import io.github.clamentos.blackhole.framework.implementation.network.transfer.components.ResponseHeaders;

///..
import io.github.clamentos.blackhole.framework.scaffolding.transfer.serialization.Streamable;

///
/**
 * <h3>Response Factory</h3>
 * Utility class to instantiate pre-made, default-type network responses.
*/
// TODO:
public final class ResponseFactory {

    ///
    public static Response build(byte request_id, ResponseStatuses status, Streamable payload) {

        return(new NetworkResponse(new ResponseHeaders(payload.getSize() + 2, request_id, (byte)0, 0, status), payload));
    }

    ///
}
