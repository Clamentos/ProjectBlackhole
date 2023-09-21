package io.github.clamentos.blackhole.persistence.repositories;

///
import io.github.clamentos.blackhole.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.exceptions.Failures;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.network.transfer.dtos.TagFilter;
import io.github.clamentos.blackhole.persistence.PersistenceException;
import io.github.clamentos.blackhole.persistence.models.TagEntity;
import io.github.clamentos.blackhole.persistence.pool.ConnectionPool;
import io.github.clamentos.blackhole.persistence.pool.PooledConnection;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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

        PooledConnection connection = connection_pool.aquireConnection();

        for(int i = 0; i < MAX_DB_ATTEMPTS; i++) {

            try {

                int idx = 1;
                PreparedStatement statement = connection.connection().prepareStatement(
                    
                    "INSERT INTO \"Tags\"(\"name\", \"creation_date\") VALUES(?, ?)"
                );

                for(TagEntity tag : tags) {

                    statement.setString(idx, tag.name());
                    statement.setInt(idx + 1, tag.creation_date());
                    statement.addBatch();

                    idx += 2;
                }

                statement.executeBatch();
                statement.close();
                connection_pool.releaseConnection(connection);

                return;
            }

            catch(SQLException exc) {

                if(exc.getSQLState().substring(0, 2).equals("08")) {

                    connection = connection_pool.refreshConnection(connection);
                }

                else {

                    connection_pool.releaseConnection(connection);
                    throw new PersistenceException(exc);
                }
            }
        }

        connection_pool.releaseConnection(connection);
        throw new PersistenceException(Failures.DB_RETRIES_EXHAUSTED);
    }

    public List<TagEntity> read(TagFilter query) throws PersistenceException {

        return(null);
    }
    
    public void update(List<TagEntity> tags) throws PersistenceException {

        PooledConnection connection = connection_pool.aquireConnection();

        for(int i = 0; i < MAX_DB_ATTEMPTS; i++) {

            try {

                int idx = 1;
                PreparedStatement statement = connection.connection().prepareStatement(
                    
                    "UPDATE \"Tags\" SET \"name\" = ? WHERE \"id\" = ?"
                );

                for(TagEntity tag : tags) {

                    statement.setString(idx, tag.name());
                    statement.setInt(idx + 1, tag.id());
                    statement.addBatch();

                    idx += 2;
                }

                statement.executeBatch();
                statement.close();
                connection_pool.releaseConnection(connection);

                return;
            }

            catch(SQLException exc) {

                if(exc.getSQLState().substring(0, 2).equals("08")) {

                    connection = connection_pool.refreshConnection(connection);
                }

                else {

                    connection_pool.releaseConnection(connection);
                    throw new PersistenceException(exc);
                }
            }
        }

        connection_pool.releaseConnection(connection);
        throw new PersistenceException(Failures.DB_RETRIES_EXHAUSTED);
    }

    public void delete(List<Integer> ids) throws PersistenceException {

        PooledConnection connection = connection_pool.aquireConnection();

        for(int i = 0; i < MAX_DB_ATTEMPTS; i++) {

            try {

                int idx = 1;
                PreparedStatement statement = connection.connection().prepareStatement(
                    
                    "DELETE FROM \"Tags\" WHERE \"id\" = ?"
                );

                for(Integer id : ids) {

                    statement.setInt(idx, id);
                    statement.addBatch();

                    idx++;
                }

                statement.executeBatch();
                statement.close();
                connection_pool.releaseConnection(connection);

                return;
            }

            catch(SQLException exc) {

                if(exc.getSQLState().substring(0, 2).equals("08")) {

                    connection = connection_pool.refreshConnection(connection);
                }

                else {

                    connection_pool.releaseConnection(connection);
                    throw new PersistenceException(exc);
                }
            }
        }

        connection_pool.releaseConnection(connection);
        throw new PersistenceException(Failures.DB_RETRIES_EXHAUSTED);
    }

    ///
}
