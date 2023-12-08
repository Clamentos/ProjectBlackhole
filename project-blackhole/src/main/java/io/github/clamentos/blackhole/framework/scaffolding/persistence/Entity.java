package io.github.clamentos.blackhole.framework.scaffolding.persistence;

///
import java.sql.PreparedStatement;
import java.sql.SQLException;

///..
import java.util.List;

///
/**
 * <h3>Entity</h3>
 * Specifies that the implementing class is a database entity.
*/
public interface Entity {

    ///
    /** @return The never {@code null} and never empty database table name associated to {@code this} entity. */
    String getTableName();
    
    ///..
    /**
     * @return <p>The never {@code null} list of database column names associated to {@code this} entity.</p>
     * For convention, the primary key should always be the first in the list.
    */
    List<String> getColumnNames();

    ///..
    /** @return {@code true} if {@code this} entity should be inserted without providing the primary key, {@code false} otherwise. */
    boolean usesAutoKey();
    
    ///..
    /**
     * Binds the field values of {@code this} into the given prepared-statement for an {@code INSERT} query.
     * @param statement : The prepared-statement to bind the parameters on.
     * @throws SQLException If any database access error occurs.
    */
    void bindForInsert(PreparedStatement statement) throws SQLException;

    ///..
    /**
     * Binds the field values of {@code this} into the given prepared-statement for an {@code UPDATE} query.
     * @param statement : The prepared-statement to bind the parameters on.
     * @param fields : The fields to consider (excluding the primary key). This parameter works as a checklist
     * starting from the first field which maps to least significant bit.
     * @throws SQLException If any database access error occurs.
    */
    void bindForUpdate(PreparedStatement statement, long fields) throws SQLException;

    ///
}
