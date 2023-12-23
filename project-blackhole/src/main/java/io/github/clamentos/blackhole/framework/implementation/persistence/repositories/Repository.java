package io.github.clamentos.blackhole.framework.implementation.persistence.repositories;

///
import io.github.clamentos.blackhole.framework.implementation.logging.Logger;

///..
import io.github.clamentos.blackhole.framework.implementation.persistence.PersistenceException;

///..
import io.github.clamentos.blackhole.framework.implementation.persistence.pool.PooledConnection;

///..
import io.github.clamentos.blackhole.framework.implementation.utility.ResourceReleaser;

///..
import io.github.clamentos.blackhole.framework.scaffolding.persistence.Entity;
import io.github.clamentos.blackhole.framework.scaffolding.persistence.Filter;

///.
import java.sql.Connection;
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
public final class Repository {

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
     * @param connection : The connection to the database.
     * @throws PersistenceException If any database access error occurrs.
     * @throws IllegalArgumentException If either {@code entities}, {@code sql} or {@code connection} are {@code null}.
     * @see Entity
     * @see PooledConnection
    */
    public void insert(List<? extends Entity> entities, PooledConnection connection, boolean commit) throws PersistenceException, IllegalArgumentException {

        if(entities == null || connection == null) {

            throw new IllegalArgumentException("(Repository.insert) -> The input arguments cannot be null");
        }

        PreparedStatement statement = null;

        try {

            if(entities.size() == 0) {

                conditionalCommit(connection, commit);
                return;
            }

            statement = prepareInsert(entities, connection.getConnection());
            statement.executeUpdate();

            conditionalCommit(connection, commit);
        }

        catch(SQLException exc) {

            try {

                // Immediately rollback.
                connection.getConnection().rollback();

                // If the SQL error state was caused by a network / connection problem -> refresh & retry.
                if(exc.getSQLState().substring(0, 2).equals("08")) {

                    connection.refreshConnection();

                    if(statement == null) {

                        statement = prepareInsert(entities, connection.getConnection());
                    }

                    statement.executeUpdate();
                    conditionalCommit(connection, commit);
                }

                else {

                    // The exception was not caused by a network / connection problem -> cannot recover...
                    throw new PersistenceException(exc);
                }
            }

            catch(SQLException exc2) {

                // Failed while retrying, give up...
                throw new PersistenceException(exc2);
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
     * @throws PersistenceException If any database access error occurrs.
     * @throws IllegalArgumentException If either {@code filter} or {@code connection} are {@code null}.
     * @see Filter
     * @see PooledConnection
    */
    //TODO: release statement
    public ResultSet select(Filter filter, PooledConnection connection) throws PersistenceException, IllegalArgumentException {

        if(filter == null || connection == null) {

            throw new IllegalArgumentException("(Repository.select) -> The input arguments cannot be null");
        }

        PreparedStatement statement = null;

        try {

            statement = filter.generateSelect(connection.getConnection());
            return(statement.executeQuery());
        }

        catch(SQLException exc) {

            try {

                // Immediately rollback.
                connection.getConnection().rollback();

                // If the SQL error state was caused by a network / connection problem -> refresh & retry.
                if(exc.getSQLState().substring(0, 2).equals("08")) {

                    connection.refreshConnection();

                    if(statement == null) {

                        statement = filter.generateSelect(connection.getConnection());
                    }

                    return(statement.executeQuery());
                }

                else {

                    // The exception was not caused by a network / connection problem -> cannot recover...
                    throw new PersistenceException(exc);
                }
            }

            catch(SQLException exc2) {

                // Failed while retrying, give up...
                throw new PersistenceException(exc2);
            }
        }
    }

    ///..
    /**
     * Updates the database table associated to the provided entity.
     * @param entity : The entity to be persisted.
     * @param connection : The connection to the database.
     * @param fields : The fields to update in the table.
     * This parameter works as a checklist starting from the first field which maps to least significant bit.
     * @throws PersistenceException If any database access error occurrs.
     * @throws IllegalArgumentException If either {@code entities} or {@code connection} are {@code null}.
     * @see Entity
     * @see PooledConnection
    */
    public void update(Entity entity, long fields, PooledConnection connection) throws PersistenceException, IllegalArgumentException {

        if(entity == null || connection == null) {

            throw new IllegalArgumentException("(Repository.update) -> The input arguments cannot be null");
        }

        PreparedStatement statement = null;

        try {

            statement = prepareUpdate(entity, fields, connection.getConnection());
            statement.executeUpdate();
        }

        catch(SQLException exc) {

            try {

                // Immediately rollback.
                connection.getConnection().rollback();

                // If the SQL error state was caused by a network / connection problem -> refresh & retry.
                if(exc.getSQLState().substring(0, 2).equals("08")) {

                    connection.refreshConnection();

                    if(statement == null) {

                        statement = prepareUpdate(entity, fields, connection.getConnection());
                    }

                    statement.executeUpdate();
                }

                else {

                    // The exception was not caused by a network / connection problem -> cannot recover...
                    throw new PersistenceException(exc);
                }
            }

            catch(SQLException exc2) {

                // Failed while retrying, give up...
                throw new PersistenceException(exc2);
            }
        }

        ResourceReleaser.release(logger, "Repository.update", statement);
    }

    ///..
    /**
     * Removes from the specified database table the recods matching the provided keys.
     * @param keys : The primary keys of the records.
     * @param table_name : The name of the target table.
     * @param key_column_name : The name of the primary key column.
     * @param connection : The connection to the database.
     * @throws PersistenceException If any database access error occurrs.
     * @throws IllegalArgumentException If either {@code keys} or {@code connection} are {@code null}.
     * @see PooledConnection
    */
    public void delete(List<Object> keys, String table_name, String key_column_name, PooledConnection connection) throws PersistenceException, IllegalArgumentException {

        if(keys == null || connection == null) {

            throw new IllegalArgumentException("(Repository.delete) -> The input arguments cannot be null");
        }

        if(keys.size() == 0) {

            return;
        }

        PreparedStatement statement = null;

        try {

            statement = prepareDelete(keys, table_name, key_column_name, connection.getConnection());
            statement.executeUpdate();
        }

        catch(SQLException exc) {

            try {

                // Immediately rollback.
                connection.getConnection().rollback();

                // If the SQL error state was caused by a network / connection problem -> refresh & retry.
                if(exc.getSQLState().substring(0, 2).equals("08")) {

                    connection.refreshConnection();

                    if(statement == null) {

                        statement = prepareDelete(keys, table_name, key_column_name, connection.getConnection());
                    }

                    statement.executeUpdate();
                }

                else {

                    // The exception was not caused by a network / connection problem -> cannot recover...
                    throw new PersistenceException(exc);
                }
            }

            catch(SQLException exc2) {

                // Failed while retrying, give up...
                throw new PersistenceException(exc2);
            }
        }

        ResourceReleaser.release(logger, "Repository.delete", statement);
    }

    ///.
    private PreparedStatement prepareInsert(List<? extends Entity> entities, Connection connection) throws SQLException {

        StringBuilder columns = new StringBuilder();
        List<String> column_names = entities.get(0).getColumnNames();
        String table_name = entities.get(0).getTableName();
        boolean auto_key = entities.get(0).usesAutoKey();

        for(int i = (auto_key ? 1 : 0); i < column_names.size(); i++) {

            columns.append(column_names.get(i) + ",");
        }

        columns.deleteCharAt(columns.length() - 1);
        String placeholders = getPlaceholders(auto_key ? column_names.size() - 1 : column_names.size());

        PreparedStatement statement = connection.prepareStatement(

            "INSERT INTO " + table_name + "(" + columns + ") VALUES(" + placeholders + ")"
        );

        for(Entity entity : entities) {

            entity.bindForInsert(statement);

            if(entities.size() > 1) {

                statement.addBatch();
            }
        }

        return(statement);
    }

    ///..
    private PreparedStatement prepareUpdate(Entity entity, long fields, Connection connection) throws SQLException {

        // Generate the query.
        StringBuilder columns = new StringBuilder();
        List<String> column_names = entity.getColumnNames();

        for(int i = 1; i < column_names.size(); i++) {

            if((fields & (1 << i)) > 0) {

                columns.append(column_names.get(i) + "=?,");
            }
        }

        columns.deleteCharAt(columns.length() - 1);

        // Prepare the statement with the generated INSERT query.
        PreparedStatement statement = connection.prepareStatement(

            "UPDATE " + entity.getTableName() + " SET " + columns + " WHERE " + column_names.get(0) + "=?"
        );

        entity.bindForUpdate(statement, fields);
        return(statement);
    }

    ///..
    private PreparedStatement prepareDelete(List<Object> keys, String table_name, String key_column_name, Connection connection) throws SQLException {

        String placeholders = getPlaceholders(keys.size());

        PreparedStatement statement = connection.prepareStatement(

            "DELETE FROM " + table_name + "WHERE " + key_column_name + " IN(" + placeholders + ")"
        );

        for(int i = 0; i < keys.size(); i++) {

            statement.setObject(i + 1, keys.get(i));
        }

        return(statement);
    }

    ///..
    private String getPlaceholders(int amount) {

        return("?,".repeat(amount).substring(0, (amount * 2) - 1));
    }

    ///..
    private void conditionalCommit(PooledConnection connection, boolean commit) throws SQLException {

        if(commit == true) {

            connection.getConnection().commit();
        }
    }

    ///
}
