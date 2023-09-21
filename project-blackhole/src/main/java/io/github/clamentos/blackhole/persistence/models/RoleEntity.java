package io.github.clamentos.blackhole.persistence.models;

public record RoleEntity(

    short id,
    String name,
    short permission_flags

) {}
