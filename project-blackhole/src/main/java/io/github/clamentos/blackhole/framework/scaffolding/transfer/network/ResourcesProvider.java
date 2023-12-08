package io.github.clamentos.blackhole.framework.scaffolding.transfer.network;

///
/**
 * <h3>Resources provider</h3>
 * Specifies that the implementing class can provide the user-defined resources to other classes.
 * @see Resources
*/
@FunctionalInterface
public interface ResourcesProvider<E extends Enum<E>> {

    ///
    /**
     * Gets the associated resources enumeration.
     * @param id : The corresponding resource id.
     * @return The never {@code null} enumeration constant.
     * @throws IllegalArgumentException If {@code id} didn't match any element.
     * @see Resources
    */
    Resources<E> getResource(byte id) throws IllegalArgumentException;

    ///
}
