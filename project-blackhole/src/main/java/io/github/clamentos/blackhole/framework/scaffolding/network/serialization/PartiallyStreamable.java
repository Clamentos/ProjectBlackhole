package io.github.clamentos.blackhole.framework.scaffolding.network.serialization;

///
import java.io.DataOutputStream;
import java.io.IOException;

///
/**
 * <h3>Partially Streamable</h3>
 * Specifies that the implementing class can be sent through an output stream,
 * with the ability of choosing which fields to stream up to a maximum of 64.
*/
public interface PartiallyStreamable extends Streamable {

    ///
    /**
     * Sends {@code this} through the provided output stream.
     * @param out : The output stream.
     * @param fields : The fields to consider. This parameter works as a checklist
     * starting from the first field which maps to least significant bit.
     * @throws IOException If any error occurs during the serialization process.
     * @apiNote This method does not flush the output stream.
    */
    void stream(DataOutputStream out, long fields) throws IOException;

    ///
}
