package io.github.clamentos.blackhole.scaffolding;

///
/**
 * <h3>Streamable interface</h3>
 * 
 * Used to indicate that the implementing classes can be transformed into an array of bytes
 * in order to be sent through a stream directly.
*/
@FunctionalInterface
public interface Streamable {

    ///
    /** @return A never {@code null} array of bytes representing {@code this}. */
    byte[] stream();

    ///
}
