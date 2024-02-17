package io.github.clamentos.blackhole.framework.implementation.network.transfer;

///
import io.github.clamentos.blackhole.framework.implementation.network.tasks.RequestTask;

///..
import io.github.clamentos.blackhole.framework.implementation.network.transfer.components.RequestHeaders;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.deserialization.Deserializable;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Methods;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Request;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Resources;

///
/**
 * <h3>Network Request</h3>
 * This class holds all the fields and data required to handle a network request.
 * @see RequestHeaders
 * @see Deserializable
 * @see RequestTask
*/
public final record NetworkRequest(

    ///
    /** The request headers. */
    RequestHeaders headers,

    /** The actual data payload. */
    Deserializable data

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
    public byte getId() {

        return(headers.id());
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
    public Methods getMethod() {

        return(headers.method());
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public Deserializable getPayload() {

        return(data);
    }

    ///
}
