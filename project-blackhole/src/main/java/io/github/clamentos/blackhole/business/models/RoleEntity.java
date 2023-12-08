package io.github.clamentos.blackhole.business.models;

public record RoleEntity(

    short id,
    int creation_date,
    int last_modified,
    String name,
    int flags

) {}
