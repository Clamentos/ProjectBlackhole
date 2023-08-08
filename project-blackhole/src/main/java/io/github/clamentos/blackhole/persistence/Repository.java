package io.github.clamentos.blackhole.persistence;

//________________________________________________________________________________________________________________________________________

import io.github.clamentos.blackhole.logging.LogLevel;
import io.github.clamentos.blackhole.logging.Logger;
import io.github.clamentos.blackhole.persistence.pool.ConnectionPool;

import java.io.InputStream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;

//________________________________________________________________________________________________________________________________________

// TODO: finish
public class Repository {
    
    private static final Repository INSTANCE = new Repository();

    private Logger logger;
    private ConnectionPool connection_pool;

    //____________________________________________________________________________________________________________________________________

    private Repository() {

        logger = Logger.getInstance();
        connection_pool = ConnectionPool.getInstance();

        logger.log("Repository.new > Instantiation Successfull", LogLevel.SUCCESS);
    }

    //____________________________________________________________________________________________________________________________________

    public static Repository getInstance() {

        return(INSTANCE);
    }

    //____________________________________________________________________________________________________________________________________

    public String getPlaceholders(int num) {

        String result = "(";

        for(int i = 0; i < num; i++) {

            result += "?";
            if(i < num - 1) result += ",";
        }

        return(result + ")");
    }

    public void insert(String query, List<List<QueryParameter>> parameters) throws PersistenceException {

        Connection connection;
        PreparedStatement statement;

        connection = connection_pool.aquireConnection();

        try {

            statement = connection.prepareStatement(query);

            for(int i = 0; i < parameters.size(); i++) {

                for(int j = 0; j <= parameters.get(i).size(); j++) {

                    setParameter(statement, j + 1, parameters.get(i).get(j));
                }

                statement.addBatch();
            }

            statement.executeBatch();
            connection_pool.releaseConnection(connection);
        }

        catch(SQLException exc) {

            connection_pool.releaseConnection(connection);
            throw new PersistenceException(exc);
        }
    }

    public ResultSet select(String query, List<QueryParameter> parameters) throws PersistenceException {

        Connection connection;
        PreparedStatement statement;

        connection = connection_pool.aquireConnection();

        try {

            statement = connection.prepareStatement(query);

            for(int i = 0; i < parameters.size(); i++) {

                setParameter(statement, i + 1, parameters.get(i));
            }

            return(statement.executeQuery());
        }

        catch(SQLException exc) {

            connection_pool.releaseConnection(connection);
            throw new PersistenceException(exc);
        }
    }

    //...

    //____________________________________________________________________________________________________________________________________

    private void setParameter(PreparedStatement statement, int idx, QueryParameter parameter) throws SQLException {

        switch(parameter.type()) {

            case BYTE: statement.setByte(idx, (byte)parameter.parameter()); break;
            case SHORT: statement.setShort(idx, (short)parameter.parameter()); break;
            case INT: statement.setInt(idx, (int)parameter.parameter()); break;
            case LONG: statement.setLong(idx, (long)parameter.parameter()); break;
            case FLOAT: statement.setFloat(idx, (float)parameter.parameter()); break;
            case DOUBLE: statement.setDouble(idx, (double)parameter.parameter()); break;
            case STRING: statement.setString(idx, (String)parameter.parameter()); break;
            case BLOB: statement.setBlob(idx, (InputStream)parameter.parameter()); break;
        }
    }

    //____________________________________________________________________________________________________________________________________
}
