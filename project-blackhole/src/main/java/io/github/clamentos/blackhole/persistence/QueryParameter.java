package io.github.clamentos.blackhole.persistence;

///
import java.sql.JDBCType;

///
/**
 * <h3>Query parameter wrapper</h3>
 * 
 * This simple record class is used as a wrapper class to parameter binding
 * on dynamically generated SQL queries.
*/
public record QueryParameter(

    ///
    JDBCType type,
    Object value

    ///
) {}
