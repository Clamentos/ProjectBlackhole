package io.github.clamentos.blackhole.persistence;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.common.Worker;
import io.github.clamentos.blackhole.common.config.ConfigurationProvider;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.concurrent.BlockingQueue;

//________________________________________________________________________________________________________________________________________

/**
 * Query thread that actually executes the queries.
*/
public class QueryWorker extends Worker<QueryWrapper> {

    private final Logger LOGGER;
    private Connection db_connection;

    //____________________________________________________________________________________________________________________________________

    /**
     * Instantiate a new {@lonk QueryWorker}.
     * @param query_queue : The resource queue from which the worker will consume.
    */
    public QueryWorker(BlockingQueue<QueryWrapper> query_queue) {

        super(query_queue);
        LOGGER = Logger.getInstance();
        LOGGER.log("Query worker started", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    /**
     * Method that executes the queries.
     * @param query : The query to execute.
    */
    @Override
    public void doWork(QueryWrapper query) {

        try {
            
            refresh();
            PreparedStatement statement = db_connection.prepareStatement(query.getSql());

            for(int i = 0; i < query.getParameters().size(); i++) {

                statement.setObject(i, query.getParameters().get(i));

                if(query.getQueryType() == QueryType.INSERT || query.getQueryType() == QueryType.UPDATE) {

                    statement.addBatch();
                }
            }

            if(query.getQueryType() == QueryType.INSERT || query.getQueryType() == QueryType.UPDATE) {

                statement.executeBatch();
                query.setResult(null);
            }

            else {

                query.setResult(statement.executeQuery());
            }

            query.setStatus(true);
        }

        catch(SQLException exc) {

            query.setStatus(false);
            LOGGER.log("Could not execute query, SQLException: " + exc.getMessage(), LogLevel.ERROR);
        }
    }

    /**
     * {@inheritDoc}
    */
    @Override
    public void catchInterrupted(InterruptedException exc) {

        LOGGER.log("Interrupted while waiting on queue, InterruptedException: " + exc.getMessage(), LogLevel.NOTE);

        if(super.getRunning() == false) {

            try {

                db_connection.close();
            }

            catch(SQLException exc2) {

                LOGGER.log("Could not close the database connection, SQLException: " + exc.getMessage(), LogLevel.ERROR);
            }
        }
    }

    //____________________________________________________________________________________________________________________________________

    private void refresh() throws SQLException {

        if(db_connection == null || db_connection.isValid(ConfigurationProvider.MAX_DB_CONNECTION_TIMEOUT) == false) {

            db_connection = DriverManager.getConnection(

                ConfigurationProvider.DB_URL,
                ConfigurationProvider.DB_USERNAME,
                ConfigurationProvider.DB_PASWORD
            );
        }
    }

    //____________________________________________________________________________________________________________________________________
}
