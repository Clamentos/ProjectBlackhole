package io.github.clamentos.blackhole.framework.scaffolding.exceptions;

///
/**
 * <h3>Query Binding Exception</h3>
 * Runtime exception indicating an error during the query parameter binding process.
 * @see PersistenceException
*/
public final class QueryBindingException extends PersistenceException {

    ///
    /**
     * Instantiates a new {@link QueryBindingException} object with {@code null} cause.
     * @param message : The exception detail message.
    */
    public QueryBindingException(String message) {

        super(message);
    }

    ///..
    /**
     * Instantiates a new {@link QueryBindingException} object.
     * @param message : The internal exception detail message.
     * @param cause : The cause of {@code this} exception.
    */
    public QueryBindingException(String message, Throwable cause) {

        super(message, cause);
    }

    ///
}
