package io.github.clamentos.blackhole.persistence.pool;

///
import io.github.clamentos.blackhole.persistence.PersistenceException;

import java.sql.Connection;
import java.sql.SQLException;

///
/**
 * <h3>Pooled connection wrapper</h3>
 * This simple record class is used as a wrapper on the {@link Connection} class.
*/
public record PooledConnection(

    ///
    Connection connection
    
    ///
) {

    /**
     * Checks if {@code this.connection} is valid.
     * 
     * @param timeout : The amount of seconds to wait before timing out on the check. 
     * @return : {@code true} if valid, {@code false} otherwise.
     * @throws PersistenceException If a database access error occurs.
    */
    public boolean isInvalid(int timeout) throws PersistenceException {

        try {

            return(connection.isClosed() || connection.isValid(timeout) == false);
        }

        catch(SQLException exc) {

            throw new PersistenceException(exc);
        }
    }

    ///
}
