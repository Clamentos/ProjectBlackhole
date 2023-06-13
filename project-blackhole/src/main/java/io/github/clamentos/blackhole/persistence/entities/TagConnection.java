package io.github.clamentos.blackhole.persistence.entities;

/**
 * <p><b>Entity</b></p>
 * Join table between {@link Tag} and {@link Resource}.
*/
public record TagConnection(

    int tag_id,
    long resource_id

) {}
