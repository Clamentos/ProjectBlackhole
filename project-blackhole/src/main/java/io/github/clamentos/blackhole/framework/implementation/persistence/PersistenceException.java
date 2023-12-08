package io.github.clamentos.blackhole.framework.implementation.persistence;

///
import java.sql.SQLException;

///
/**
 * <h3>Persistence exception</h3>
 * This exception indicates a problem with the persistence layer.
*/
public final class PersistenceException extends RuntimeException {

    ///
    /**
     * Instantiates a new {@link PersistenceException} object decoding the specified sql exception.
     * @param exc : The caught sql exception.
    */
    public PersistenceException(SQLException exc) {

        super(decodeMessage(exc));
    }

    ///
    // TODO: finish
    private static String decodeMessage(SQLException exc) {

        //...
        return("");
    }

    ///
}
