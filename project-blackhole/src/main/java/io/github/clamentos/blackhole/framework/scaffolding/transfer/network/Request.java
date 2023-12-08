package io.github.clamentos.blackhole.framework.scaffolding.transfer.network;

///
/**
 * <h3>Request</h3>
 * Specifies that the implementing class is a network request.
*/
@FunctionalInterface
public interface Request {

    ///
    /**
     * @return The never {@code null} resource that {@code this} request is targeting.
     * @see Resources
    */
    Resources<? extends Enum<?>> getResource();

    ///
}
