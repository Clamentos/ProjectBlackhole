package io.github.clamentos.blackhole.persistence.pool;

///
import io.github.clamentos.blackhole.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;

import java.sql.SQLException;

import java.util.concurrent.LinkedBlockingQueue;

///
/**
 * <h3>Database connection pool</h3>
 * This class implements connection pooling for the persistence layer.
 * @apiNote This class is an <b>eager-loaded singleton</b>.
*/
public class ConnectionPool {
    
    private static final ConnectionPool INSTANCE = new ConnectionPool();
    
    private final int NUM_DB_CONNECTIONS;
    private final String DB_ADDRESS;
    private final String DB_USERNAME;
    private final String DB_PASSWORD;

    private Logger logger;
    private LinkedBlockingQueue<PooledConnection> pool;   // TODO: multiple queues to spread lock contention

    ///
    // Populate the queue with connections. Each connection has ALL the statements ready to go.
    private ConnectionPool() {

        logger = Logger.getInstance();

        NUM_DB_CONNECTIONS = ConfigurationProvider.getInstance().NUM_DB_CONNECTIONS;
        DB_ADDRESS = ConfigurationProvider.getInstance().DB_ADDRESS;
        DB_USERNAME = ConfigurationProvider.getInstance().DB_USERNAME;
        DB_PASSWORD = ConfigurationProvider.getInstance().DB_PASSWORD;

        pool = new LinkedBlockingQueue<>(NUM_DB_CONNECTIONS);

        try {

            for(int i = 0; i < NUM_DB_CONNECTIONS; i++) {

                pool.add(ConnectionUtility.create(DB_ADDRESS, DB_USERNAME, DB_PASSWORD));
            }

            logger.log("ConnectionPool.new > Instantiation successfull", LogLevel.SUCCESS);
        }

        catch(SQLException exc) {

            logger.log(
                
                "ConnectionPool.new > Could not instantiate, SQLException: " + exc.getMessage() +
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

                return(pool.take());
            }

            catch(InterruptedException exc) {

                logger.log("ConnectionPool.aquireConnection > Interrupted, ignoring", LogLevel.WARNING);
            }
        }
    }

    /**
     * Releases the specified {@link PooledConnection} back into the pool.
     * @param connection : The connection.
     * @throws IllegalStateException If the pool capacity is somehow exceeded.
    */
    public void releaseConnection(PooledConnection connection) throws IllegalStateException {

        pool.add(connection);
    }

    ///
}
