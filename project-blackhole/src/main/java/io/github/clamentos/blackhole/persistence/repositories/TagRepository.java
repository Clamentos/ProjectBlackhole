package io.github.clamentos.blackhole.persistence.repositories;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.persistence.PersistenceException;
import io.github.clamentos.blackhole.persistence.Queries;
import io.github.clamentos.blackhole.persistence.models.TagEntity;
import io.github.clamentos.blackhole.persistence.pool.ConnectionPool;
import io.github.clamentos.blackhole.persistence.pool.PooledConnection;

public class TagRepository {
    
    private static final TagRepository INSTANCE = new TagRepository();

    private Logger logger;
    private ConnectionPool connection_pool;

    private TagRepository() {

        logger = Logger.getInstance();
        connection_pool = ConnectionPool.getInstance();

        logger.log("TagRepository.new > Instantiation Successfull", LogLevel.SUCCESS);
    }

    public static TagRepository getInstance() {

        return(INSTANCE);
    }

    public void insert(List<TagEntity> tags) throws SQLException {

        PooledConnection db_connection = connection_pool.aquireConnection();
        PreparedStatement statement = db_connection.getAssociatedStatements().get(Queries.INSERT_TAG);
        int idx = 1;

        try {

            for(TagEntity tag : tags) {

                statement.setString(idx, tag.name());
                statement.setInt(idx + 1, tag.creation_date());
                statement.addBatch();

                idx += 2;
            }

            statement.executeBatch();
        }

        catch(SQLException exc) {

            // check if the sql status signifies a connection error and of so, retry.
            // else propagate.
        }

        connection_pool.releaseConnection(db_connection);
    }

    public List<TagEntity> select(int[] ids) throws SQLException, PersistenceException {

        PooledConnection db_connection = connection_pool.aquireConnection();
        PreparedStatement statement = db_connection.getAssociatedStatements().get(Queries.SELECT_TAG_0);

        try {

            Array id_params = statement.getConnection().createArrayOf(
            
                "INTEGER", 
                Arrays.stream(ids).boxed().toArray(Integer[]::new)
            );

            statement.setArray(1, id_params);
            return(TagEntity.newInstances(statement.executeQuery()));
        }

        catch(SQLException exc) {

            // check if the sql status signifies a connection error and of so, retry.
            // else propagate.
        }
    }
}
