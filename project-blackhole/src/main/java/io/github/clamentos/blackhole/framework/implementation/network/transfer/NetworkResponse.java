package io.github.clamentos.blackhole.framework.implementation.network.transfer;

///
import io.github.clamentos.blackhole.framework.implementation.network.transfer.components.Types;
import io.github.clamentos.blackhole.framework.implementation.network.transfer.components.ResponseHeaders;

///..
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Response;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Streamable;

///.
import java.io.IOException;
import java.io.OutputStream;

///
/**
 * <h3>Network response</h3>
 * This class holds all the fields and data that can be sent through a stream as a network response.
 * @see ResponseHeaders
 * @see Streamable
*/
public final record NetworkResponse(

    /** The response headers. */
    ResponseHeaders headers,

    /** The response data payload. */
    Streamable data

) implements Response {

    ///
    /**
     * Instantiates a new {@link NetworkResponse} object.
     * @param headers : The request headers.
     * @param data : The request payload.
     * @throws IllegalArgumentException If {@code headers} is {@code null}.
    */
    public NetworkResponse {

        if(headers == null) {

            throw new IllegalArgumentException("The input argument \"headers\" cannot be null");
        }
    }

    ///
    /** {@inheritDoc} */
    @Override
    public long getSize() {

        return(headers.getSize() + data.getSize());
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void stream(OutputStream out) throws IOException {

        // Headers.
        headers.stream(out);

        // Data.
        out.write((byte)Types.BEGIN.ordinal());
        data.stream(out);
        out.write((byte)Types.END.ordinal());
    }

    ///
}
