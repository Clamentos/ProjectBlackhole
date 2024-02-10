package io.github.clamentos.blackhole.framework.scaffolding.exceptions;

///
/**
 * <h3>Deserialization Exception</h3>
 * Runtime exception indicating an error during deserialization processes.
 * @see GenericException
*/
public class DeserializationException extends GenericException {

    ///
    /**
     * Instantiates a new {@link DeserializationException} object.
     * @param message : The internal exception detail message.
     * @param failure_message : The message to insert into a potential client response.
    */
    public DeserializationException(String message, String failure_message) {

        super(message, failure_message);
    }

    ///
}
