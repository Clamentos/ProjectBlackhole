package io.github.clamentos.blackhole.framework.scaffolding.persistence.model;

///
import io.github.clamentos.blackhole.framework.scaffolding.persistence.cache.Cacheability;

///
/**
 * <h3>Entities</h3>
 * Specifies that the implementing class is an enumeration of database entity types.
 * @param < E > E : The type of database entity.
*/
public interface Entities<E extends Enum<E>> {

    ///
    /** @return The never {@code null} and never empty database table name associated to {@code this} entity. */
    String getTableName();

    ///..
    /** @return The never {@code null} and never empty database column names associated to {@code this} entity. */
    String[] getColumnNames();

    ///..
    /** @return {@code true} if {@code this} entity should be inserted without providing the primary key, {@code false} otherwise. */
    boolean usesAutoKey();

    ///..
    /** @return The never {@code null} cacheability level of {@code this} entity. */
    Cacheability getCacheability();

    ///..
    /**
     * @return The cacheability size limit of {@code this} entity.
     * @apiNote This method will only be used by the framework if the cacheability of {@code this} entity is
     * {@code Cacheability.SIZE_LIMITED}.
    */
    int getCacheabilitySizeLimit();

    ///
}
