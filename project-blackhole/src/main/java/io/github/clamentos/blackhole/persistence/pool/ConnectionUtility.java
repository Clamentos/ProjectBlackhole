package io.github.clamentos.blackhole.persistence.pool;

///
import io.github.clamentos.blackhole.persistence.PersistenceException;
import io.github.clamentos.blackhole.persistence.Queries;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.EnumMap;

///
/**
 * <h3>Connection utility</h3>
 * This class offers some simple methods to create and refresh JDBC connections.
 * @apiNote This class is <b>static</b>.
*/
public class ConnectionUtility {

    ///
    /**
     * @param db_connection : The current {@link PooledConnection} to be tested or refreshed.
     * @param url : The database URL address.
     * @param username : The username for the database.
     * @param password : The password for the database.
     * @return {@code db_connection} if it's valid, a new one if it's not.
     * @throws PersistenceException If a database connection error or timeout occurs.
    */
    protected static PooledConnection refresh(PooledConnection db_connection, String url, String username, String password) throws PersistenceException {

        try {

            // 5 sec of latency, above which the method will timeout and the connection will be considered as invalid.
            if(db_connection.getDbConnection().isValid(5) == false) {

                close(db_connection);
                return(create(url, username, password));
            }
        }

        catch(SQLException exc) {

            throw new PersistenceException(exc);
        }

        return(db_connection);
    }

    private static void close(PooledConnection db_connection) {

        EnumMap<Queries, PreparedStatement> statements = db_connection.getAssociatedStatements();

        for(PreparedStatement statement : statements.values()) {

            try {

                statement.close();
            }

            catch(SQLException exc) {

                //...
            }
        }

        try {

            db_connection.getDbConnection().close();
        }

        catch(SQLException exc) {

            //...
        }
    }

    /**
     * @param url : The database URL address.
     * @param username : The username for the database.
     * @param password : The password for the database.
     * @return A new {@link PooledConnection} with the specified parameters.
     * @throws PersistenceException If a database connection error or timeout occurs.
    */
    protected static PooledConnection create(String url, String username, String password) throws PersistenceException {

        EnumMap<Queries, PreparedStatement> statements = new EnumMap<>(Queries.class);

        try {

            Connection db_connection = DriverManager.getConnection(url, username, password);

            for(Queries query : Queries.values()) {

                statements.putIfAbsent(query, db_connection.prepareStatement(query.getQuery()));
            }

            return(new PooledConnection(db_connection, statements));
        }

        catch(SQLException exc) {

            throw new PersistenceException(exc);
        }
    }

    ///
}
