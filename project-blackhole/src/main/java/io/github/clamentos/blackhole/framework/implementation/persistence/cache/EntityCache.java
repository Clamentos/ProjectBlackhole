package io.github.clamentos.blackhole.framework.implementation.persistence.cache;

import io.github.clamentos.blackhole.framework.scaffolding.persistence.model.Entities;
import io.github.clamentos.blackhole.framework.scaffolding.persistence.model.Entity;
import io.github.clamentos.blackhole.framework.scaffolding.persistence.query.Filter;

///.
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

///..
import java.util.concurrent.ConcurrentHashMap;

///
// TODO: finish
public final class EntityCache {

    ///
    /** Singleton instance of {@code this} instantiated during class loading. */
    private static final EntityCache INSTANCE = new EntityCache();

    ///.
    private final Map<Entities<? extends Enum<?>>, Map<Long, Entity>> caches;
    private final Map<Filter, Set<Long>> filter_map;

    ///
    private EntityCache() {

        filter_map = new ConcurrentHashMap<>();
        caches = new ConcurrentHashMap<>();
    }

    ///
    public static EntityCache getInstance() {

        return(INSTANCE);
    }

    ///
    public List<Entity> get(Filter filter) {

        List<Entity> result = null;
        Set<Long> ids = filter_map.get(filter);

        if(ids != null) {

            result = new ArrayList<>();

            for(Long id : ids) {

                result.add(caches.get(filter.getFilteredEntityType()).get(id));
            }
        }

        return(result);
    }

    ///..
    public void update(Map<Long, Entity> modified_entities, Entities<? extends Enum<?>> entity_type) {

        for(Map.Entry<Long, Entity> entry : modified_entities.entrySet()) {

            caches.get(entity_type).put(entry.getKey(), entry.getValue());

            for(Map.Entry<Filter, Set<Long>> entry2 : filter_map.entrySet()) {

                if(entry2.getKey().isFiltered(entry.getValue()) == true) {

                    entry2.getValue().add(entry.getKey());
                }

                else {

                    entry2.getValue().remove(entry.getKey());
                }
            }
        }
    }

    ///..
    public void delete(List<Long> ids, Entities<? extends Enum<?>> entity_type) {

        Map<Long, Entity> sub_cache = caches.get(entity_type);

        if(sub_cache != null) {

            for(Long id : ids) {

                sub_cache.remove(id);

                for(Map.Entry<Filter, Set<Long>> filters : filter_map.entrySet()) {

                    if(filters.getKey().getFilteredEntityType().equals(entity_type) == true) {

                        if(filters.getValue() != null) {

                            filters.getValue().remove(id);
                        }
                    }
                }
            }
        }
    }

    ///
}
