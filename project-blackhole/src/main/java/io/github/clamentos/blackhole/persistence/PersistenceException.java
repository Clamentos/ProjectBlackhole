package io.github.clamentos.blackhole.persistence;

///
import io.github.clamentos.blackhole.exceptions.Failures;
import java.sql.SQLException;

///
/**
 * <h3>Persistence exception</h3>
 * This exception indicates a problem with the persistence layer.
*/
public class PersistenceException extends Exception {
    
    ///
    private Failures failure_cause;

    ///
    /**
     * Instantiates a new {@link PersistenceException} by decoding the
     * specified {@link SQLException} into a more friendly exception.
     * 
     * @param exc : The desired {@link SQLException}.
    */
    public PersistenceException(SQLException exc) {

        super(decodeMessage(exc));
        failure_cause = decodeCause(exc);
    }

    /**
     * Instantiates a new {@link PersistenceException} by specifying
     * a {@link Failures} constant.
     * 
     * @param failure : The desired constant.
    */
    public PersistenceException(Failures failure) {

        super(decodeMessage(failure));
        failure_cause = failure;
    }

    ///
    /** @return The never {@code null} associated {@link Failures} constant. */
    public Failures getFailureCause() {

        return(failure_cause);
    }

    ///
    // TODO: finish
    private static String decodeMessage(SQLException exc) {

        //...
        return(null);
    }

    private static String decodeMessage(Failures failure) {

        //...
        return(null);
    }

    private static Failures decodeCause(SQLException exc) {

        //...
        return(null);
    }

    ///
}
