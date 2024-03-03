package io.github.clamentos.blackhole.framework.implementation.network.transfer;

///
import io.github.clamentos.blackhole.framework.implementation.network.transfer.components.RequestHeaders;

///..
import io.github.clamentos.blackhole.framework.scaffolding.network.deserialization.Deserializable;

///..
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.input.Methods;
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.input.Request;
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.input.Resources;

///
/**
 * <h3>Network Request</h3>
 * This class holds all the fields and data required to handle a network request.
*/
public final record NetworkRequest(

    ///
    /** The request headers. */
    RequestHeaders headers,

    /** The payload data. */
    Deserializable data

    ///
) implements Request {

    ///
    /**
     * Instantiates a new {@link NetworkRequest} object.
     * @param headers : The request headers.
     * @param data : The payload data.
     * @throws IllegalArgumentException If {@code headers} is {@code null}.
    */
    public NetworkRequest {

        if(headers == null) {

            throw new IllegalArgumentException("NetworkRequest.new -> The input argument \"headers\" cannot be null");
        }
    }

    ///
    /** {@inheritDoc} */
    @Override
    public Methods getMethod() {

        return(headers.method());
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public Resources<? extends Enum<?>> getResource() {

        return(headers.target_resource());
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public Deserializable getPayload() {

        return(data);
    }

    ///
}
