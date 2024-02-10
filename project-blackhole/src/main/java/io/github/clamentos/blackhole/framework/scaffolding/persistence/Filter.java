package io.github.clamentos.blackhole.framework.scaffolding.persistence;

///
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

///
/**
 * <h3>Filter</h3>
 * Specifies that the implementing class can generate SQL {@code SELECT} clauses as well as aid caching.
 * @see Entity
*/
public interface Filter {

    ///
    /**
     * Generates the SQL {@code SELECT} statement that describes {@code this} filter.
     * @param connection : The JDBC connection from which to generate the statement.
     * @return The never {@code null} JDBC statement to be executed.
     * @throws SQLException If any database access error occurs.
    */
    PreparedStatement generateSelect(Connection connection) throws SQLException;

    ///..
    /**
     * Checks if {@code this} filters the target entity.
     * @param entity : The entity to be tested by {@code this} filter.
     * @return {@code true} if the filter matches the specified entity, {@code false} otherwise.
     * @see Entity
    */
    boolean isFiltered(Entity entity);

    ///
}
