package io.github.clamentos.blackhole.framework.scaffolding.exceptions;

///
/**
 * <h3>Deserialization Exception</h3>
 * Runtime exception indicating an error during the deserialization process.
 * @see DatabindException
*/
public final class DeserializationException extends DatabindException {

    ///
    /**
     * Instantiates a new {@link DeserializationException} object with {@code null} cause.
     * @param message : The exception detail message.
    */
    public DeserializationException(String message) {

        super(message);
    }

    ///..
    /**
     * Instantiates a new {@link DeserializationException} object.
     * @param message : The internal exception detail message.
     * @param cause : The cause of {@code this} exception.
    */
    public DeserializationException(String message, Throwable cause) {

        super(message, cause);
    }

    ///
}
