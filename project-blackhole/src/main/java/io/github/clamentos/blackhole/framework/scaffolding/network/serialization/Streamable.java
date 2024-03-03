package io.github.clamentos.blackhole.framework.scaffolding.network.serialization;

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
     * Streams {@code this} through the provided output stream.
     * @param out : The output stream.
     * @throws IOException If any error occurs during the serialization process.
     * @apiNote This method does not flush the output stream.
    */
    void stream(DataOutputStream out) throws IOException;

    ///
}
