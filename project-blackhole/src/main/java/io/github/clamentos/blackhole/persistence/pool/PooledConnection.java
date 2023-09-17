package io.github.clamentos.blackhole.persistence.pool;

///
import io.github.clamentos.blackhole.persistence.Queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.EnumMap;

///
public class PooledConnection {
    
    ///
    private Connection db_connection;
    private EnumMap<Queries, PreparedStatement> associated_statements;

    ///
    public PooledConnection(Connection db_connection, EnumMap<Queries, PreparedStatement> associated_statements) {

        this.db_connection = db_connection;
        this.associated_statements = associated_statements;
    }

    ///
    public Connection getDbConnection() {

        return(db_connection);
    }

    /** This method automatically clears any parameter or batch for the desired statement. */
    public PreparedStatement getAssociatedStatement(Queries associated_query) throws SQLException {

        PreparedStatement statement = associated_statements.get(associated_query);

        statement.clearBatch();
        statement.clearParameters();

        return(statement);
    }

    ///
    protected EnumMap<Queries, PreparedStatement> getAssociatedStatements() {

        return(associated_statements);
    }

    ///
}
