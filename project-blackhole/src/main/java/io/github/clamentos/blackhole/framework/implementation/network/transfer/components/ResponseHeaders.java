package io.github.clamentos.blackhole.framework.implementation.network.transfer.components;

///
import io.github.clamentos.blackhole.framework.implementation.network.transfer.NetworkResponse;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.ResponseStatuses;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.serialization.Streamable;

///.
import java.io.DataOutputStream;
import java.io.IOException;

///
/**
 * <h3>Response headers</h3>
 * Specifies the headers for the network response.
 * @see NetworkResponse
 * @see ResponseStatuses
*/
public final record ResponseHeaders(

    ///
    /** The response payload size in bytes. */
    long payload_size,

    /** The identifier of the associated request. */
    byte id,

    /**
     * The response flag bits.
     * <ol>
     *     <li>Compression: {@code true} if {@code this} response payload is compressed, {@code false} otherwise.</li>
     * </ol>
    */
    byte flags,

    /** The timestamp used for caching. {@code <= 0} if the response is not cacheable. */
    long cache_timestamp,

    /** The response status code of {@code this}. */
    ResponseStatuses response_status

    ///
) implements Streamable {

    ///
    /** {@inheritDoc} */
    @Override
    public long getSize() {

        return(19);
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void stream(DataOutputStream out) throws IOException {

        out.writeLong(payload_size);
        out.writeByte(id);
        out.writeByte(flags);
        out.writeLong(cache_timestamp);
        out.writeByte(response_status.ordinal());
    }

    ///
}
