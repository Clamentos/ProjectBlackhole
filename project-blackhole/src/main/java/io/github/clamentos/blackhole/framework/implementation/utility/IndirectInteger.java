package io.github.clamentos.blackhole.framework.implementation.utility;

///
/**
 * <h3>Indirect integer</h3>
 * <p>Mutable class to use when a reference to an integer is needed instead of the raw value.</p>
 * This can be useful for methods that need to both iterate arrays with a starting index, as well as updating the index in the process.
*/
public final class IndirectInteger {

    ///
    public int value;

    ///
    /**
     * Instantiates a new {@code IndirectInteger} with the provided value.
     * @param value : The starting value.
    */
    public IndirectInteger(int value) {

        this.value = value;
    }

    ///
}
