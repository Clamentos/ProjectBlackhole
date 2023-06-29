package io.github.clamentos.blackhole.common.framework;

//________________________________________________________________________________________________________________________________________

/**
 * <p> Streamable interface.</p>
 * <p>Used to indicate that the implementing classes can be transformed into an array of bytes.</p>
*/
@FunctionalInterface
public interface Streamable {

    //____________________________________________________________________________________________________________________________________
    
    /**
     * Transform {@code this} into raw bytes of data to be sent to an output stream.
     * @return An array of bytes representing {@code this}.
    */
    byte[] toBytes();

    //____________________________________________________________________________________________________________________________________
}
