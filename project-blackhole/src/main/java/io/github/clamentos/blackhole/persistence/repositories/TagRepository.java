package io.github.clamentos.blackhole.persistence.repositories;


import io.github.clamentos.blackhole.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.exceptions.Failures;///
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.persistence.PersistenceException;
import io.github.clamentos.blackhole.persistence.Queries;
import io.github.clamentos.blackhole.persistence.models.TagEntity;
import io.github.clamentos.blackhole.persistence.pool.ConnectionPool;
import io.github.clamentos.blackhole.persistence.pool.PooledConnection;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.Arrays;
import java.util.List;

///
public class TagRepository {
    
    ///
    private static final TagRepository INSTANCE = new TagRepository();

    private final int MAX_DB_ATTEMPTS;

    private Logger logger;
    private ConnectionPool connection_pool;

    ///
    private TagRepository() {

        logger = Logger.getInstance();
        connection_pool = ConnectionPool.getInstance();

        MAX_DB_ATTEMPTS = ConfigurationProvider.getInstance().MAX_DB_ATTEMPTS;

        logger.log("TagRepository.new > Instantiation Successfull", LogLevel.SUCCESS);
    }

    ///
    public static TagRepository getInstance() {

        return(INSTANCE);
    }

    ///
    public void insert(List<TagEntity> tags) throws PersistenceException {

        PooledConnection db_connection = connection_pool.aquireConnection();

        for(int i = 0; i < MAX_DB_ATTEMPTS; i++) {

            try {

                int idx = 1;
                PreparedStatement statement = db_connection.getAssociatedStatement(Queries.INSERT_TAGS);

                for(TagEntity tag : tags) {

                    statement.setString(idx, tag.name());
                    statement.setInt(idx + 1, tag.creation_date());
                    statement.addBatch();

                    idx += 2;
                }

                statement.executeBatch();

                return;
            }

            catch(SQLException exc) {

                if(exc.getSQLState().substring(0, 2).equals("08")) {

                    db_connection = connection_pool.refreshConnection(db_connection);
                }

                else {

                    throw new PersistenceException(exc);
                }
            }
        }

        connection_pool.releaseConnection(db_connection);
        throw new PersistenceException(Failures.DB_RETRIES_EXHAUSTED);
    }

    public List<TagEntity> selectByIds(int[] ids) throws PersistenceException {

        List<TagEntity> tags;
        PooledConnection db_connection = connection_pool.aquireConnection();

        for(int i = 0; i < MAX_DB_ATTEMPTS; i++) {

            try {

                PreparedStatement statement = db_connection.getAssociatedStatement(Queries.SELECT_TAGS_BY_IDS);

                Array id_params = db_connection.getDbConnection().createArrayOf(

                    "INTEGER", 
                    Arrays.stream(ids).boxed().toArray(Integer[]::new)
                );

                statement.setArray(1, id_params);
                tags = TagEntity.newInstances(statement.executeQuery());
                connection_pool.releaseConnection(db_connection);

                return(tags);
            }

            catch(SQLException exc) {

                if(exc.getSQLState().substring(0, 2).equals("08")) {

                    db_connection = connection_pool.refreshConnection(db_connection);
                }

                else {

                    throw new PersistenceException(exc);
                }
            }
        }

        connection_pool.releaseConnection(db_connection);
        throw new PersistenceException(Failures.DB_RETRIES_EXHAUSTED);
    }

    public List<TagEntity> selectByDate(int start, int end) throws PersistenceException {

        List<TagEntity> tags;
        PooledConnection db_connection = connection_pool.aquireConnection();

        for(int i = 0; i < MAX_DB_ATTEMPTS; i++) {

            try {

                PreparedStatement statement = db_connection.getAssociatedStatement(Queries.SELECT_TAGS_BY_DATE);

                statement.setInt(1, start);
                statement.setInt(2, end);
                tags = TagEntity.newInstances(statement.executeQuery());
                connection_pool.releaseConnection(db_connection);

                return(tags);
            }

            catch(SQLException exc) {

                if(exc.getSQLState().substring(0, 2).equals("08")) {

                    db_connection = connection_pool.refreshConnection(db_connection);
                }

                else {

                    throw new PersistenceException(exc);
                }
            }
        }

        connection_pool.releaseConnection(db_connection);
        throw new PersistenceException(Failures.DB_RETRIES_EXHAUSTED);
    }

    // select by name
    // select by name and date
    // update
    // delete

    ///
}
