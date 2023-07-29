package io.github.clamentos.blackhole.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

// TODO: finish
public class Repository {
    
    private static final Repository INSTANCE = new Repository();
    private ConnectionPool connection_pool;

    private Repository() {

        connection_pool = ConnectionPool.getInstance();
    }

    public static Repository getInstance() {

        return(INSTANCE);
    }

    public void insert(String query, List<List<QueryParameter>> parameters) {

        Connection connection = connection_pool.aquireConnection();
        
        try {

            PreparedStatement statement = connection.prepareStatement(query);

            for(int i = 0; i < parameters.size(); i++) {

                for(int j = 0; j <= parameters.get(i).size(); j++) {

                    setParameter(statement, j + 1, parameters.get(i).get(j));
                }

                statement.addBatch();
            }

            statement.executeBatch();
        }

        catch(SQLException exc) {

            //TODO: handle exc
        }
    }

    private void setParameter(PreparedStatement statement, int idx, QueryParameter parameter) throws SQLException {

        switch(parameter.type()) {

            case BYTE: statement.setByte(idx, (byte)parameter.parameter()); break;
            case SHORT: statement.setShort(idx, (short)parameter.parameter()); break;
            case INT: statement.setInt(idx, (int)parameter.parameter()); break;
            case LONG: statement.setLong(idx, (long)parameter.parameter()); break;
            case FLOAT: statement.setFloat(idx, (float)parameter.parameter()); break;
            case DOUBLE: statement.setDouble(idx, (double)parameter.parameter()); break;
            case STRING: statement.setString(idx, (String)parameter.parameter()); break;
        }
    }
}
