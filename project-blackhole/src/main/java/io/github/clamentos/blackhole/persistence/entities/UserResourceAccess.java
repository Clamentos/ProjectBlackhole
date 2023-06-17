package io.github.clamentos.blackhole.persistence.entities;

public record UserResourceAccess(
    
    Integer user_id,
    Long resource_id,
    Byte flags

) {}
