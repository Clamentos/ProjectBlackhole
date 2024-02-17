package io.github.clamentos.blackhole.framework.scaffolding.exceptions;

///
/**
 * <h3>Persistence Exception</h3>
 * <p>Runtime exception indicating an error while comunicating with the database.</p>
 * This is the root class for any other persistence related exception.
*/
public class PersistenceException extends RuntimeException {
    
    ///
    /**
     * Instantiates a new {@link PersistenceException} object with {@code null} cause.
     * @param message : The internal exception detail message.
    */
    public PersistenceException(String message) {

        super(message);
    }

    ///..
    /**
     * Instantiates a new {@link PersistenceException} object.
     * @param message : The internal exception detail message.
     * @param cause : The cause of {@code this} exception.
    */
    public PersistenceException(String message, Throwable cause) {

        super(message, cause);
    }

    ///
}
