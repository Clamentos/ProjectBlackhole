package io.github.clamentos.blackhole.framework.implementation.network.transfer;

///
import io.github.clamentos.blackhole.framework.implementation.network.transfer.components.RequestHeaders;

///..
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.DataTransferObject;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Request;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Resources;

///
/**
 * <h3>Network Request</h3>
 * This class holds all the fields and data required to handle a network request.
 * @see RequestHeaders
 * @see DataTransferObject
*/
public final record NetworkRequest(

    ///
    /** The request headers. */
    RequestHeaders headers,

    /** The actual data payload. */
    DataTransferObject data

    ///
) implements Request {

    ///
    /**
     * Instantiates a new {@link NetworkRequest} object.
     * @param headers : The request headers.
     * @param data : The request payload.
     * @throws IllegalArgumentException If {@code headers} is {@code null}.
    */
    public NetworkRequest {

        if(headers == null) {

            throw new IllegalArgumentException("The input argument \"headers\" cannot be null");
        }
    }

    ///
    /** {@inheritDoc} */
    @Override
    public Resources<? extends Enum<?>> getResource() {

        return(headers.target_resource());
    }

    ///
}
