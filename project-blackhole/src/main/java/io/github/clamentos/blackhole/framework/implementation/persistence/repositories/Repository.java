package io.github.clamentos.blackhole.framework.implementation.persistence.repositories;

///
import io.github.clamentos.blackhole.framework.implementation.logging.Logger;

///..
import io.github.clamentos.blackhole.framework.implementation.persistence.pool.PooledConnection;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.ResourceReleaser;
import io.github.clamentos.blackhole.framework.implementation.utility.SqlExceptionDecoder;

///..
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.DatabaseConnectionException;
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.PersistenceException;
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.ResultSetMappingException;

///..
import io.github.clamentos.blackhole.framework.scaffolding.persistence.Entities;
import io.github.clamentos.blackhole.framework.scaffolding.persistence.Entity;
import io.github.clamentos.blackhole.framework.scaffolding.persistence.Filter;
import io.github.clamentos.blackhole.framework.scaffolding.persistence.ResultSetMapper;

///.
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

///..
import java.util.List;

///
/**
 * <h3>Repository</h3>
 * Entity repository that provides basic CRUD operations.
*/
public class Repository {

    ///
    /** Singleton instance of {@code this} instantiated during class loading. */
    private static final Repository INSTANCE = new Repository();

    ///.
    /** The service used to log notable events. */
    private final Logger logger;

    ///
    /**
     * <p>Instantiates a new {@code Repository}.</p>
     * Since this class is a singleton, this constructor will only be called once.
    */
    private Repository() {

        logger = Logger.getInstance();
    }

    ///
    /** @return The {@link Repository} instance created during class loading. */
    public static Repository getInstance() {

        return(INSTANCE);
    }

    ///
    /**
     * <p>Inserts the specified entities into the corresponding table in the database.</p>
     * <b>NOTE: This method assumes that all entities are of the same type.</b>
     * @param entities : The entities to be persisted.
     * @param descriptor : The entities descriptor.
     * @param connection : The connection to the database.
     * @param commit : The flag used to indicate if the transaction should be committed or not.
     * @throws IllegalArgumentException If either {@code entities}, {@code descriptor} or {@code connection} are {@code null}.
     * @throws PersistenceException If any database access error occurrs.
     * @see Entity
     * @see Entities
     * @see PooledConnection
    */
    public void insert(

        List<? extends Entity> entities,
        Entities<? extends Enum<?>> descriptor,
        PooledConnection connection,
        boolean commit

    ) throws IllegalArgumentException, PersistenceException {

        if(entities == null || descriptor == null || connection == null) {

            throw new IllegalArgumentException("(Repository.insert) -> The input arguments cannot be null");
        }

        PreparedStatement statement = null;

        try {

            if(entities.size() == 0) {

                conditionalCommit(connection, commit);
                return;
            }

            statement = prepareInsert(entities, descriptor, connection);
            conditionalBatchExecute(statement, entities.size());
            conditionalCommit(connection, commit);
        }
        
        catch(PersistenceException exc) {

            try {

                // Immediately rollback.
                connection.getConnection().rollback();

                // If connection problem -> refresh & retry.
                if(exc instanceof DatabaseConnectionException) {

                    connection.refreshConnection();

                    if(statement == null) {

                        statement = prepareInsert(entities, descriptor, connection);
                    }

                    conditionalBatchExecute(statement, entities.size());
                    conditionalCommit(connection, commit);
                }

                else {

                    // The exception was not caused by a network / connection problem -> cannot recover...

                    ResourceReleaser.release(logger, "Repository.insert", statement);
                    throw exc;
                }
            }

            catch(SQLException exc2) {

                // Failed while retrying, give up...

                ResourceReleaser.release(logger, "Repository.insert", statement);
                throw SqlExceptionDecoder.decode("(Repository.insert) -> Could not insert: ", exc2);
            }
        }

        ResourceReleaser.release(logger, "Repository.insert", statement);
    }

    ///..
    /**
     * Fetches the entities from the database filtered by the specified filter.
     * @param filter : The query filter.
     * @param connection : The database connection.
     * @return The JDBC result set holding the found entities.
     * @throws IllegalArgumentException If either {@code filter}, {@code connection} or {@code mapper} are {@code null}.
     * @throws PersistenceException If any database access error occurrs.
     * @see Filter
     * @see PooledConnection
     * @see ResultSetMapper
    */
    public <T extends Entity> List<T> select(Filter filter, PooledConnection connection, ResultSetMapper<T> mapper)
    throws IllegalArgumentException, PersistenceException {

        if(filter == null || connection == null || mapper == null) {

            throw new IllegalArgumentException("(Repository.select) -> The input arguments cannot be null");
        }

        PreparedStatement statement = null;
        ResultSet result_set = null;

        try {

            statement = prepareSelect(filter, connection);
            result_set = statement.executeQuery();
        }

        catch(SQLException | PersistenceException exc) {

            try {

                // If connection problem -> refresh & retry.
                if(exc instanceof DatabaseConnectionException) {

                    connection.refreshConnection();

                    if(statement == null) {

                        statement = prepareSelect(filter, connection);
                    }

                    result_set = statement.executeQuery();
                }

                else {

                    // The exception was not caused by a network / connection problem -> cannot recover...

                    ResourceReleaser.release(logger, "Repository.select", statement, result_set);
                    throw exc;
                }
            }

            catch(SQLException exc2) {

                // Failed while retrying, give up...

                ResourceReleaser.release(logger, "Repository.select", statement, result_set);
                throw SqlExceptionDecoder.decode("(Repository.select) -> Could not select: ", exc2);
            }
        }

        try {

            List<T> entities = mapper.map(result_set);
            ResourceReleaser.release(logger, "Repository.select", statement, result_set);

            return(entities);
        }

        catch(ResultSetMappingException exc3) {

            ResourceReleaser.release(logger, "Repository.select", statement, result_set);
            throw exc3;
        }
    }

