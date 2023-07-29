package io.github.clamentos.blackhole.framework;

//________________________________________________________________________________________________________________________________________

/**
 * <p><b>STEREOTYPE: Interface.</b></p>
 * <p>Streamable object.</p>
 * Used to indicate that the implementing classes can be transformed into an array of bytes
 * in order to be sent through a stream directly.
*/
@FunctionalInterface
public interface Streamable {

    //____________________________________________________________________________________________________________________________________
    
    /** @return A never {@code null} array of bytes representing {@code this}. */
    byte[] stream();

    //____________________________________________________________________________________________________________________________________
}
