package io.github.clamentos.blackhole.framework.scaffolding.exceptions;

///
/**
 * <h3>Database Connection Exception</h3>
 * Runtime exception indicating a database connection error.
 * @see PersistenceException
*/
public final class DatabaseConnectionException extends PersistenceException {

    ///
    /**
     * Instantiates a new {@link DatabaseConnectionException} object with {@code null} cause.
     * @param message : The exception detail message.
    */
    public DatabaseConnectionException(String message) {

        super(message);
    }

    ///..
    /**
     * Instantiates a new {@link DatabaseConnectionException} object.
     * @param message : The internal exception detail message.
     * @param cause : The cause of {@code this} exception.
    */
    public DatabaseConnectionException(String message, Throwable cause) {

        super(message, cause);
    }

    ///
}
