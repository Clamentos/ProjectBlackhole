package io.github.clamentos.blackhole.framework.implementation.persistence.pool;

///
import io.github.clamentos.blackhole.framework.implementation.configuration.ConfigurationProvider;

///..
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.DatabaseConnectionException;

///..
import io.github.clamentos.blackhole.framework.scaffolding.persistence.QueryBinder;

///.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

///
/**
 * <h3>Pooled Connection</h3>
 * Wrapper on the JDBC connection class.
 * @see ConnectionPool
 * @see QueryBinder
 * @see SqlQueryBinder
*/
public final class PooledConnection {

    ///
    /**
     * The associated query binder.
     * @see QueryBinder
     * @see SqlQueryBinder
    */
    private final SqlQueryBinder query_binder;

    ///..
    /** The underlying JDBC connection. */
    private Connection connection;

    ///
    /**
     * Instantiates a new {@link PooledConnection} object.
     * @param connection : The JDBC connection.
     * @throws IllegalArgumentException If {@code connection} is {@code null}.
     * @see QueryBinder
     * @see SqlQueryBinder
    */
    public PooledConnection(Connection connection) throws IllegalArgumentException {

        if(connection != null) {

            query_binder = new SqlQueryBinder();
            this.connection = connection;
        }

        else {

            throw new IllegalArgumentException("(PooledConnection.new) -> argument \"connection\" cannot be null");
        }
    }

    ///
    /**
     * @return The associated query binder.
     * @see QueryBinder
     * @see SqlQueryBinder
    */
    public SqlQueryBinder getQueryBinder() {

        return(query_binder);
    }

    ///..
    /** @return The associated connection. */
    public Connection getConnection() {

        return(connection);
    }

    ///..
    /**
     * Refreshes the connection in-place. If {@code this} is valid, this method does nothing.
     * @throws DatabaseConnectionException If any database access error occurs.
    */
    public void refreshConnection() throws DatabaseConnectionException {

        try {

            boolean is_invalid = !connection.isValid(ConfigurationProvider.getInstance().DATABASE_CONNECTION_CHECK_TIMEOUT);

            if(connection.isClosed() || is_invalid) {

                connection.close();

                connection = DriverManager.getConnection(

                    ConfigurationProvider.getInstance().DATABASE_ADDRESS, ConnectionPool.getInstance().getDriverProperties()
                );
            }
        }

        catch(SQLException exc) {

            throw new DatabaseConnectionException("(PooledConnection.refreshConnection) -> Could not refresh the database connection", exc);
        }
    }

    ///
}
