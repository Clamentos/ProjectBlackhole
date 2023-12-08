package io.github.clamentos.blackhole.framework.scaffolding.exceptions;

///
/**
 * <h3>Deserialization exception</h3>
 * Runtime exception indicating an error during deserialization processes.
 * @see RuntimeException
*/
public class DeserializationException extends RuntimeException {

    ///
    // Instance fields.
    private final String response_message;

    ///
    // Constructors.

    /**
     * Instantiates a new {@link DeserializationException} object.
     * @param message : The exception detail message.
     * @param response_message : The message to insert into a potential response.
    */
    public DeserializationException(String message, String response_message) {

        super(message);
        this.response_message = response_message;
    }

    ///
    // Instance methods.

    /** @return The response message or {@code null} if there isn't any. */
    public String getResponseMessage() {

        return(response_message);
    }

    ///
}
