package io.github.clamentos.blackhole.framework.scaffolding.exceptions;

///
/**
 * <h3>Session Expired Exception</h3>
 * Runtime exception indicating that the client network session expired and is no longer valid.
*/
public final class SessionExpiredException extends SecurityException {
    
    ///
    /**
     * Instantiates a new {@link SessionExpiredException} object with {@code null} cause.
     * @param message : The exception detail message.
    */
    public SessionExpiredException(String message) {

        super(message);
    }

    ///..
    /**
     * Instantiates a new {@link SessionExpiredException} object.
     * @param message : The exception detail message.
     * @param cause : The cause of {@code this} exception.
    */
    public SessionExpiredException(String message, Throwable cause) {

        super(message, cause);
    }

    ///
}
