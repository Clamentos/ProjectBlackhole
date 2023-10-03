package io.github.clamentos.blackhole.scaffolding;

///
/**
 * <h3>Search filter interface</h3>
 * This interface can be used on search filter DTOs in order to aid caching.
 * The cache can determine which cached read type requests needs to be
 * updated when new entities are created.
*/
public interface Filter {

    /**
     * Check if {@code this} "filters" the entity.
     * 
     * @param entity : the entity to be tested by {@code this}.
     * @return {@code true} if the filter finds (matches) the specified entity.
    */
    boolean isFiltered(Entity entity);

    ///
}