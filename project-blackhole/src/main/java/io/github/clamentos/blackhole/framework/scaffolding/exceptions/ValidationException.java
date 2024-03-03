package io.github.clamentos.blackhole.framework.scaffolding.exceptions;

///
/**
 * <h3>Validation Exception</h3>
 * Runtime exception indicating an error while validating a request payload.
*/
public class ValidationException extends DatabindException {
    
    ///
    /**
     * Instantiates a new {@link ValidationException} object with {@code null} cause.
     * @param message : The exception detail message.
    */
    public ValidationException(String message) {

        super(message);
    }

    ///..
    /**
     * Instantiates a new {@link ValidationException} object.
     * @param message : The exception detail message.
     * @param cause : The cause of {@code this} exception.
    */
    public ValidationException(String message, Throwable cause) {

        super(message, cause);
    }

    ///
}
