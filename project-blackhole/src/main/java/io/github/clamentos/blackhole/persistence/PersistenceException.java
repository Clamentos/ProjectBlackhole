package io.github.clamentos.blackhole.persistence;

import io.github.clamentos.blackhole.exceptions.Failures;
import java.sql.SQLException;

// TODO: finish
public class PersistenceException extends Exception {
    
    private Failures failure_cause;

    public PersistenceException(SQLException exc) {

        super(decodeMessage(exc));
        failure_cause = decodeCause(exc);
    }

    public Failures getFailureCause() {

        return(failure_cause);
    }

    private static String decodeMessage(SQLException exc) {

        //...
        return(null);
    }

    private static Failures decodeCause(SQLException exc) {

        //...
        return(null);
    }
}