    ///..
    /**
     * Updates the database table associated to the provided entity.
     * @param entity : The entity to be persisted.
     * @param descriptor : The entity descriptor.
     * @param fields : The fields to update in the table.
     * This parameter works as a checklist starting from the first field which maps to least significant bit.
     * @param connection : The connection to the database.
     * @param commit : The flag used to indicate if the transaction should be committed or not.
     * @throws IllegalArgumentException If either {@code entity}, {@code descriptor} or {@code connection} are {@code null}.
     * @throws PersistenceException If any database access error occurrs.
     * @see Entity
     * @see Entities
     * @see PooledConnection
    */
    public void update(
        
        Entity entity,
        Entities<? extends Enum<?>> descriptor,
        long fields,
        PooledConnection connection,
        boolean commit

    ) throws IllegalArgumentException, PersistenceException {

        if(entity == null || descriptor == null || connection == null) {

            throw new IllegalArgumentException("(Repository.update) -> The input arguments cannot be null");
        }

        PreparedStatement statement = null;

        try {

            statement = prepareUpdate(entity, descriptor, fields, connection);
            statement.executeUpdate();
            conditionalCommit(connection, commit);
        }

        catch(SQLException | PersistenceException exc) {

            try {

                // Immediately rollback.
                connection.getConnection().rollback();

                // If connection problem -> refresh & retry.
                if(exc instanceof DatabaseConnectionException) {

                    connection.refreshConnection();

                    if(statement == null) {

                        statement = prepareUpdate(entity, descriptor, fields, connection);
                    }

                    statement.executeUpdate();
                    conditionalCommit(connection, commit);
                }

                else {

                    // The exception was not caused by a network / connection problem -> cannot recover...

                    ResourceReleaser.release(logger, "Repository.update", statement);
                    throw exc;
                }
            }

            catch(SQLException exc2) {

                // Failed while retrying, give up...

                ResourceReleaser.release(logger, "Repository.update", statement);
                throw SqlExceptionDecoder.decode("(Repository.update) -> Could not update: ", exc2);
            }
        }

        ResourceReleaser.release(logger, "Repository.update", statement);
    }

    ///..
    /**
     * Removes from the specified database table the recods matching the provided keys.
     * @param keys : The primary keys of the records.
     * @param descriptor : The entity descriptor.
     * @param connection : The connection to the database.
     * @param commit : The flag used to indicate if the transaction should be committed or not.
     * @throws IllegalArgumentException If either {@code keys}, {@code descriptor} or {@code connection} are {@code null}.
     * @throws PersistenceException If any database access error occurrs.
     * @see PooledConnection
    */
    public void delete(List<Object> keys, Entities<? extends Enum<?>> descriptor, PooledConnection connection, boolean commit)
    throws IllegalArgumentException, PersistenceException {

        if(keys == null || descriptor == null || connection == null) {

            throw new IllegalArgumentException("(Repository.delete) -> The input arguments cannot be null");
        }

        if(keys.size() == 0) {

            conditionalCommit(connection, commit);
            return;
        }

        PreparedStatement statement = null;

        try {

            statement = prepareDelete(keys, descriptor, connection);
            statement.executeUpdate();
            conditionalCommit(connection, commit);
        }

        catch(SQLException | PersistenceException exc) {

            try {

                // Immediately rollback.
                connection.getConnection().rollback();

                // If connection problem -> refresh & retry.
                if(exc instanceof DatabaseConnectionException) {

                    connection.refreshConnection();

                    if(statement == null) {

                        statement = prepareDelete(keys, descriptor, connection);
                    }

                    statement.executeUpdate();
                    conditionalCommit(connection, commit);
                }

                else {

                    // The exception was not caused by a network / connection problem -> cannot recover...

                    ResourceReleaser.release(logger, "Repository.delete", statement);
                    throw exc;
                }
            }

            catch(SQLException exc2) {

                // Failed while retrying, give up...

                ResourceReleaser.release(logger, "Repository.delete", statement);
                throw SqlExceptionDecoder.decode("(Repository.delete) -> Could not delete: ", exc2);
            }
        }

        ResourceReleaser.release(logger, "Repository.delete", statement);
    }

