package io.github.clamentos.blackhole.framework.implementation.network.transfer.components;

///
import io.github.clamentos.blackhole.framework.implementation.utility.StreamUtils;

///..
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.DataTransferObject;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Streamable;

///.
import java.io.IOException;
import java.io.OutputStream;

///
/**
 * <h3>Simple DTO</h3>
 * Simple data transfer object.
 * @see DataTransferObject
*/
public final record SimpleDto(

    ///
    /** The timestamp of the instantiation of {@code this}. */
    long timestamp,

    /** The message to the client. */
    String message

    ///
) implements Streamable, DataTransferObject {

    ///
    /**
     * Instantiates a new {@link SimpleDto} object. 
     * @param message : The message string.
    */
    public SimpleDto(String message) {

        this(System.currentTimeMillis(), message);
    }

    ///
    /** {@inheritDoc} */
    @Override
    public long getSize() {

        return(8 + message.length());
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void stream(OutputStream out) throws IOException {

        // Timestamp.
        out.write((byte)Types.LONG.ordinal());
        StreamUtils.writeNumber(out, timestamp, 8);

        // Message string.
        out.write((byte)Types.STRING.ordinal());
        StreamUtils.writeNumber(out, message.length(), 4);
        out.write(message.getBytes());
    }

    ///
}
