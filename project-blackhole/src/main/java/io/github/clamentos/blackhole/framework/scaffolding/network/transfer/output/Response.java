package io.github.clamentos.blackhole.framework.scaffolding.network.transfer.output;

///
import io.github.clamentos.blackhole.framework.scaffolding.network.serialization.Streamable;

///
/**
 * <h3>Response</h3>
 * Specifies that the implementing class is a network response that can be streamed back to the client.
*/
public interface Response extends Streamable {

    ///
    /** @return The never {@code null} status of {@code this} response. */
    ResponseStatuses getResponseStatus();

    ///
}
