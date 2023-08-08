package io.github.clamentos.blackhole.persistence.pool;

//________________________________________________________________________________________________________________________________________

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//________________________________________________________________________________________________________________________________________

/**
 * <h3>Connection utility</h3>
 * This class offers some simple methods to create and refresh JDBC connections.
 * @apiNote This class is <b>static</b>.
*/
public class ConnectionUtility {

    //____________________________________________________________________________________________________________________________________

    /**
     * @param db_connection : The current {@link Connection} to be tested or refreshed.
     * @param url : The database URL address.
     * @param username : The username for the database.
     * @param password : The password for the database.
     * @return {@code db_connection} if it's valid, a new one if it's not.
     * @throws SQLException If a database connection error or timeout occurs.
    */
    public static Connection refresh(Connection db_connection, String url, String username, String password) throws SQLException {

        if(db_connection.isValid(5) == false) {

            return(create(url, username, password));
        }

        return(db_connection);
    }

    /**
     * @param url : The database URL address.
     * @param username : The username for the database.
     * @param password : The password for the database.
     * @return A new {@link Connection} with the specified parameters.
     * @throws SQLException If a database connection error or timeout occurs.
    */
    protected static Connection create(String url, String username, String password) throws SQLException {

        return(DriverManager.getConnection(url, username, password));
    }

    //____________________________________________________________________________________________________________________________________
}
