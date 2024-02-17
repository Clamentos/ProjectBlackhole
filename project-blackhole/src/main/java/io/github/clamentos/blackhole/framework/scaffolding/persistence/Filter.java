package io.github.clamentos.blackhole.framework.scaffolding.persistence;

///
/**
 * <h3>Filter</h3>
 * Specifies that the implementing class can generate SQL {@code SELECT} clauses as well as aid caching.
 * @see Entity
 * @see QueryBinder
 * @see Entities
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
     * @param query_binder : The statement binder to bind the parameters on.
     * @see QueryBinder
    */
    void bindForSelect(QueryBinder query_binder);

    ///..
    /**
     * Checks if {@code this} filters the target entity.
     * @param entity : The entity to be tested by {@code this} filter.
     * @return {@code true} if the filter matches the specified entity, {@code false} otherwise.
     * @see Entity
    */
    boolean isFiltered(Entity entity);

    ///..
    /**
     * @return The never {@code null} entity type filtered by {@code this}.
     * @see Entities
    */
    Entities<? extends Enum<?>> getFilteredEntityType();

    ///
}
