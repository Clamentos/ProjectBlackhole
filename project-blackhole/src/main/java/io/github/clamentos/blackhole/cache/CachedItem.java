package io.github.clamentos.blackhole.cache;

public record CachedItem(

    long id,
    Object data

) {}
// TODO: implement comparable