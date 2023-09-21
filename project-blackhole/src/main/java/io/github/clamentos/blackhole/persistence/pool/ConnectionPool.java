package io.github.clamentos.blackhole.persistence.pool;

///
import io.github.clamentos.blackhole.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.persistence.PersistenceException;

import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

///
/**
 * <h3>Database connection pool</h3>
 * This class implements connection pooling for the persistence layer.
 * @apiNote This class is an <b>eager-loaded singleton</b>.
*/
public class ConnectionPool {
    
    ///
    private static final ConnectionPool INSTANCE = new ConnectionPool();
    
    private final int NUM_DB_CONNECTIONS;
    private final String DB_ADDRESS;

    private Logger logger;
    private Properties driver_properties;
    private LinkedBlockingQueue<PooledConnection> pool;   // TODO: multiple queues to spread lock contention

    ///
    // Populate the queue with connections. Each connection has ALL the statements ready to go.
    private ConnectionPool() {

        logger = Logger.getInstance();
        driver_properties = new Properties();

        NUM_DB_CONNECTIONS = ConfigurationProvider.getInstance().NUM_DB_CONNECTIONS;
        DB_ADDRESS = ConfigurationProvider.getInstance().DB_ADDRESS;

        driver_properties.setProperty("user", ConfigurationProvider.getInstance().DB_USERNAME);
        driver_properties.setProperty("password", ConfigurationProvider.getInstance().DB_PASSWORD);
        //...

        pool = new LinkedBlockingQueue<>(NUM_DB_CONNECTIONS);

        try {

            for(int i = 0; i < NUM_DB_CONNECTIONS; i++) {

                pool.add(new PooledConnection(DriverManager.getConnection(DB_ADDRESS, driver_properties)));
            }

            logger.log("ConnectionPool.new > Instantiation successfull", LogLevel.SUCCESS);
        }

        catch(SQLException exc) {

            logger.log(

                "ConnectionPool.new > Could not instantiate, PersistenceException: " + exc.getMessage() +
                " Aborting",
                LogLevel.ERROR
            );

            System.exit(1);
        }
    }

    ///
    /** @return The {@link ConnectionPool} instance created during class loading. */
    public static ConnectionPool getInstance() {

        return(INSTANCE);
    }

    ///
    /**
     * Aquires a {@link PooledConnection} from the connection pool.
     * This method does NOT guarantee that the aquired connections are valid.
     * @return One connection from the pool.
    */
    public PooledConnection aquireConnection() {

        while(true) {

            try {

                return(pool.take());    // TODO: make it adaptive just like the logger
            }

            catch(InterruptedException exc) {

                logger.log("ConnectionPool.aquireConnection > Interrupted, ignoring", LogLevel.WARNING);
            }
        }
    }

    /**
     * Releases the specified {@link PooledConnection} back into the pool.
     * @param connection : The connection.
     * @throws IllegalStateException If the pool capacity is exceeded.
    */
    public void releaseConnection(PooledConnection connection) throws IllegalStateException {

        pool.add(connection);
    }

    public PooledConnection refreshConnection(PooledConnection connection) throws PersistenceException {

        try {

            if(connection.isInvalid(5)) {

                connection.connection().close();
                return(new PooledConnection(DriverManager.getConnection(DB_ADDRESS, driver_properties)));
            }

            return(connection);
        }

        catch(SQLException exc) {

            throw new PersistenceException(exc);
        }
    }

    public void closePool() {

        // TODO
    }

    ///
}
