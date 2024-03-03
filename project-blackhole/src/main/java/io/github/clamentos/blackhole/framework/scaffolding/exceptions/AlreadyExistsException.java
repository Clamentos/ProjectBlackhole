package io.github.clamentos.blackhole.framework.scaffolding.exceptions;

///
/**
 * <h3>Already Exists Exception</h3>
 * Runtime exception indicating that the same entity already exists in the database while trying to insert.
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
     * @param message : The exception detail message.
     * @param cause : The cause of {@code this} exception.
    */
    public AlreadyExistsException(String message, Throwable cause) {

        super(message, cause);
    }

    ///
}
