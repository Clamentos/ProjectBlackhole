package io.github.clamentos.blackhole.framework.implementation.network.transfer.components;

///
import io.github.clamentos.blackhole.framework.implementation.network.transfer.NetworkResponse;

///..
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Streamable;

///.
import java.io.IOException;
import java.io.OutputStream;

///
/**
 * <h3>Response headers</h3>
 * Specifies the headers for the network response.
 * @see NetworkResponse
 * @see ResponseStatuses
*/
public final record ResponseHeaders(

    ///
    /**
     * The identifier of the network response containing {@code this}.
     * @see NetworkResponse
    */
    byte id,

    /** The flag bits. */
    byte flags,

    /** The response status code of the network response containing {@code this}. */
    ResponseStatuses response_status

    ///
) implements Streamable {

    ///
    // Instance methods.

    /** {@inheritDoc} */
    @Override
    public long getSize() {

        return(3);
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void stream(OutputStream out) throws IOException {

        out.write(id);
        out.write(flags);
        out.write((byte)response_status.ordinal());
    }

    ///
}
