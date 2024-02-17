package io.github.clamentos.blackhole.framework.scaffolding.transfer.network;

///
import io.github.clamentos.blackhole.framework.scaffolding.transfer.deserialization.Deserializable;

///
/**
 * <h3>Request</h3>
 * Specifies that the implementing class is a network request.
 * @see Resources
 * @see Deserializable
*/
public interface Request {

    ///
    /** @return The client specified request id to be echoed back in the response. */
    byte getId();
    
    ///..
    /**
     * @return The never {@code null} resource that {@code this} request is targeting.
     * @see Resources
    */
    Resources<? extends Enum<?>> getResource();

    ///..
    /**
     * @return The never {@code null} method that {@code this} request is specifying.
     * @see Methods
    */
    Methods getMethod();

    ///..
    /**
     * @return The possibly {@code null} payload object that {@code this} request is carrying.
     * @see Deserializable
    */
    Deserializable getPayload();

    ///
}
