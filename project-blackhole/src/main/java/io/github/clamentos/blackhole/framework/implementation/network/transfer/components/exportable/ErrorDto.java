package io.github.clamentos.blackhole.framework.implementation.network.transfer.components.exportable;

///
import io.github.clamentos.blackhole.framework.scaffolding.network.deserialization.Deserializable;

///..
import io.github.clamentos.blackhole.framework.scaffolding.network.serialization.Streamable;

///.
import java.io.DataOutputStream;
import java.io.IOException;

///
/**
 * <h3>Error Dto</h3>
 * Simple error data transfer object.
*/
public final record ErrorDto(

    ///
    /** The timestamp of the instantiation of {@code this}. */
    long timestamp,

    /** The message to the client. */
    String message,

    /** The recoverability of the error. */
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

        if(message != null) {

            return(message.length() + 15);
        }

        else {

            return(11);
        }
    }

    ///..
    /**
     * {@inheritDoc}
     * @throws NullPointerException If {@code out} is {@code null}.
    */
    @Override
    public void stream(DataOutputStream out) throws IOException, NullPointerException {

        out.writeByte(Types.LONG.ordinal());
        out.writeLong(timestamp);

        if(message != null) {

            out.writeByte(Types.STRING.ordinal());
            out.writeInt(message.length());
            out.write(message.getBytes());
        }

        else {

            out.writeByte(Types.NULL.ordinal());
        }

        out.writeByte(recoverable ? Types.BOOLEAN_TRUE.ordinal() : Types.BOOLEAN_FALSE.ordinal());
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public boolean isStreaming() {

        return(false);
    }

    ///
}
