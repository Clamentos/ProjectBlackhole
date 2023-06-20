package io.github.clamentos.blackhole.persistence.entities;

/**
 * <p><b>Entity</b></p>
 * Join table between {@link Tag} and {@link Resource}.
*/
public record Categorization(

    Integer tag_id,
    Long resource_id

) {}
