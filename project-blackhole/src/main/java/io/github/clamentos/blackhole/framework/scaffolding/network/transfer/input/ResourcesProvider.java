package io.github.clamentos.blackhole.framework.scaffolding.network.transfer.input;

///
/**
 * <h3>Resources Provider</h3>
 * Specifies that the implementing class can provide resources to other classes.
*/
@FunctionalInterface
public interface ResourcesProvider<E extends Enum<E>> {

    ///
    /**
     * Gets the associated resources enumeration.
     * @param id : The resource id.
     * @return The never {@code null} enumeration constant.
     * @throws IllegalArgumentException If {@code id} didn't match any element.
    */
    Resources<E> getResource(byte id) throws IllegalArgumentException;

    ///
}
