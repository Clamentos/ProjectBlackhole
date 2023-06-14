package io.github.clamentos.blackhole.persistence.entities;

/**
 * <p><b>Entity</b></p>
 * Join table between two {@link Resource}.
 * This entity represents the edges in the graph.
*/
public record Edge(

    Integer source,
    Integer destination,
    String data,
    Integer creation_time,
    Integer last_updated

) {}
