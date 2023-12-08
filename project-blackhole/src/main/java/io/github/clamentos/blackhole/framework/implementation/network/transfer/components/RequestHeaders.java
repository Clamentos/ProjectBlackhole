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
    /**
     * The identifier of the request containing {@code this}.
     * @see NetworkRequest
     */
    byte id,

    /** The flag bits. */
    byte flags,

    /** The request method. */
    Methods method,

    /** The target resource that the request manipulates. */
    Resources<? extends Enum<?>> target_resource,

    /** The optional session identifier. */
    byte[] session_id

    ///
) {}
