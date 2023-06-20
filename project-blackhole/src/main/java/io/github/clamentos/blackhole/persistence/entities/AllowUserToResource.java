package io.github.clamentos.blackhole.persistence.entities;

public record AllowUserToResource(

    Integer user_id,
    Long resource_id,
    Byte flags

) {}
