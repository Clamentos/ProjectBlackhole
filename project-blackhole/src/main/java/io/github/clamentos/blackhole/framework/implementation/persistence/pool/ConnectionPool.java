package io.github.clamentos.blackhole.framework.implementation.persistence.pool;

///
import io.github.clamentos.blackhole.framework.implementation.configuration.ConfigurationProvider;

///..
import io.github.clamentos.blackhole.framework.implementation.logging.LogLevels;
import io.github.clamentos.blackhole.framework.implementation.logging.Logger;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.ExceptionFormatter;
import io.github.clamentos.blackhole.framework.implementation.utility.ResourceReleaser;

///.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

///..
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

///..
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

///
/**
 * <h3>Connection pool</h3>
 * Provides connection pooling for the persistence layer.
*/
public final class ConnectionPool {
    
    ///
    /** Singleton instance of {@code this} instantiated during class loading. */
    private static final ConnectionPool INSTANCE = new ConnectionPool();

    ///.
    /** The service used to log notable events. */
    private final Logger logger;

    ///..
    /**
     * The number of database connections in the pool.
     * @see ConfigurationProvider#NUM_DATABASE_CONNECTIONS
    */
    private final int NUM_DATABASE_CONNECTIONS;

    ///..
    /** The JDBC driver configuration properties. */
    private final Properties driver_properties;

    ///..
    /**
     * <p>The connection pools.</p>
     * Splitted into sub-pools to help with lock contention.
    */
    private List<BlockingQueue<PooledConnection>> pools;

    /**
     * <p>The open flag for {@code this}.</p>
     * This field is used to avoid closing the pool twice. Once closed, the pool cannot be opened again.
    */
    private volatile boolean is_open;

    ///
    /**
     * <p>Instantiates a new {@code ConnectionPool} object and starts the log task.</p>
     * Since this class is a singleton, this constructor will only be called once.
    */
    private ConnectionPool() {

        logger = Logger.getInstance();

        NUM_DATABASE_CONNECTIONS = ConfigurationProvider.getInstance().NUM_DATABASE_CONNECTIONS;

        // Instantiate and initialize the JDBC driver properties.
        driver_properties = new Properties();

        driver_properties.setProperty("user", ConfigurationProvider.getInstance().DATABASE_USERNAME);
        driver_properties.setProperty("password", ConfigurationProvider.getInstance().DATABASE_PASSWORD);
        driver_properties.setProperty( "prepareThreshold", Integer.toString(ConfigurationProvider.getInstance().PREPARE_THRESHOLD));

        driver_properties.setProperty(

            "preparedStatementCacheQueries", Integer.toString(ConfigurationProvider.getInstance().MAX_NUM_CACHEABLE_STATEMENTS)
        );

        driver_properties.setProperty(

            "preparedStatementCacheSizeMiB", Integer.toString(ConfigurationProvider.getInstance().MAX_STATEMENTS_CACHE_ENTRY_SIZE)
        );

        // Instantiate and fill the pools.
        int num_per_pool = ConfigurationProvider.getInstance().NUM_DATABASE_CONNECTIONS_PER_POOL;

        if((NUM_DATABASE_CONNECTIONS < num_per_pool) || (NUM_DATABASE_CONNECTIONS % num_per_pool) != 0) {

            logger.log(

                "ConnectionPool.new >> NUM_DATABASE_CONNECTIONS_PER_POOL must divide NUM_DATABASE_CONNECTIONS",
                LogLevels.FATAL
            );

            System.exit(1);
        }

        pools = new ArrayList<>();
        int num_pools = NUM_DATABASE_CONNECTIONS / num_per_pool;

        try {

            for(int i = 0; i < num_pools; i++) {

                pools.add(new LinkedBlockingQueue<>());

                for(int j = 0; j < num_per_pool; j++) {

                    Connection connection = DriverManager.getConnection(

                        ConfigurationProvider.getInstance().DATABASE_ADDRESS, driver_properties
                    );

                    connection.setAutoCommit(false);
                    pools.get(i).add(new PooledConnection(connection));
                }
            }

            logger.log("ConnectionPool.new >> Instantiation successfull", LogLevels.SUCCESS);
        }

        catch(SQLException exc) {

            logger.log(ExceptionFormatter.format("ConnectionPool.new >> ", exc, ""), LogLevels.FATAL);
            System.exit(1);
        }
    }

