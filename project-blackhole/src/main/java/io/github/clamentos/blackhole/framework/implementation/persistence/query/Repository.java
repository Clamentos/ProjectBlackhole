package io.github.clamentos.blackhole.framework.implementation.persistence.query;

///
import io.github.clamentos.blackhole.framework.implementation.logging.Logger;

///..
import io.github.clamentos.blackhole.framework.implementation.logging.exportable.LogLevels;
import io.github.clamentos.blackhole.framework.implementation.logging.exportable.MetricsTracker;

///..
import io.github.clamentos.blackhole.framework.implementation.persistence.pool.PooledConnection;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.ResourceReleaserInternal;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.exportable.SqlExceptionDecoder;

///..
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.DatabaseConnectionException;
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.PersistenceException;
import io.github.clamentos.blackhole.framework.scaffolding.exceptions.ResultSetMappingException;

///..
import io.github.clamentos.blackhole.framework.scaffolding.persistence.model.Entities;
import io.github.clamentos.blackhole.framework.scaffolding.persistence.model.Entity;

///..
import io.github.clamentos.blackhole.framework.scaffolding.persistence.query.Filter;
import io.github.clamentos.blackhole.framework.scaffolding.persistence.query.ResultSetMapper;

///.
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

///..
import java.util.ArrayList;
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

    /** The service used to update query success metrics. */
    private final MetricsTracker metrics_tracker;

    ///
    /**
     * Instantiates a new {@code Repository}.
     * @apiNote Since this class is a singleton, this constructor will only be called once.
    */
    private Repository() {

        logger = Logger.getInstance();
        metrics_tracker = MetricsTracker.getInstance();

        logger.log("Repository.new => Instantiated successfully", LogLevels.SUCCESS);
    }

    ///
    /** @return The {@link Repository} instance created during class loading. */
    public static Repository getInstance() {

        return(INSTANCE);
    }

    ///
    /**
     * Inserts the specified entities into the corresponding database table.
     * @param entities : The entities to be persisted.
     * @param descriptor : The entities descriptor.
     * @param connection : The connection to the database.
     * @param commit : The flag used to indicate if the transaction should be committed or not after this method.
     * @return The never {@code null} list of inserted entity keys.
     * @throws IllegalArgumentException If either {@code entities}, {@code descriptor} or {@code connection} are {@code null}.
     * @throws PersistenceException If any database access error occurrs.
     * @apiNote This method assumes that all entities are of the same type.
    */
    public List<Long> insert(

        List<? extends Entity> entities,
        Entities<? extends Enum<?>> descriptor,
        PooledConnection connection,
        boolean commit

    ) throws IllegalArgumentException, PersistenceException {

        if(entities == null || descriptor == null || connection == null) {

            metrics_tracker.incrementDatabaseQueriesKo(1);
            throw new IllegalArgumentException("Repository.insert -> The input arguments cannot be null");
        }

        List<Long> keys = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet result_set = null;

        if(entities.size() == 0) {

            metrics_tracker.incrementDatabaseQueriesOk(1);
            return(keys);
        }

        try {

            statement = prepareInsert(entities, descriptor, connection);
            executeInsertOrUpdate(statement, entities.size());
            conditionalCommit(connection, commit);
        }

        catch(PersistenceException exc) {

            performRollback(connection);

            if(exc instanceof DatabaseConnectionException) {

                connection.refreshConnection();

                if(statement == null) {

                    statement = prepareInsert(entities, descriptor, connection);
                }

                executeInsertOrUpdate(statement, entities.size());
                conditionalCommit(connection, commit);
            }

            else {

                metrics_tracker.incrementDatabaseQueriesKo(1);
                ResourceReleaserInternal.release(logger, "Repository", "insert", statement);

                throw exc;
            }
        }

        try {

            result_set = statement.getGeneratedKeys();

            while(result_set.next()) {

                keys.add(result_set.getLong(1));
            }

            ResourceReleaserInternal.release(logger, "Repository", "insert", statement, result_set);
            metrics_tracker.incrementDatabaseQueriesOk(1);

            return(keys);
        }

        catch(SQLException exc2) {

            metrics_tracker.incrementDatabaseQueriesKo(1);
            ResourceReleaserInternal.release(logger, "Repository", "insert", statement, result_set);

            throw SqlExceptionDecoder.decode("Repository.insert -> Could not extract keys: ", exc2);
        }
    }

    ///..
    /**
     * Fetches the entities from the database filtered by the specified filter.
     * @param filter : The query filter.
     * @param connection : The database connection.
     * @return The never {@code null} list of fetched entities.
     * @throws IllegalArgumentException If either {@code filter}, {@code connection} or {@code mapper} are {@code null}.
     * @throws PersistenceException If any database access error occurrs.
    */
    public <T extends Entity> List<T> select(Filter filter, PooledConnection connection, ResultSetMapper<T> mapper)
    throws IllegalArgumentException, PersistenceException {

        if(filter == null || connection == null || mapper == null) {

            metrics_tracker.incrementDatabaseQueriesKo(1);
            throw new IllegalArgumentException("Repository.select -> The input arguments cannot be null");
        }

        PreparedStatement statement = null;
        ResultSet result_set = null;

        try {

            statement = prepareSelect(filter, connection);
            result_set = executeSelect(statement);
        }

        catch(PersistenceException exc) {

            if(exc instanceof DatabaseConnectionException) {

                connection.refreshConnection();

                if(statement == null) {

                    statement = prepareSelect(filter, connection);
                }

                result_set = executeSelect(statement);
            }

            else {

                metrics_tracker.incrementDatabaseQueriesKo(1);
                ResourceReleaserInternal.release(logger, "Repository", "select", statement);

                throw exc;
            }
        }

        try {

            List<T> entities = mapper.map(result_set);
            ResourceReleaserInternal.release(logger, "Repository", "select", statement, result_set);

            if(entities == null) {

                return(new ArrayList<>());
            }

            metrics_tracker.incrementDatabaseQueriesOk(1);
            return(entities);
        }

        catch(ResultSetMappingException exc3) {

            metrics_tracker.incrementDatabaseQueriesKo(1);
            ResourceReleaserInternal.release(logger, "Repository", "select", statement, result_set);

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
    */
    public void update(

        Entity entity,
        Entities<? extends Enum<?>> descriptor,
        long fields,
        PooledConnection connection,
        boolean commit

    ) throws IllegalArgumentException, PersistenceException {

        if(entity == null || descriptor == null || connection == null) {

            metrics_tracker.incrementDatabaseQueriesKo(1);
            throw new IllegalArgumentException("Repository.update -> The input arguments cannot be null");
        }

        PreparedStatement statement = null;

        try {

            statement = prepareUpdate(entity, descriptor, fields, connection);
            executeInsertOrUpdate(statement, 0);
            conditionalCommit(connection, commit);
        }

        catch(PersistenceException exc) {

            performRollback(connection);

            if(exc instanceof DatabaseConnectionException) {

                connection.refreshConnection();

                if(statement == null) {

                    statement = prepareUpdate(entity, descriptor, fields, connection);
                }

                executeInsertOrUpdate(statement, 0);
                conditionalCommit(connection, commit);
            }

            else {

                metrics_tracker.incrementDatabaseQueriesKo(1);
                ResourceReleaserInternal.release(logger, "Repository", "update", statement);

                throw exc;
            }
        }

        metrics_tracker.incrementDatabaseQueriesOk(1);
        ResourceReleaserInternal.release(logger, "Repository", "update", statement);
    }

    ///..
    /**
     * Removes from the specified database table the recods matching the provided keys.
     * @param keys : The primary keys of the records.
     * @param descriptor : The entity descriptor.
     * @param connection : The connection to the database.
     * @param commit : The flag used to indicate if the transaction should be committed or not.
     * @return The number of deleted entities.
     * @throws IllegalArgumentException If either {@code keys}, {@code descriptor} or {@code connection} are {@code null}.
     * @throws PersistenceException If any database access error occurrs.
    */
    public int delete(List<Object> keys, Entities<? extends Enum<?>> descriptor, PooledConnection connection, boolean commit)
    throws IllegalArgumentException, PersistenceException {

        if(keys == null || descriptor == null || connection == null) {

            metrics_tracker.incrementDatabaseQueriesKo(1);
            throw new IllegalArgumentException("Repository.delete -> The input arguments cannot be null");
        }

        if(keys.size() == 0) {

            return(0);
        }

        int rows_affected;
        PreparedStatement statement = null;

        try {

            statement = prepareDelete(keys, descriptor, connection);
            rows_affected = executeDelete(statement);
            conditionalCommit(connection, commit);
        }

        catch(PersistenceException exc) {

            performRollback(connection);

            if(exc instanceof DatabaseConnectionException) {

                connection.refreshConnection();

                if(statement == null) {

                    statement = prepareDelete(keys, descriptor, connection);
                }

                rows_affected = executeDelete(statement);
                conditionalCommit(connection, commit);
            }

            else {

                metrics_tracker.incrementDatabaseQueriesKo(1);
                ResourceReleaserInternal.release(logger, "Repository", "delete", statement);

                throw exc;
            }
        }

        metrics_tracker.incrementDatabaseQueriesOk(1);
        ResourceReleaserInternal.release(logger, "Repository", "delete", statement);

        return(rows_affected);
    }

    ///.
    /**
     * Generates a JDBC statement for an SQL {@code INSERT} query.
     * @param entities : The entities to be inserted.
     * @param descriptor : The entity descriptor.
     * @param pooled_connection : The database connection.
     * @return The never {@code null} JDBC statement ready to be executed.
     * @throws PersistenceException If any database access error occurs.
    */
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

                "INSERT INTO " + table_name + "(" + columns + ") VALUES(" + placeholders + ")",
                Statement.RETURN_GENERATED_KEYS
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

                throw SqlExceptionDecoder.decode("Repository.prepareInsert -> Could not prepare for insert: ", (SQLException)exc);
            }

            else {

                throw (PersistenceException)exc;
            }
        }
    }

    ///..
    /**
     * Generates a JDBC statement for an SQL {@code SELECT} query.
     * @param filter : The filter used to generate the query.
     * @param pooled_connection : The database connection.
     * @return The never {@code null} JDBC statement ready to be executed.
     * @throws PersistenceException If any database access error occurs.
    */
    private PreparedStatement prepareSelect(Filter filter, PooledConnection pooled_connection) throws PersistenceException {

        try {

            PreparedStatement statement = pooled_connection.getConnection().prepareStatement(filter.generateSelect());

            pooled_connection.getQueryBinder().setStatement(statement);
            pooled_connection.getQueryBinder().resetIndex();
            filter.bindForSelect(pooled_connection.getQueryBinder());

            return(statement);
        }

        catch(SQLException | PersistenceException exc) {

            if(exc instanceof SQLException) {

                throw SqlExceptionDecoder.decode("Repository.prepareSelect -> Could not prepare for select: ", (SQLException)exc);
            }

            else {

                throw (PersistenceException)exc;
            }
        }
    }

    ///..
    /**
     * Generates a JDBC statement for an SQL {@code UPDATE} query.
     * @param entity : The entity to be updated.
     * @param descriptor : The entity descriptor.
     * @param fields : The fields to consider.
     * @param pooled_connection : The database connection.
     * @return The never {@code null} JDBC statement ready to be executed.
     * @throws PersistenceException If any database access error occurs.
    */
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

                throw SqlExceptionDecoder.decode("Repository.prepareUpdate -> Could not prepare for update: ", (SQLException)exc);
            }

            else {

                throw (PersistenceException)exc;
            }
        }
    }

    ///..
    /**
     * Generates a JDBC statement for an SQL {@code DELETE} query.
     * @param keys : The primary keys of the entities to delete.
     * @param descriptor : The entity description.
     * @param pooled_connection : The database connection.
     * @return The never {@code null} JDBC statement ready to be executed.
     * @throws PersistenceException If any database access error occurs.
    */
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

            throw SqlExceptionDecoder.decode("Repository.prepareDelete -> Could not prepare for delete: ", exc);
        }
    }

    ///..
    /**
     * Generates the placeholder string.
     * @param amount : The number of placeholders.
     * @return The never {@code null} string containing comma separated placeholders.
    */
    private String getPlaceholders(int amount) {

        return("?,".repeat(amount).substring(0, (amount * 2) - 1));
    }

    ///..
    /**
     * Conditionally commits the transaction.
     * @param pooled_connection : The database connection.
     * @param commit : {@code true} if commit, {@code false} otherwise.
     * @throws PersistenceException If any database access error occurs.
    */
    private void conditionalCommit(PooledConnection pooled_connection, boolean commit) throws PersistenceException {

        if(commit == true) {

            try {

                pooled_connection.getConnection().commit();
            }

            catch(SQLException exc) {

                throw SqlExceptionDecoder.decode("Repository.conditionalCommit -> Could not commit: ", exc);
            }
        }
    }

    ///..
    /**
     * Performs the transaction rollback on the provided connection.
     * @param pooled_connection : The connection from which to rollback the transaction.
     * @throws PersistenceException If any database access error occurs.
    */
    private void performRollback(PooledConnection pooled_connection) throws PersistenceException {

        try {

            pooled_connection.getConnection().rollback();
        }

        catch(SQLException exc) {

            throw SqlExceptionDecoder.decode("Repository.performRollback -> Could not rollback: ", exc);
        }
    }

    ///..
    /**
     * Executes the SQL {@code INSERT} or {@code UPDATE} query.
     * @param statement : The statement to execute.
     * @param batch_size : The size of the batch.
     * @throws PersistenceException If any database access error occurs.
    */
    private void executeInsertOrUpdate(PreparedStatement statement, int batch_size) throws PersistenceException {

        try {

            if(batch_size > 0) {

                statement.executeBatch();
            }

            else {

                statement.executeUpdate();
            }
        }

        catch(SQLException exc) {

            throw SqlExceptionDecoder.decode("Repository.executeInsertOrUpdate -> Could not execute insert or update: ", exc);
        }
    }

    ///..
    /**
     * Executes the SQL {@code SELECT} query.
     * @param statement : The statement to execute.
     * @throws PersistenceException If any database access error occurs.
    */
    private ResultSet executeSelect(PreparedStatement statement) throws PersistenceException {

        try {

            return(statement.executeQuery());
        }

        catch(SQLException exc) {

            throw SqlExceptionDecoder.decode("Repository.executeSelect -> Could not execute select: ", exc);
        }
    }

    ///..
    /**
     * Executes the SQL {@code DELETE} query.
     * @param statement : The statement to execute.
     * @throws PersistenceException If any database access error occurs.
    */
    private int executeDelete(PreparedStatement statement) throws PersistenceException {

        try {

            return(statement.executeUpdate());
        }

        catch(SQLException exc) {

            throw SqlExceptionDecoder.decode("Repository.executeDelete -> Could not execute delete: ", exc);
        }
    }

    ///
}
