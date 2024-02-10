package io.github.clamentos.blackhole.framework.implementation.network.transfer.components;

///
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.DataTransferObject;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.network.Streamable;

///.
import java.io.DataOutputStream;
import java.io.IOException;

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

        return((1 + 8) + (1 + 4 + message.length()));
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void stream(DataOutputStream out) throws IOException {

        out.writeByte(Types.LONG.ordinal());
        out.writeLong(timestamp);

        out.writeByte(Types.STRING.ordinal());
        out.writeInt(message.length());
        out.write(message.getBytes());
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public boolean isReactive() {

        return(false);
    }

    ///
}
