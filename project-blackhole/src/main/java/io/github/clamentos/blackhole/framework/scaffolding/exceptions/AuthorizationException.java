package io.github.clamentos.blackhole.framework.scaffolding.exceptions;

///
/**
 * <h3>Authorization Exception</h3>
 * Runtime exception indicating a lack of privileges by the requesting client.
*/
public final class AuthorizationException extends SecurityException {
    
    ///
    /**
     * Instantiates a new {@link AuthorizationException} object with {@code null} cause.
     * @param message : The exception detail message.
    */
    public AuthorizationException(String message) {

        super(message);
    }

    ///..
    /**
     * Instantiates a new {@link AuthorizationException} object.
     * @param message : The exception detail message.
     * @param cause : The cause of {@code this} exception.
    */
    public AuthorizationException(String message, Throwable cause) {

        super(message, cause);
    }

    ///
}