    ///.
    private PreparedStatement prepareInsert(

        List<? extends Entity> entities,
        Entities<? extends Enum<?>> descriptor,
        PooledConnection pooled_connection

    ) throws PersistenceException {

        StringBuilder columns = new StringBuilder();
        String[] column_names = descriptor.getColumnNames();
        String table_name = descriptor.getTableName();
        boolean auto_key = descriptor.usesAutoKey();

        for(int i = (auto_key ? 1 : 0); i < column_names.length; i++) {

            columns.append(column_names[i] + ",");
        }

        columns.deleteCharAt(columns.length() - 1);
        String placeholders = getPlaceholders(auto_key ? column_names.length - 1 : column_names.length);

        try {

            PreparedStatement statement = pooled_connection.getConnection().prepareStatement(

                "INSERT INTO " + table_name + "(" + columns + ") VALUES(" + placeholders + ")"
            );

            pooled_connection.getQueryBinder().setStatement(statement);

            for(Entity entity : entities) {

                pooled_connection.getQueryBinder().resetIndex();
                entity.bindForInsert(pooled_connection.getQueryBinder());
    
                if(entities.size() > 1) {
    
                    statement.addBatch();
                }
            }

            return(statement);
        }

        catch(SQLException | PersistenceException exc) {

            if(exc instanceof SQLException) {

                throw SqlExceptionDecoder.decode("(Repository.prepareInsert) -> Could not prepare for insert: ", (SQLException)exc);
            }

            else {

                throw (PersistenceException)exc;
            }
        }
    }

    ///..
    private PreparedStatement prepareSelect(Filter filter, PooledConnection pooled_connection) throws PersistenceException {

        try {

            PreparedStatement statement = pooled_connection.getConnection().prepareStatement(filter.generateSelect());

            pooled_connection.getQueryBinder().setStatement(statement);
            pooled_connection.getQueryBinder().resetIndex();
            filter.bindForSelect(pooled_connection.getQueryBinder());

            return(statement);
        }

        catch(SQLException exc) {

            throw SqlExceptionDecoder.decode("(Repository.prepareSelect) -> Could not prepare for select: ", exc);
        }
    }

    ///..
    private PreparedStatement prepareUpdate(

        Entity entity,
        Entities<? extends Enum<?>> descriptor,
        long fields,
        PooledConnection pooled_connection

    ) throws PersistenceException {

        StringBuilder columns = new StringBuilder();
        String[] column_names = descriptor.getColumnNames();

        for(int i = 1; i < column_names.length; i++) {

            if((fields & (1 << i)) > 0) {

                columns.append(column_names[i] + "=?,");
            }
        }

        columns.deleteCharAt(columns.length() - 1);

        try {

            PreparedStatement statement = pooled_connection.getConnection().prepareStatement(

                "UPDATE " + descriptor.getTableName() + " SET " + columns + " WHERE " + column_names[0] + "=?"
            );

            pooled_connection.getQueryBinder().setStatement(statement);
            pooled_connection.getQueryBinder().resetIndex();
            entity.bindForUpdate(pooled_connection.getQueryBinder(), fields);

            return(statement);
        }

        catch(SQLException | PersistenceException exc) {

            if(exc instanceof SQLException) {

                throw SqlExceptionDecoder.decode("(Repository.prepareUpdate) -> Could not prepare for update: ", (SQLException)exc);
            }

            else {

                throw (PersistenceException)exc;
            }
        }
    }

    ///..
    private PreparedStatement prepareDelete(List<Object> keys, Entities<? extends Enum<?>> descriptor, PooledConnection pooled_connection) throws PersistenceException {

        String placeholders = getPlaceholders(keys.size());

        try {

            PreparedStatement statement = pooled_connection.getConnection().prepareStatement(

                "DELETE FROM " + descriptor.getTableName() + "WHERE " + descriptor.getColumnNames()[0] + " IN(" + placeholders + ")"
            );

            for(int i = 0; i < keys.size(); i++) {

                statement.setObject(i + 1, keys.get(i));
            }

            return(statement);
        }

        catch(SQLException exc) {

            throw SqlExceptionDecoder.decode("(Repository.prepareDelete) -> Could not prepare for delete: ", exc);
        }
    }

    ///..
    private String getPlaceholders(int amount) {

        return("?,".repeat(amount).substring(0, (amount * 2) - 1));
    }

    ///..
    private void conditionalCommit(PooledConnection connection, boolean commit) throws PersistenceException {

        try {

            if(commit == true) {

                connection.getConnection().commit();
            }
        }

        catch(SQLException exc) {

            throw SqlExceptionDecoder.decode("(Repository.conditionalCommit) -> Could not commit: ", exc);
        }
    }

    ///..
    private void conditionalBatchExecute(PreparedStatement statement, int size) throws PersistenceException {

        try {

            if(size > 1) {

                statement.executeBatch();
            }
    
            else {
    
                statement.executeUpdate();
            }
        }

        catch(SQLException exc) {

            throw SqlExceptionDecoder.decode("(Repository.conditionalBatchExecute) -> Could not execute: ", exc);
        }
    }

    ///
}
