package io.github.clamentos.blackhole.persistence.pool;

///
import io.github.clamentos.blackhole.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.persistence.PersistenceException;

import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

///
/**
 * <h3>Database connection pool</h3>
 * This class implements connection pooling for the persistence layer.
*/
public class ConnectionPool {
    
    ///
    private static final ConnectionPool INSTANCE = new ConnectionPool();
    private Logger logger;
    
    private final int NUM_DB_CONNECTIONS;
    private final int NUM_POOLS;
    private final String DB_ADDRESS;

    private Properties driver_properties;
    private List<LinkedBlockingQueue<PooledConnection>> pools;

    ///
    // Populate the queue with connections. Each connection has ALL the statements ready to go.
    private ConnectionPool() {

        logger = Logger.getInstance();
        driver_properties = new Properties();
        pools = new ArrayList<>();

        NUM_DB_CONNECTIONS = ConfigurationProvider.getInstance().NUM_DB_CONNECTIONS;
        NUM_POOLS = ConfigurationProvider.getInstance().NUM_POOLS;
        DB_ADDRESS = ConfigurationProvider.getInstance().DB_ADDRESS;

        driver_properties.setProperty("user", ConfigurationProvider.getInstance().DB_USERNAME);
        driver_properties.setProperty("password", ConfigurationProvider.getInstance().DB_PASSWORD);

        driver_properties.setProperty(
            
            "prepareThreshold",
            Integer.toString(ConfigurationProvider.getInstance().PREPARE_THRESHOLD)
        );

        driver_properties.setProperty(
            
            "preparedStatementCacheQueries",
            Integer.toString(ConfigurationProvider.getInstance().MAX_NUM_CACHEABLE_PS)
        );

        driver_properties.setProperty(
            
            "preparedStatementCacheSizeMiB",
            Integer.toString(ConfigurationProvider.getInstance().MAX_PS_CACHE_ENTRY_SIZE)
        );

        try {

            for(int i = 0; i < NUM_POOLS; i++) {

                pools.add(new LinkedBlockingQueue<>(NUM_DB_CONNECTIONS));

                for(int j = 0; j < NUM_DB_CONNECTIONS; j++) {

                    pools.get(j).add(new PooledConnection(DriverManager.getConnection(DB_ADDRESS, driver_properties)));
                }
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
     * 
     * @param task_id : The id of the calling task. This is used to select which
     *                  sub-pool to use in order to spread lock contention.
     * @return One connection from the pool.
    */
    public PooledConnection aquireConnection(long task_id) {

        while(true) {

            try {

                // Choose the pool and take.
                // Use the MOD operation to uniformely distribute the amount of work.
                return(pools.get((int)(task_id % pools.size())).take()); // TODO: make it adaptive
            }

            catch(InterruptedException exc) {

                logger.log("ConnectionPool.aquireConnection > Interrupted, ignoring", LogLevel.WARNING);
            }
        }
    }

    /**
     * Releases the specified {@link PooledConnection} back into the pool.
     * 
     * @param connection : The connection.
     * @param task_id : The id of the calling task. This is used to select which
     *                  sub-pool to use in order to spread lock contention.
     * @throws IllegalStateException If the pool capacity is exceeded.
    */
    public void releaseConnection(PooledConnection connection, long task_id) throws IllegalStateException {

        pools.get((int)(task_id % pools.size())).add(connection);
    }

    /**
     * Refreshes the connection.
     * 
     * @param connection : The connection to be refreshed.
     * @return : a new {@link PooledConnection} instance if it's invalid, otherwise it will simply
     *           return the passed connection back.
     * @throws PersistenceException : If a database access error occurs.
    */
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

    /** Releases and closes all connections in the pool. */
    public synchronized void closePool() {

        for(LinkedBlockingQueue<PooledConnection> pool : pools) {

            for(int i = 0; i < 5; i++) {

                if(pool.size() == NUM_DB_CONNECTIONS) {

                    for(PooledConnection connection : pool) {

                        try {

                            connection.connection().close();
                        }
                                
                        catch(SQLException exc) {

                            logger.log(

                                "ConnectionPool.closePool > Could not close the connection " +
                                connection.toString() + ", SQLException: " + exc.getMessage() + " skipping",
                                LogLevel.WARNING
                            );
                        }
                    }

                    break;
                }

                try {

                    Thread.sleep(2000);
                }

                catch(InterruptedException exc) {

                    logger.log(
                            
                        "ConnectionPool.closePool > Interrupted while waiting on connections to be released, retrying",
                        LogLevel.WARNING
                    );
                }
            }
        }
    }

    ///
}
