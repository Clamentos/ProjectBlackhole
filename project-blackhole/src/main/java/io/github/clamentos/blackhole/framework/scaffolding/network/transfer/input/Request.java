package io.github.clamentos.blackhole.framework.scaffolding.network.transfer.input;

///
import io.github.clamentos.blackhole.framework.scaffolding.network.deserialization.Deserializable;

///
/**
 * <h3>Request</h3>
 * Specifies that the implementing class is a network request.
*/
public interface Request {

    ///
    /** @return The never {@code null} method that {@code this} request is specifying. */
    Methods getMethod();

    ///..
    /** @return The never {@code null} resource that {@code this} request is targeting. */
    Resources<? extends Enum<?>> getResource();

    ///..
    /** @return The possibly {@code null} payload object that {@code this} request is carrying. */
    Deserializable getPayload();

    ///
}
