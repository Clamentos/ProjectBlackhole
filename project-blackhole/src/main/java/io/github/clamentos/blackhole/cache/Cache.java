package io.github.clamentos.blackhole.cache;

///
import io.github.clamentos.blackhole.configuration.ConfigurationProvider;
import java.util.List;

///
// TODO: needs to be completely redone
public class Cache {
    
    ///
    private static final Cache INSTANCE = new Cache();

    private final int CACHE_CAPACITY;
    
    private RequestIndex request_index;
    private Object[] objects_cache;

    ///
    private Cache() {

        CACHE_CAPACITY = ConfigurationProvider.getInstance().CACHE_CAPACITY;
    
        request_index = RequestIndex.getInstance();
        objects_cache = new Object[CACHE_CAPACITY];
    }

    ///
    /** @return The {@link Cache} instance created during class loading. */
    public static Cache getInstance() {

        return(INSTANCE);
    }

    ///
    public void put(byte[] request_bytes, List<Object> objects, int hash_code) {

        int[] indexes = new int[objects.size()];

        for(int i = 0; i < objects.size(); i++) {

            int obj_hash = objects.get(i).hashCode() % CACHE_CAPACITY;
            objects_cache[obj_hash] = objects.get(i);
            indexes[i] = obj_hash;
        }

        request_index.put(request_bytes, indexes, hash_code);
    }

    ///
    public Object[] get(byte[] request_bytes, int hash_code) {

        Object[] cached_objects;
        int[] indexes = request_index.get(request_bytes, hash_code);

        if(indexes != null) {

            cached_objects = new Object[indexes.length];

            for(int i = 0; i < indexes.length; i++) {

                cached_objects[i] = objects_cache[indexes[i]];
            }

            return(cached_objects);
        }

        return(null);
    }

    ///
    public void update(int[] indexes, List<Object> updated_objects) {

        for(int i = 0; i < indexes.length; i++) {

            objects_cache[indexes[i] % CACHE_CAPACITY] = updated_objects.get(i);
        }
    }

    ///
}