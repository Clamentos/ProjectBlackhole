package io.github.clamentos.blackhole.framework.scaffolding.transfer.network;

///
/**
 * <h3>Resource</h3>
 * Specifies that the implementing class is an enumeration of target resources that a network request can reach.
*/
@FunctionalInterface
public interface Resources<E extends Enum<E>> {

    ///
    /** @return {@code true} if {@code this} resource is "reactive", {@code false} otherwise. */
    boolean isReactive();

    ///
}
