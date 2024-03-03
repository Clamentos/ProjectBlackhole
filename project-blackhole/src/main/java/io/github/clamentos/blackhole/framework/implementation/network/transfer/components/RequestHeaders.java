package io.github.clamentos.blackhole.framework.implementation.network.transfer.components;

///
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.input.Methods;
import io.github.clamentos.blackhole.framework.scaffolding.network.transfer.input.Resources;

///
/**
 * <h3>Request Headers</h3>
 * Specifies the headers for the network request.
*/
public final record RequestHeaders(

    ///
    /** The request payload size in bytes. */
    long payload_size,

    /** The identifier of {@code this} request sent by the client. */
    byte id,

    /** The request method. */
    Methods method,

    /** The target resource that the request manipulates. */
    Resources<? extends Enum<?>> target_resource,

    /**
     * The request flag bits from least significant to most significant:
     * <ul>
     *     <li>Authorization: {@code true} if the client is providing the session id, {@code false} otherwise.</li>
     *     <li>Compression: {@code true} if the client desires it, {@code false} otherwise.</li>
     *     <li>The remaining bits are reserved for future use.</li>
     * </ul>
    */
    byte flags,

    /** The timestamp used for caching. {@code <= 0} if not available client side. This parameter only works for {@code READ} requests. */
    long cache_timestamp,

    /** The optional session identifier. */
    byte[] session_id

    ///
) {

    ///
    /**
     * Instantiates a new {@link RequestHeaders} object.
     * @param payload_size : The request payload size in bytes.
     * @param id : The identifier of {@code this} request sent by the client.
     * @param method : The request method.
     * @param target_resource : The target resource that the request manipulates.
     * @param flags : The request flag bits.
     * @param cache_timestamp : The timestamp used for caching.
     * @param session_id : The optional session identifier.
     * @throws IllegalArgumentException If either {@code method} or {@code target_resource} are {@code null}.
    */
    public RequestHeaders {

        if(method == null || target_resource == null) {

            throw new IllegalArgumentException("RequestHeaders.new -> The input arguments cannot be null");
        }
    }

    ///
}
