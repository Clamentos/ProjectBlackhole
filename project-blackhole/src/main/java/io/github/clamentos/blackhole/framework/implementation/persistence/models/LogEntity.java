package io.github.clamentos.blackhole.framework.implementation.persistence.models;

///
import io.github.clamentos.blackhole.framework.implementation.logging.Log;
import io.github.clamentos.blackhole.framework.implementation.logging.MetricsTask;

///..
import io.github.clamentos.blackhole.framework.scaffolding.persistence.Entity;

///.
import java.sql.PreparedStatement;
import java.sql.SQLException;

///..
import java.util.List;

///
/**
 * <h3>Log entity</h3>
 * Database log entity. Corresponds to the {@code Logs} table.
 * @see Log
 * @see MetricsTask
*/
public final record LogEntity(

    ///
    /** The primary key of {@code this}. */
    long id,

    /** The log object identifier. */
    long log_id,

    /** The instantiation timestamp of the log object. */
    long creation_date,

    /** The severity of the log object. */
    String log_level,

    /** The message of the log object. */
    String message

    ///
) implements Entity {

    ///
    /** {@inheritDoc} */
    @Override
    public String getTableName() {

        return("\"Logs\"");
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public List<String> getColumnNames() {

        return(List.of(

            "\"id\"",
            "\"log_id\"",
            "\"creation_date\"",
            "\"log_level\"",
            "\"message\""
        ));
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public boolean usesAutoKey() {

        return(true);
    }
    
    ///..
    /** {@inheritDoc} */
    @Override
    public void bindForInsert(PreparedStatement statement) throws SQLException {

        statement.setLong(1, log_id);
        statement.setLong(2, creation_date);
        statement.setString(3, log_level);
        statement.setString(4, message);
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public void bindForUpdate(PreparedStatement statement, long fields) throws SQLException {

        int idx = 1;

        if((fields & 0b000010) > 0) statement.setLong(idx++, log_id);
        if((fields & 0b000100) > 0) statement.setLong(idx++, creation_date);
        if((fields & 0b001000) > 0) statement.setString(idx++, log_level);
        if((fields & 0b010000) > 0) statement.setString(idx++, message);

        statement.setLong(idx, id);
    }

    ///..
    /** {@inheritDoc} */
    @Override
    public boolean cacheable() {

        return(false);
    }

    ///
}
