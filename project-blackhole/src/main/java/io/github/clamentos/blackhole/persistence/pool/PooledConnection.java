package io.github.clamentos.blackhole.persistence.pool;

///
import io.github.clamentos.blackhole.persistence.Queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

    public EnumMap<Queries, PreparedStatement> getAssociatedStatements() {

        return(associated_statements);
    }

    ///
}
