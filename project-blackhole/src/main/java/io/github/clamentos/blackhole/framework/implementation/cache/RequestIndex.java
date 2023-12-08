package io.github.clamentos.blackhole.framework.implementation.cache;

import io.github.clamentos.blackhole.framework.implementation.configuration.ConfigurationProvider;

///
public class RequestIndex {
    
    ///
    private static final RequestIndex INSTANCE = new RequestIndex();

    private final long CACHE_ENTRY_DURATION;
    private final int CACHE_CAPACITY;

    private RequestIndexEntry[] entries;

    ///
    private RequestIndex() {

        CACHE_ENTRY_DURATION = ConfigurationProvider.getInstance().CACHE_ENTRY_DURATION;
        CACHE_CAPACITY = ConfigurationProvider.getInstance().CACHE_CAPACITY;

        entries = new RequestIndexEntry[CACHE_CAPACITY];
    }

    ///
    /** @return The {@link RequestIndex} instance created during class loading. */
    public static RequestIndex getInstance() {

        return(INSTANCE);
    }

    ///
    public void put(byte[] request_bytes, int[] cache_indexes, int hash_code) {

        entries[hash_code % CACHE_CAPACITY] = new RequestIndexEntry(
            
            request_bytes,
            cache_indexes,
            System.currentTimeMillis() + CACHE_ENTRY_DURATION
        );
    }

    public int[] get(byte[] request_bytes, int hash_code) {

        RequestIndexEntry entry = entries[hash_code % CACHE_CAPACITY];

        if(entry != null) {

            if(entry.isValid() && entry.request_bytes().equals(request_bytes)) {

                return(entry.cache_indexes());
            }
        }

        return(null);
    }
    
    ///
}
