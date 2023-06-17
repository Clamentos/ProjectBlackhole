package io.github.clamentos.blackhole.persistence.entities;

public record RoleResourceAccess(

    Short role_id,
    Long resource_id,
    Byte flags
) {}
