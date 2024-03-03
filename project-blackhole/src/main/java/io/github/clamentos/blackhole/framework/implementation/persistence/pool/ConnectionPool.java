package io.github.clamentos.blackhole.framework.implementation.persistence.pool;

///
import io.github.clamentos.blackhole.framework.implementation.configuration.ConfigurationProvider;

///..
import io.github.clamentos.blackhole.framework.implementation.logging.Logger;

///..
import io.github.clamentos.blackhole.framework.implementation.logging.exportable.LogLevels;

///..
import io.github.clamentos.blackhole.framework.implementation.tasks.TaskManager;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.ResourceReleaserInternal;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.exportable.ExceptionFormatter;

///.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

///..
import java.util.Properties;

///..
import java.util.concurrent.ConcurrentLinkedQueue;

///..
import java.util.concurrent.locks.LockSupport;

///
/**
 * <h3>Connection Pool</h3>
 * Provides database connection pooling for the persistence layer.
*/
public final class ConnectionPool {
    
    ///
    /** Singleton instance of {@code this} instantiated during class loading. */
    private static final ConnectionPool INSTANCE = new ConnectionPool();

    ///.
    /** The service used to log notable events. */
    private final Logger logger;

    ///..
    /** The JDBC driver configuration properties. */
    private final Properties driver_properties;

    ///..
    /** The connection pool. */
    private final PooledConnection[] pool;

    /** The queue of waiting threads. */
    private final ConcurrentLinkedQueue<Thread> waiters;

    ///..
    /** The internal pool status flag. */
    private boolean is_closed;

    ///
    /**
     * Instantiates a new {@code ConnectionPool} object and starts the log task.
     * @apiNote Since this class is a singleton, this constructor will only be called once.
    */
    private ConnectionPool() {

        logger = Logger.getInstance();
        ConfigurationProvider cfg_p = ConfigurationProvider.getInstance();

        driver_properties = new Properties();

        driver_properties.setProperty("user", cfg_p.DATABASE_USERNAME);
        driver_properties.setProperty("password", cfg_p.DATABASE_PASSWORD);
        driver_properties.setProperty("prepareThreshold", Integer.toString(cfg_p.PREPARE_THRESHOLD));
        driver_properties.setProperty("preparedStatementCacheQueries", Integer.toString(cfg_p.MAX_NUM_CACHEABLE_STATEMENTS));
        driver_properties.setProperty("preparedStatementCacheSizeMiB", Integer.toString(cfg_p.MAX_STATEMENTS_CACHE_ENTRY_SIZE));

        pool = new PooledConnection[cfg_p.NUM_DATABASE_CONNECTIONS];
        waiters = new ConcurrentLinkedQueue<>();

        try {

            for(int i = 0; i < cfg_p.NUM_DATABASE_CONNECTIONS; i++) {

                Connection connection = DriverManager.getConnection(cfg_p.DATABASE_ADDRESS, driver_properties);
                connection.setAutoCommit(false);
                pool[i] = new PooledConnection(connection);
            }
        }

        catch(SQLException exc) {

            logger.log(

                ExceptionFormatter.format("ConnectionPool.new => Could not populate the pool", exc, "Aborting..."),
                LogLevels.FATAL
            );

            close();
            System.exit(1);
        }

        is_closed = false;
        logger.log("ConnectionPool.new => Instantiation successfull", LogLevels.SUCCESS);
    }

    ///
    /** @return The {@link ConnectionPool} instance created during class loading. */
    public static ConnectionPool getInstance() {

        return(INSTANCE);
    }

    ///
    /**
     * Aquires a database connection from the pool.
     * @return One connection from the pool.
     * @apiNote This method does NOT guarantee that the aquired connections are valid. If not, a refresh is needed.
    */
    public PooledConnection aquireConnection() {

        int id = (int)Thread.currentThread().threadId();
        PooledConnection connection;

        while(true) {

            for(int i = 0; i < pool.length; i++) {

                connection = pool[(id + i) % pool.length];

                if(connection.getAvailable().get() == true) {

                    connection.getAvailable().set(false);
                    return(connection);
                }
            }

            waiters.offer(Thread.currentThread());
            LockSupport.park();
        }
    }

    ///..
    /**
     * Releases the specified database connection back into the pool.
     * @param connection : The connection to release.
     * @throws IllegalArgumentException If {@code connection} is {@code null}.
    */
    public void releaseConnection(PooledConnection connection) throws IllegalArgumentException {

        if(connection == null) {

            throw new IllegalArgumentException("ConnectionPool.releaseCOnnection -> The input parameter cannot be null");
        }

        connection.getAvailable().set(true);
        LockSupport.unpark(waiters.poll());
    }

    ///..
    /**
     * Releases and closes all connections in the pool.
     * @throws IllegalCallerException If this method is called by anyone other than the {@code main} task.
    */
    public void close() throws IllegalCallerException {

        if(TaskManager.getInstance().isMain()) {

            if(is_closed == false) {

                for(PooledConnection connection : pool) {

                    connection.getAvailable().set(false);
                    ResourceReleaserInternal.release(logger, "ConnectionPool", "closeConnection", connection.getConnection());
                }

                is_closed = true;
            }
        }

        else {

            throw new IllegalCallerException("ConnectionPool.closeConnection -> Not main task");
        }
    }

    ///.
    /** @return The never {@code null} currently set JDBC driver properties. */
    protected Properties getDriverProperties() {

        return(driver_properties);
    }

    ///
}
