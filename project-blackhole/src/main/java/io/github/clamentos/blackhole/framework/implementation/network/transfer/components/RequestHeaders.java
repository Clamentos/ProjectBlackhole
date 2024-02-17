package io.github.clamentos.blackhole.framework.implementation.network.transfer.components;

///
import io.github.clamentos.blackhole.framework.implementation.network.transfer.NetworkRequest;

///..
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Methods;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Resources;

///
/**
 * <h3>Request headers</h3>
 * Specifies the headers for the network request.
 * @see NetworkRequest
 * @see Methods
 * @see Resources
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
     * The request flag bits.
     * <ol>
     *     <li>Authorization: {@code true} if the client is providing the session id.</li>
     *     <li>Compression: {@code true} if the client desires it, {@code false} otherwise.</li>
     * </ol>
    */
    byte flags,

    /** The timestamp used for caching. {@code <= 0} if not available client side. This parameter only works for {@code READ} requests. */
    long cache_timestamp,

    /** The optional session identifier. */
    byte[] session_id

    ///
) {}
