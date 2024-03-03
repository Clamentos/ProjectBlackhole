package io.github.clamentos.blackhole.framework.scaffolding.exceptions;

///
/**
 * <h3>Session Not Found Exception</h3>
 * Runtime exception indicating that a client network session does not exists.
*/
public final class SessionNotFoundException extends SecurityException {
    
    ///
    /**
     * Instantiates a new {@link SessionNotFoundException} object with {@code null} cause.
     * @param message : The exception detail message.
    */
    public SessionNotFoundException(String message) {

        super(message);
    }

    ///..
    /**
     * Instantiates a new {@link SessionNotFoundException} object.
     * @param message : The exception detail message.
     * @param cause : The cause of {@code this} exception.
    */
    public SessionNotFoundException(String message, Throwable cause) {

        super(message, cause);
    }

    ///
}
