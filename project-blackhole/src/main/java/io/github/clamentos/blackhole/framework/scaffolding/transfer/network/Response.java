package io.github.clamentos.blackhole.framework.scaffolding.transfer.network;

///
import io.github.clamentos.blackhole.framework.scaffolding.transfer.serialization.Streamable;

///
/**
 * <h3>Response</h3>
 * Specifies that the implementing class is a network response that can be streamed back to the client.
 * @see Streamable
*/
public interface Response extends Streamable {

    ///
    ResponseStatuses getResponseStatus();

    ///
}
