package io.github.clamentos.blackhole.framework.scaffolding.exceptions;

///
/**
 * <h3>Databind Exception</h3>
 * <p>Runtime exception indicating an error during the databind process.</p>
 * This is the root class for any other databind related exception.
*/
public class DatabindException extends RuntimeException {

    ///
    /**
     * Instantiates a new {@link DatabindException} object with {@code null} cause.
     * @param message : The exception detail message.
    */
    public DatabindException(String message) {

        super(message);
    }

    ///..
    /**
     * Instantiates a new {@link DatabindException} object.
     * @param message : The internal exception detail message.
     * @param cause : The cause of {@code this} exception.
    */
    public DatabindException(String message, Throwable cause) {

        super(message, cause);
    }

    ///
}
