package io.github.clamentos.blackhole.framework.scaffolding.exceptions;

///
/**
 * <h3>Generic Exception</h3>
 * Runtime exception that serves as the root of the custom exception hierarchy.
*/
public class GenericException extends RuntimeException {

    ///
    /** The optional failure message to send as a feedback to the client. */
    private final String failure_message;

    ///
    /**
     * Instantiates a new {@link GenericException} object.
     * @param message : The internal exception detail message.
     * @param failure_message : The message to insert into a potential client response.
    */
    public GenericException(String message, String failure_message) {

        super(message);
        this.failure_message = failure_message;
    }

    ///
    /** @return The response message or {@code null} if there isn't any. */
    public String getFailureMessage() {

        return(failure_message);
    }

    ///
}
