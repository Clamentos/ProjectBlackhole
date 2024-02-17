package io.github.clamentos.blackhole.framework.scaffolding.exceptions;

///
/**
 * <h3>Conflict Exception</h3>
 * Runtime exception indicating an entity version mismatch.
 * @see PersistenceException
*/
public final class ConflictException extends PersistenceException {
    
    ///
    /**
     * Instantiates a new {@link ConflictException} object with {@code null} cause.
     * @param message : The exception detail message.
    */
    public ConflictException(String message) {

        super(message);
    }

    ///..
    /**
     * Instantiates a new {@link ConflictException} object.
     * @param message : The internal exception detail message.
     * @param cause : The cause of {@code this} exception.
    */
    public ConflictException(String message, Throwable cause) {

        super(message, cause);
    }

    ///
}
