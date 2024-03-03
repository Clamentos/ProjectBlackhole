package io.github.clamentos.blackhole.framework.implementation.network.transfer.components;

///
import io.github.clamentos.blackhole.framework.scaffolding.network.serialization.Streamable;

///..
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.output.ResponseStatuses;

///.
import java.io.DataOutputStream;
import java.io.IOException;

///
/**
 * <h3>Response Headers</h3>
 * Specifies the headers for the network response.
*/
public final record ResponseHeaders(

    ///
    /** The response payload size in bytes. */
    long payload_size,

    /** The identifier of the associated request. */
    byte id,

    /**
     * The response flag bits from least significant to most significant:
     * <ul>
     *     <li>Compression: {@code true} if the response payload is compressed, {@code false} otherwise.</li>
     *     <li>The remaining bits are reserved for future use.</li>
     * </ul>
    */
    byte flags,

    /** The timestamp used for caching. {@code <= 0} if the payload is not cacheable. */
    long cache_timestamp,

    /** The response status code of the associated response. */
    ResponseStatuses response_status

    ///
) implements Streamable {

    ///
    /**
     * Instantiates a new {@link ResponseHeaders} object.
     * @param payload_size : The response payload size in bytes.
     * @param id : The identifier of the associated request.
     * @param flags : The response flag bits.
     * @param cache_timestamp : The timestamp used for caching.
     * @param response_status : The response status code of the associated response.
     * @throws IllegalArgumentException If {@code response_status} is {@code null}.
    */
    public ResponseHeaders {

        if(response_status == null) {

            throw new IllegalArgumentException("ResponseHeaders.new -> The input argument \"response_status\" cannot be null");
        }
    }
    
    ///
    /** {@inheritDoc} */
    @Override
    public long getSize() {

        return(21);
    }

    ///..
    /**
     * {@inheritDoc}
     * @throws NullPointerException If {@code out} is {@code null}.
    */
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
