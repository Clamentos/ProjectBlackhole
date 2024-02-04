package io.github.clamentos.blackhole.framework.scaffolding.exceptions;

///
/**
 * <h3>Deserialization Exception</h3>
 * Runtime exception indicating an error during deserialization processes.
*/
public class DeserializationException extends RuntimeException {

    ///
    /** The optional failure message to send as a feedback to the client. */
    private final String response_message;

    ///
    /**
     * Instantiates a new {@link DeserializationException} object.
     * @param message : The exception detail message.
     * @param response_message : The message to insert into a potential client response.
    */
    public DeserializationException(String message, String response_message) {

        super(message);
        this.response_message = response_message;
    }

    ///
    /** @return The response message or {@code null} if there isn't any. */
    public String getResponseMessage() {

        return(response_message);
    }

    ///
}
