package io.github.clamentos.blackhole.framework.scaffolding.transfer.network;

///
import java.io.DataOutputStream;
import java.io.IOException;

///
/**
 * <h3>Partially streamable</h3>
 * Specifies that the implementing class can be sent through an output stream,
 * with the ability of choosing which fields to stream up to a maximum of 64.
 * @see Streamable
*/
public interface PartiallyStreamable extends Streamable {

    ///
    /**
     * <p>Streams {@code this} through the output stream.</p>
     * <b>NOTE: This method does not flush the output stream.</b>
     * @param out : The output stream.
     * @param fields : The fields to consider. This parameter works as a checklist
     * starting from the first field which maps to least significant bit.
    */
    void stream(DataOutputStream out, long fields) throws IOException;

    ///
}
