package io.github.clamentos.blackhole.framework.scaffolding.exceptions;

///
/**
 * <h3>Databind Exception</h3>
 * Runtime exception indicating an error while decoding a request payload.
 * @apiNote This is the root class for any other databind related exception.
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
     * @param message : The exception detail message.
     * @param cause : The cause of {@code this} exception.
    */
    public DatabindException(String message, Throwable cause) {

        super(message, cause);
    }

    ///
}