    ///
    /** @return The {@link ConnectionPool} instance created during class loading. */
    public static ConnectionPool getInstance() {

        return(INSTANCE);
    }

    ///
    /** @return The never {@code null} currently set JDBC driver properties. */
    public Properties getDriverProperties() {

        return(driver_properties);
    }

    ///..
    /**
     * <p>Aquires a {@link PooledConnection} from the connection pool.</p>
     * <b>NOTE: This method does NOT guarantee that the aquired connections are valid.</b>
     * @param caller : The reference of the caller. This is used to select which sub-pool to use in order to spread lock contention.
     * @return One connection from the pool.
     * @see PooledConnection
    */
    public PooledConnection aquireConnection(Object caller) {

        BlockingQueue<PooledConnection> pool = pools.get((int)(System.identityHashCode(caller) % pools.size()));
        int busy_wait_attempts = 0;

        PooledConnection connection;

        // Attempt to insert aggressively.
        while(busy_wait_attempts < ConfigurationProvider.getInstance().MAX_POOL_POLL_ATTEMPTS) {

            connection = pool.poll();

            if(connection != null) {

                return(connection);
            }

            busy_wait_attempts++;
        }

        // The busy wait expired the attempts. Insert with blocking behaviour.
        while(true) {

            try {

                return(connection = pool.take());
            }

            catch(InterruptedException exc) {

                logger.log(ExceptionFormatter.format("ConnectionPool.aquireConnection >> ", exc, " >> Ignoring..."), LogLevels.NOTE);
            }
        }
    }

    ///..
    /**
     * Releases the specified {@link PooledConnection} back into the pool.
     * @param caller : The reference of the caller. This is used to select which sub-pool to use in order to spread lock contention.
     * @param connection : The connection to release.
     * @throws IllegalStateException If the pool capacity is exceeded.
     * @throws NullPointerException If {@code connection} is {@code null}.
     * @see PooledConnection
    */
    public void releaseConnection(Object caller, PooledConnection connection) throws IllegalStateException, NullPointerException {

        BlockingQueue<PooledConnection> pool = pools.get((int)(System.identityHashCode(caller) % pools.size()));
        boolean inserted = false;
        int busy_wait_attempts = 0;

        // Attempt to insert aggressively.
        while(busy_wait_attempts < 10) {

            inserted = pool.offer(connection);

            if(inserted == true) {

                return;
            }

            busy_wait_attempts++;
        }

        // The busy wait expired the attempts. Insert with blocking behaviour.
        while(true) {

            try {

                pool.put(connection);
            }

            catch(InterruptedException exc) {

                logger.log(ExceptionFormatter.format("ConnectionPool.releaseConnection >> ", exc, " >> Ignoring..."), LogLevels.NOTE);
            }
        }
    }

    ///..
    /**
     * <p>Releases and closes all connections in the pool.</p>
     * <b>NOTE: Subsequent calls to this method will do nothing.</b>
    */
    public synchronized void closePool() {

        if(is_open) {

            for(BlockingQueue<PooledConnection> pool : pools) {

                // Check if all connections have been released.
                if(pool.size() == NUM_DATABASE_CONNECTIONS) {

                    for(PooledConnection connection : pool) {

                        ResourceReleaser.release(logger, "ConnectionPool.closePool", connection.getConnection());
                    }
                }

                else {

                    try {

                        Thread.sleep(ConfigurationProvider.getInstance().POOL_SHUTDOWN_SLEEP_CHUNK_SIZE);
                    }

                    catch(InterruptedException exc) {

                        logger.log(ExceptionFormatter.format("ConnectionPool.closePool >> ", exc, " >> Ignoring..."), LogLevels.NOTE);
                    }
                }
            }
        }
    }

    ///
}
