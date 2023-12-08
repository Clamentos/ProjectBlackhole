package io.github.clamentos.blackhole.business.repositories;

/*import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import io.github.clamentos.blackhole.framework.implementation.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.framework.implementation.failures.Failures;
import io.github.clamentos.blackhole.framework.implementation.logging.LogLevels;
import io.github.clamentos.blackhole.framework.implementation.logging.Logger;
import io.github.clamentos.blackhole.framework.implementation.persistence.PersistenceException;
import io.github.clamentos.blackhole.framework.implementation.persistence.Query;
import io.github.clamentos.blackhole.framework.implementation.persistence.QueryParameter;
import io.github.clamentos.blackhole.framework.implementation.persistence.pool.ConnectionPool;
import io.github.clamentos.blackhole.framework.implementation.persistence.pool.PooledConnection;
import io.github.clamentos.blackhole.framework.scaffolding.persistence.Entity;

public abstract class Repository {

    private Logger logger;

    private final int MAX_DB_ATTEMPTS;

    private ConnectionPool connection_pool;

    public Repository() {

        logger = Logger.getInstance();
        connection_pool = ConnectionPool.getInstance();

        MAX_DB_ATTEMPTS = ConfigurationProvider.getInstance().MAX_DB_ATTEMPTS;

        logger.log("TagRepository.new > Instantiation Successfull", LogLevels.SUCCESS);
    }
    
    public void insert(List<Entity> entities, long task_id) throws PersistenceException {

        Query query = generateInsertQuery(entities);
        PooledConnection connection = connection_pool.aquireConnection(task_id);
        PreparedStatement statement = null;

        for(int i = 0; i < MAX_DB_ATTEMPTS; i++) {

            try {

                int idx = 1;
                statement = connection.connection().prepareStatement(query.sql());

                for(List<QueryParameter> parameters : query.parameters()) {

                    for(int j = 0; j < parameters.size(); j++) {

                        statement.setObject(idx, parameters.get(j).value(), parameters.get(j).type());
                    }

                    statement.addBatch();
                    idx += parameters.size();
                }

                statement.executeBatch();
                statement.close();
                connection_pool.releaseConnection(connection, task_id);

                return;
            }

            catch(SQLException exc) {

                if(exc.getSQLState().substring(0, 2).equals("08")) {

                    connection = connection_pool.refreshConnection(connection);
                }

                else {

                    if(statement != null) {

                        try {

                            statement.close();
                        }

                        catch(SQLException exc2) {

                            //...
                        }
                    }

                    connection_pool.releaseConnection(connection, task_id);
                    throw new PersistenceException(exc);
                }
            }
        }

        connection_pool.releaseConnection(connection, task_id);
        throw new PersistenceException(Failures.DB_RETRIES_EXHAUSTED);
    }

    public abstract Query generateInsertQuery(List<Entity> entities);
}*/
