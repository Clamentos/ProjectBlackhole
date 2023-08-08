package io.github.clamentos.blackhole.persistence.pool;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.scaffolding.tasks.ContinuousTask;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

//________________________________________________________________________________________________________________________________________

/**
 * <h3>Database connection pool refresher</h3>
 * This class is responsible for periodically check and refresh all available connections in the pool.
 * Connections that are actively used won't be checked.
 * @apiNote This class is a <b>continuous runnable task</b>.
*/
public class ConnectionCheckingTask extends ContinuousTask {
    
    private final int POOL_TASK_SCHEDULE_TIME;
    private final int NUM_DB_CONNECTIONS;
    private final String DB_ADDRESS;
    private final String DB_USERNAME;
    private final String DB_PASSWORD;

    private Logger logger;
    private BlockingQueue<Connection> pool;

    //____________________________________________________________________________________________________________________________________

    /**
     * Instantiates a new {@link ConnectioncheckingTask} object.
     * @param pool : The connection pool.
     * @param id : The unique task id.
    */
    public ConnectionCheckingTask(BlockingQueue<Connection> pool, long id) {

        super(id);

        logger = Logger.getInstance();
        POOL_TASK_SCHEDULE_TIME = ConfigurationProvider.getInstance().POOL_TASK_SCHEDULE_TIME;
        NUM_DB_CONNECTIONS = ConfigurationProvider.getInstance().NUM_DB_CONNECTIONS;
        DB_ADDRESS = ConfigurationProvider.getInstance().DB_ADDRESS;
        DB_USERNAME = ConfigurationProvider.getInstance().DB_USERNAME;
        DB_PASSWORD = ConfigurationProvider.getInstance().DB_PASSWORD;
        this.pool = pool;

        logger.log("ConnectionCheckingTask.new > Instantiation successfull", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    /** {@inheritDoc} */
    @Override
    public void setup() {

        logger.log("ConnectionCheckingTask.setup > Setup successfull", LogLevel.SUCCESS);
    }

    /** {@inheritDoc} */
    @Override
    public void work() {

        Iterator<Connection> connections;

        logger.log("ConnectionCheckingTask.work > Sweep started", LogLevel.INFO);
        connections = pool.iterator();

        while(connections.hasNext() == true) {

            try {

                ConnectionUtility.refresh(connections.next(), DB_ADDRESS, DB_USERNAME, DB_PASSWORD);
            }

            catch(SQLException exc) { // Should never happen since the timeout value is > 0.

                logger.log(

                    "ConnectionCheckingTask.work > SQLException: " + exc.getMessage(),
                    LogLevel.ERROR
                );

                super.stop();
            }
        }

        logger.log("ConnectionCheckingTask.work > Sweep completed", LogLevel.SUCCESS);
        sleep(POOL_TASK_SCHEDULE_TIME);
    }

    /** {@inheritDoc} */
    @Override
    public void terminate() {

        // Wait for all the connections to be released.
        while(pool.size() < NUM_DB_CONNECTIONS) {

            sleep(100);
        }

        for(Connection connection : pool) {

            try {

                connection.close();
            }

            catch(SQLException exc) {

                logger.log(
                    
                    "ConnectionCheckingTask.terminate > Could not close connection, SQLException: " +
                    exc.getMessage() + " Skipping this one",
                    LogLevel.ERROR
                );
            }
        }

        logger.log("ConnectionCheckingTask.terminate > Shut down successfull", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    // Thread.sleep wrapping method.
    private void sleep(long millis) {

        try {

            Thread.sleep(millis);
        }

        catch(InterruptedException exc) {

            logger.log(
                    
                "ConnectionCheckingTask.sleep > Interrupted while waiting",
                LogLevel.WARNING
            );
        }
    }

    //____________________________________________________________________________________________________________________________________
}
