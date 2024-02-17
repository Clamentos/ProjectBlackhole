package io.github.clamentos.blackhole.framework.scaffolding.exceptions;

///
/**
 * <h3>Already Exists Exception</h3>
 * Runtime exception indicating that an entity already exists.
 * @see PersistenceException
*/
public final class AlreadyExistsException extends PersistenceException {

    ///
    /**
     * Instantiates a new {@link AlreadyExistsException} object with {@code null} cause.
     * @param message : The exception detail message.
    */
    public AlreadyExistsException(String message) {

        super(message);
    }

    ///..
    /**
     * Instantiates a new {@link AlreadyExistsException} object.
     * @param message : The internal exception detail message.
     * @param cause : The cause of {@code this} exception.
    */
    public AlreadyExistsException(String message, Throwable cause) {

        super(message, cause);
    }

    ///
}
