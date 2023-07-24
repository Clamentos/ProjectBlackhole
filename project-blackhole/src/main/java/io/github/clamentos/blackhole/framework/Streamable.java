// OK
package io.github.clamentos.blackhole.framework;

//________________________________________________________________________________________________________________________________________

/**
 * <p>Streamable interface.</p>
 * Used to indicate that the implementing classes can be transformed into an array of bytes
 * in order to be sent through a stream directly.
*/
@FunctionalInterface
public interface Streamable {

    //____________________________________________________________________________________________________________________________________
    
    /**
     * Transform {@code this} into an array of bytes.
     * @return A never null array of bytes representing {@code this}.
    */
    byte[] stream();

    //____________________________________________________________________________________________________________________________________
}
