package io.github.clamentos.blackhole.persistence.pool;

import io.github.clamentos.blackhole.persistence.PersistenceException;

import java.sql.Connection;
import java.sql.SQLException;

public record PooledConnection(

    Connection connection

) {

    public boolean isInvalid(int timeout) throws PersistenceException {

        try {

            return(connection.isClosed() || connection.isValid(timeout) == false);
        }

        catch(SQLException exc) {

            throw new PersistenceException(exc);
        }
    }
}
