package io.github.clamentos.blackhole.persistence.entities;

public record Edge(

    int source,
    int destination,
    String value,
    int creation_time,
    int last_updated

) {}
