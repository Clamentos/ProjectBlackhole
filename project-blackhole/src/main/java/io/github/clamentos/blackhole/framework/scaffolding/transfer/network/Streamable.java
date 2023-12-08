package io.github.clamentos.blackhole.framework.scaffolding.transfer.network;

///
import java.io.IOException;
import java.io.OutputStream;

///
/**
 * <h3>Streamable</h3>
 * Specifies that the implementing class can be sent through an output stream.
*/
public interface Streamable {

    ///
    /** @return The size in bytes of {@code this} streamable.*/
    long getSize();
    
    /**
     * <p>Streams {@code this} through the output stream.</p>
     * <b>NOTE: This method does not flush the stream.</b>
     * @param out : The output stream.
    */
    void stream(OutputStream out) throws IOException;

    ///
}
