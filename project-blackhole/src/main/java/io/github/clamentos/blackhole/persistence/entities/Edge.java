package io.github.clamentos.blackhole.persistence.entities;

/**
 * <p><b>Entity</b></p>
 * Join table between two {@link Resource}.
 * This entity represents the edges in the graph.
*/
public record Edge(

    int source,
    int destination,
    String data,
    int creation_time,
    int last_updated

) {}
