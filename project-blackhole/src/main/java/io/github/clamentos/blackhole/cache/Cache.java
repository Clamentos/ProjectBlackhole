package io.github.clamentos.blackhole.cache;

import io.github.clamentos.blackhole.network.transfer.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// sync issues?
public class Cache {
    
    private static final Cache INSTANCE = new Cache();
    private final int MAX_CACHE_CAPACITY = 65536;

    private Map<Integer, CacheEntry> indices;
    private List<CachedItem> cache;
    private int size;

    private Cache() {

        indices = new ConcurrentHashMap<>();
        cache = new ArrayList<>();
        size = 0;
    }

    public static Cache getInstance() {

        return(INSTANCE);
    }

    public List<CachedItem> read(Request key) {

        List<CachedItem> result;
        int[] ids = indices.get(key.hashCode()).getids();

        if(ids != null) { // Hit

            result = new ArrayList<>();

            for(int i = 0; i < ids.length; i++) {

                CachedItem temp = cache.get(ids[i]);                
            }
        }
    }
}