package io.github.clamentos.blackhole.persistence.repositories;

///
import io.github.clamentos.blackhole.configuration.ConfigurationProvider;
import io.github.clamentos.blackhole.exceptions.Failures;
import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.network.transfer.dtos.TagFilter;
import io.github.clamentos.blackhole.persistence.PersistenceException;
import io.github.clamentos.blackhole.persistence.QueryParameter;
import io.github.clamentos.blackhole.persistence.models.TagEntity;
import io.github.clamentos.blackhole.persistence.pool.ConnectionPool;
import io.github.clamentos.blackhole.persistence.pool.PooledConnection;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
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
        PreparedStatement statement = null;

        for(int i = 0; i < MAX_DB_ATTEMPTS; i++) {

            try {

                int idx = 1;
                statement = connection.connection().prepareStatement(
                    
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

                    if(statement != null) {

                        try {

                            statement.close();
                        }

                        catch(SQLException exc2) {

                            //...
                        }
                    }

                    connection_pool.releaseConnection(connection);
                    throw new PersistenceException(exc);
                }
            }
        }

        connection_pool.releaseConnection(connection);
        throw new PersistenceException(Failures.DB_RETRIES_EXHAUSTED);
    }

    public List<TagEntity> read(TagFilter filter) throws PersistenceException {

        List<TagEntity> tags = new ArrayList<>();
        List<QueryParameter> parameters = new ArrayList<>();
        PooledConnection connection = connection_pool.aquireConnection();
        
        for(int i = 0; i < MAX_DB_ATTEMPTS; i++) {

            try {

                PreparedStatement statement = connection.connection().prepareStatement(buildQueryByFilters(filter, parameters));
                
                for(int j = 0; j < parameters.size(); j++) {

                    statement.setObject(j + 1, parameters.get(j).value(), parameters.get(j).type());
                }

                ResultSet result = statement.executeQuery();
                tags = TagEntity.newInstances(result);
                
                result.close();
                statement.close();
                connection_pool.refreshConnection(connection);

                return(tags);
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
    private String buildQueryByFilters(TagFilter filter, List<QueryParameter> parameters) {

        StringBuilder query = new StringBuilder("");
        boolean flag = false;

        if(filter.mode() == 0) {

            query.append("SELECT");

            if((filter.fields() & 0b0001) > 0) query.append(" \"id\"");
            if((filter.fields() & 0b0010) > 0) query.append(" \"name\"");
            if((filter.fields() & 0b0100) > 0) query.append(" \"creation_date\"");
        }

        else {

            query.append("COUNT(*)");
        }

        query.append(" FROM \"Tags\" WHERE");

        if(filter.by_ids() != null && filter.by_ids().size() > 0) {

            query.append(" \"id\" ANY (?)");
            parameters.add(new QueryParameter(JDBCType.ARRAY, filter.by_ids()));

            return(query.toString());
        }

        if(filter.by_names() != null && filter.by_names().size() > 0) {

            query.append(" \"name\" ANY (?)");
            parameters.add(new QueryParameter(JDBCType.ARRAY, filter.by_names()));

            return(query.toString());
        }

        if(filter.name_like() != null && !filter.name_like().equals("")) {

            query.append(" \"name\" LIKE ?");
            parameters.add(new QueryParameter(JDBCType.VARCHAR, filter.name_like() + "%"));
            flag = true;
        }

        if(filter.creation_date_start() > 0) {

            if(flag == true) query.append(" AND");

            query.append(" \"creation_date\" >= ?");
            parameters.add(new QueryParameter(JDBCType.INTEGER, filter.creation_date_start()));
            flag = true;
        }

        if(filter.creation_date_end() > 0) {

            if(flag == true) query.append(" AND");
            
            query.append(" \"creation_date\" <= ?");
            parameters.add(new QueryParameter(JDBCType.INTEGER, filter.creation_date_end()));
        }

        return(query.toString());
    }

    ///
}
