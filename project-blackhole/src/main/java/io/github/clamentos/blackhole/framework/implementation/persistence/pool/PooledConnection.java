package io.github.clamentos.blackhole.framework.implementation.persistence.pool;

///
import io.github.clamentos.blackhole.framework.implementation.configuration.ConfigurationProvider;

///..
import io.github.clamentos.blackhole.framework.implementation.persistence.query.SqlQueryBinder;

///..
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.DatabaseConnectionException;

///..
import io.github.clamentos.blackhole.framework.scaffolding.persistence.query.QueryBinder;

///.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

///..
import java.util.concurrent.atomic.AtomicBoolean;

///
/**
 * <h3>Pooled Connection</h3>
 * Wrapper on the JDBC connection class.
*/
public final class PooledConnection {

    ///
    /** The availability flag used by the connection pool. */
    private final AtomicBoolean available;
    
    ///..
    /** The associated query binder. */
    private final SqlQueryBinder query_binder;

    ///..
    /** The underlying JDBC connection. */
    private Connection connection;

    ///
    /**
     * Instantiates a new {@link PooledConnection} object.
     * @param connection : The JDBC connection.
    */
    protected PooledConnection(Connection connection) {

        available = new AtomicBoolean(true);
        query_binder = new SqlQueryBinder();
        this.connection = connection;
    }

    ///
    /**
     * @return The never {@code null} associated query binder.
     * @see QueryBinder
     * @see SqlQueryBinder
    */
    public SqlQueryBinder getQueryBinder() {

        return(query_binder);
    }

    ///..
    /** @return The never {@code null} associated connection. */
    public Connection getConnection() {

        return(connection);
    }

    ///..
    /**
     * Refreshes the connection in-place. If {@code this} is valid, this method does nothing.
     * @throws DatabaseConnectionException If any database access error occurs.
     * @throws IllegalStateException If the available flag of {@code this} is {@code true}.
    */
    public void refreshConnection() throws DatabaseConnectionException, IllegalStateException {

        if(available.get() == true) {

            throw new IllegalStateException("PooledConnection.refreshConnection -> Cannot refresh while available");
        }

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

            throw new DatabaseConnectionException("PooledConnection.refreshConnection -> Could not refresh the database connection", exc);
        }
    }

    ///.
    /** @return The never {@code null} availability flag of {@code this}. */
    protected AtomicBoolean getAvailable() {

        return(available);
    }

    ///
}
