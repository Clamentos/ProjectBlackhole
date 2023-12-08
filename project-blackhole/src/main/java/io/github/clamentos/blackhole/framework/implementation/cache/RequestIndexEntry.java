package io.github.clamentos.blackhole.framework.implementation.cache;

public record RequestIndexEntry(

    byte[] request_bytes,
    int[] cache_indexes,
    long expires_at

) {

    public boolean isValid() {

        return(System.currentTimeMillis() < expires_at);
    }
}
