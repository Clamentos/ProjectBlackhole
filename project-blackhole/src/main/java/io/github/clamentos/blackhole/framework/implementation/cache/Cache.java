package io.github.clamentos.blackhole.framework.implementation.cache;

import io.github.clamentos.blackhole.framework.scaffolding.persistence.Entities;
///
import io.github.clamentos.blackhole.framework.scaffolding.persistence.Entity;
import io.github.clamentos.blackhole.framework.scaffolding.persistence.Filter;

///.
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

///..
import java.util.concurrent.ConcurrentHashMap;

///
// TODO: finish
public final class Cache {

    ///
    /** Singleton instance of {@code this} instantiated during class loading. */
    private static final Cache INSTANCE = new Cache();

    ///.
    private final Map<Entities<? extends Enum<?>>, Map<Long, Entity>> entities;
    private final Map<Filter, Set<Long>> filter_map;

    ///
    private Cache() {

        filter_map = new ConcurrentHashMap<>();
        entities = new ConcurrentHashMap<>();
    }

    ///
    public static Cache getInstance() {

        return(INSTANCE);
    }

    ///
    public List<Entity> get(Filter filter) {

        List<Entity> result = null;
        Set<Long> ids = filter_map.get(filter);

        if(ids != null) {

            result = new ArrayList<>();

            for(Long id : ids) {

                result.add(entities.get(filter.getFilteredEntityType()).get(id));
            }
        }

        return(result);
    }

    ///..
    public void update(Map<Long, Entity> modified_entities, int entity_type) {

        for(Map.Entry<Long, Entity> entry : modified_entities.entrySet()) {

            entities.get(entity_type).put(entry.getKey(), entry.getValue());

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

    ///
}
