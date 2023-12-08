package io.github.clamentos.blackhole.framework.implementation.persistence.pool;

///
import io.github.clamentos.blackhole.framework.implementation.configuration.ConfigurationProvider;

///..
import io.github.clamentos.blackhole.framework.implementation.persistence.PersistenceException;

///.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

///
/**
 * <h3>Pooled connection</h3>
 * Wrapper on the JDBC connection class.
 * @see Connection
 * @see ConnectionPool
*/
public final class PooledConnection {

    ///
    /** The underlying JDBC connection. */
    private Connection connection;

    ///
    /**
     * Instantiates a new {@link PooledConnection} object.
     * @param connection : The JDBC connection.
     * @see Connection
    */
    public PooledConnection(Connection connection) {

        this.connection = connection;
    }

    ///
    /** @return The associated {@link Connection}. */
    public Connection getConnection() {

        return(connection);
    }

    ///..
    /**
     * Refreshes the connection in-place. If {@code this} is valid, this method does nothing.
     * @throws PersistenceException If any database access error occurs.
    */
    public void refreshConnection() throws PersistenceException {

        try {

            // Check the connection status and refresh if invalid.
            boolean is_invalid = !connection.isValid(ConfigurationProvider.getInstance().DATABASE_CONNECTION_CHECK_TIMEOUT);

            if(connection.isClosed() || is_invalid) {

                connection.close();

                connection = DriverManager.getConnection(

                    ConfigurationProvider.getInstance().DATABASE_ADDRESS, ConnectionPool.getInstance().getDriverProperties()
                );
            }
        }

        // Wrap and propagate.
        catch(SQLException exc) {

            throw new PersistenceException(exc);
        }
    }

    ///
}
