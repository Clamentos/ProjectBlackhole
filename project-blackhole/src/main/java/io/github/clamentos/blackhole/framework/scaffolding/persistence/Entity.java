package io.github.clamentos.blackhole.framework.scaffolding.persistence;

///
import io.github.clamentos.blackhole.framework.scaffolding.cache.Cacheability;

///..
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
     * For convention, the primary key should always be the first element in the list.
    */
    List<String> getColumnNames();

    ///..
    /** @return {@code true} if {@code this} entity should be inserted without providing the primary key, {@code false} otherwise. */
    boolean usesAutoKey();
    
    ///..
    /**
     * Binds the field values of {@code this} entity into the given statement for an {@code INSERT} query.
     * @param statement : The JDBC statement to bind the parameters on.
     * @throws SQLException If any database access error occurs.
    */
    void bindForInsert(PreparedStatement statement) throws SQLException;

    ///..
    /**
     * Binds the field values of {@code this} entity into the given statement for an {@code UPDATE} query.
     * @param statement : The JDBC statement to bind the parameters on.
     * @param fields : The fields to consider. This parameter works as a checklist
     * starting from the first field which maps to least significant bit.
     * @throws SQLException If any database access error occurs.
    */
    void bindForUpdate(PreparedStatement statement, long fields) throws SQLException;

    ///..
    /**
     * @return The never {@code null} cacheability level of {@code this} entity.
     * @see Cacheability
    */
    Cacheability cacheable();

    ///..
    /**
     * @return The cacheability size limit of {@code this} entity.
     * This method will only be used by the framework if the cacheability of {@code this} entity is {@code Cacheability.SIZE_LIMITED}.
    */
    int getCacheabilitySizeLimit();

    ///
}
