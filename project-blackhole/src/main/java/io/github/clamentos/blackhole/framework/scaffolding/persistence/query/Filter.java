package io.github.clamentos.blackhole.framework.scaffolding.persistence.query;

///
import io.github.clamentos.blackhole.framework.scaffolding.persistence.model.Entities;
import io.github.clamentos.blackhole.framework.scaffolding.persistence.model.Entity;

///
/**
 * <h3>Filter</h3>
 * Specifies that the implementing class can generate SQL {@code SELECT} clauses as well as aid caching.
*/
public interface Filter {

    ///
    /**
     * Generates the SQL {@code SELECT} statement that describes {@code this} filter.
     * @return The never {@code null} and never empty query string.
    */
    String generateSelect();

    ///..
    /**
     * Binds the field values of {@code this} filter into the given statement for a {@code SELECT} query.
     * @param binder : The statement binder to bind the parameters on.
    */
    void bindForSelect(QueryBinder binder);

    ///..
    /**
     * Checks if {@code this} filters the target entity.
     * @param entity : The entity to be tested by {@code this} filter.
     * @return {@code true} if the filter matches the specified entity, {@code false} otherwise.
    */
    boolean isFiltered(Entity entity);

    ///..
    /** @return The never {@code null} entity type filtered by {@code this}. */
    Entities<? extends Enum<?>> getFilteredEntityType();

    ///..
    /** {@inheritDoc} */
    boolean equals(Object target);

    ///..
    /** {@inheritDoc} */
    int hashCode();

    ///
}
