package io.github.clamentos.blackhole.framework.implementation.network.transfer.components;

///
import io.github.clamentos.blackhole.framework.scaffolding.transfer.deserialization.Deserializable;
import io.github.clamentos.blackhole.framework.scaffolding.transfer.serialization.Streamable;

///.
import java.io.DataOutputStream;
import java.io.IOException;

///
/**
 * <h3>Error DTO</h3>
 * Simple error data transfer object.
 * @see Deserializable
 * @see Streamable
*/
public final record ErrorDto(

    ///
    /** The timestamp of the instantiation of {@code this}. */
    long timestamp,

    /** The message to the client. */
    String message,

    /** The recoverability of the error. If {@code true} then the connection will be forcibly closed by the server */
    boolean recoverable

    ///
) implements Streamable, Deserializable {

    ///
    /**
     * Instantiates a new {@link ErrorDto} object. 
     * @param message : The message string.
    */
    public ErrorDto(String message, boolean recoverable) {

        this(System.currentTimeMillis(), message, recoverable);
    }

    ///
    /** {@inheritDoc} */
    @Override
    public long getSize() {

        return(message.length() + 16);
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

        out.writeByte(Types.BYTE.ordinal());
        out.writeByte(recoverable == true ? (byte)1 : (byte)0);
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public boolean isReactive() {

        return(false);
    }

    ///
}
