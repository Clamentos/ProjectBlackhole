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

    public QueryWorker(BlockingQueue<QueryWrapper> query_queue) throws InstantiationException {

        super(query_queue);
        LOGGER = Logger.getInstance();
        attempt();
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
    }

    //____________________________________________________________________________________________________________________________________

    private void attempt() throws InstantiationException {

        for(int i = 0; i < ConfigurationProvider.MAX_DB_CONNECTION_RETRIES; i++) {

            try {

                db_connection = DriverManager.getConnection(

                    ConfigurationProvider.DB_URL,
                    ConfigurationProvider.DB_USERNAME,
                    ConfigurationProvider.DB_PASWORD
                );

                return;
            }

            catch(SQLException exc) {

                LOGGER.log("Could not connect to the database, SQLException: " + exc.getMessage(), LogLevel.ERROR);
            }

            try {

                Thread.sleep(1000);
            }

            catch(InterruptedException exc) {

                LOGGER.log("Interrupted while waiting on retries, InterruptedException: " + exc.getMessage(), LogLevel.NOTE);
            }
        }

        throw new InstantiationException("Retries exhausted while attempting to connect to the database.");
    }

    //____________________________________________________________________________________________________________________________________
}
