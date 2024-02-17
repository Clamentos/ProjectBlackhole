package io.github.clamentos.blackhole.framework.scaffolding.exceptions;

///
/**
 * <h3>Result Set Mapping Exception</h3>
 * Runtime exception indicating an error during the result set to entity mapping process.
 * @see PersistenceException
*/
public final class ResultSetMappingException extends PersistenceException {

    ///
    /**
     * Instantiates a new {@link ResultSetMappingException} object with {@code null} cause.
     * @param message : The exception detail message.
    */
    public ResultSetMappingException(String message) {

        super(message);
    }

    ///..
    /**
     * Instantiates a new {@link ResultSetMappingException} object.
     * @param message : The internal exception detail message.
     * @param cause : The cause of {@code this} exception.
    */
    public ResultSetMappingException(String message, Throwable cause) {

        super(message, cause);
    }

    ///
}
