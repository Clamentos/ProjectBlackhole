package io.github.clamentos.blackhole.framework.scaffolding.transfer.serialization;

///
import java.io.DataOutputStream;
import java.io.IOException;

///
/**
 * <h3>Streamable</h3>
 * Specifies that the implementing class can be sent through an output stream.
*/
public interface Streamable {

    ///
    /** @return The size in bytes of {@code this} streamable.*/
    long getSize();

    ///..
    /**
     * <p>Streams {@code this} through the provided output stream.</p>
     * <b>NOTE: This method does not flush the output stream.</b>
     * @param out : The output stream.
    */
    void stream(DataOutputStream out) throws IOException;

    ///
}
