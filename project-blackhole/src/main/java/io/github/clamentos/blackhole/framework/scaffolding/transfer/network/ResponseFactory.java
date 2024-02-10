package io.github.clamentos.blackhole.framework.scaffolding.transfer.network;

import io.github.clamentos.blackhole.framework.implementation.network.transfer.NetworkResponse;
import io.github.clamentos.blackhole.framework.implementation.network.transfer.components.ResponseHeaders;
import io.github.clamentos.blackhole.framework.implementation.network.transfer.components.ResponseStatuses;

public class ResponseFactory {

    public static Response build(byte request_id, ResponseStatuses status, Streamable payload) {

        return(new NetworkResponse(new ResponseHeaders(payload.getSize() + 2, request_id, (byte)0, 0, status), payload));
    }
}
